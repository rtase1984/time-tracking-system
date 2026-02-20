package com.timetracking.billing.service;

import com.timetracking.billing.domain.document.Invoice;
import com.timetracking.billing.domain.document.InvoiceItem;
import com.timetracking.billing.domain.document.InvoiceStatus;
import com.timetracking.billing.domain.document.Period;
import com.timetracking.billing.domain.dto.InvoiceResponse;
import com.timetracking.billing.repository.InvoiceRepository;
import com.timetracking.billing.exception.BusinessException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.timetracking.common.event.InvoiceGeneratedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

  private final InvoiceRepository invoiceRepository;
  private final KafkaTemplate<String, InvoiceGeneratedEvent> kafkaTemplate;

  @Override
  public InvoiceResponse generateInvoice(UUID timesheetId, UUID userId,
      Integer month, Integer year,
      BigDecimal totalHours,
      BigDecimal ratePerHour) {

    // Check if invoice already exists
    if (invoiceRepository.existsByTimesheetId(timesheetId)) {
      throw new BusinessException("Invoice already generated for this timesheet");
    }

    String invoiceNumber = generateInvoiceNumber(year, month);

    BigDecimal subtotal = totalHours.multiply(ratePerHour);
    BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.10)); // 10% tax
    BigDecimal total = subtotal.add(tax);

    Invoice invoice = Invoice.builder()
        .invoiceNumber(invoiceNumber)
        .userId(userId)
        .timesheetId(timesheetId)
        .period(Period.builder()
            .month(month)
            .year(year)
            .description(getMonthName(month) + " " + year)
            .build())
        .items(List.of(
            InvoiceItem.builder()
                .description("Professional Services - " + getMonthName(month))
                .hours(totalHours)
                .ratePerHour(ratePerHour)
                .total(subtotal)
                .build()))
        .subtotal(subtotal)
        .tax(tax)
        .total(total)
        .status(InvoiceStatus.GENERATED)
        .generatedAt(LocalDateTime.now())
        .dueDate(LocalDateTime.now().plusDays(30))
        .metadata(new HashMap<>())
        .build();

    invoice = invoiceRepository.save(invoice);
    log.info("Invoice generated: {}, user: {}, total: {}",
        invoiceNumber, userId, total);

    // Publish event
    publishInvoiceGenerated(invoice);

    return mapToResponse(invoice);
  }

  @Override
  public InvoiceResponse getInvoiceById(String invoiceId) {
    Invoice invoice = invoiceRepository.findById(invoiceId)
        .orElseThrow(() -> new BusinessException("Invoice not found"));
    return mapToResponse(invoice);
  }

  @Override
  public List<InvoiceResponse> getUserInvoices(UUID userId) {
    return invoiceRepository.findByUserIdOrderByGeneratedAtDesc(userId)
        .stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  @Override
  public InvoiceResponse updateInvoiceStatus(String invoiceId, InvoiceStatus status) {
    Invoice invoice = invoiceRepository.findById(invoiceId)
        .orElseThrow(() -> new BusinessException("Invoice not found"));

    invoice.setStatus(status);
    if (status == InvoiceStatus.PAID) {
      invoice.setPaidAt(LocalDateTime.now());
    }

    invoice = invoiceRepository.save(invoice);
    return mapToResponse(invoice);
  }

  @Override
  public byte[] generateInvoicePdf(String invoiceId) {
    Invoice invoice = invoiceRepository.findById(invoiceId)
        .orElseThrow(() -> new BusinessException("Invoice not found"));

    // Generate PDF (simplified - use library like iText or Apache PDFBox)
    String pdfContent = generatePdfContent(invoice);

    // Save PDF URL
    String pdfUrl = "s3://invoices/" + invoice.getInvoiceNumber() + ".pdf";
    invoice.setPdfUrl(pdfUrl);
    invoiceRepository.save(invoice);

    return pdfContent.getBytes();
  }

  private void publishInvoiceGenerated(Invoice invoice) {
    InvoiceGeneratedEvent event = InvoiceGeneratedEvent.builder()
        .payload(InvoiceGeneratedEvent.EventPayload.builder()
            .eventId(UUID.randomUUID())
            .invoiceId(invoice.getId())
            .invoiceNumber(invoice.getInvoiceNumber())
            .userId(invoice.getUserId())
            .total(invoice.getTotal())
            .generatedAt(invoice.getGeneratedAt())
            .build())
        .build();

    kafkaTemplate.send("invoice.generated", invoice.getUserId().toString(), event);
    log.info("Invoice generated event published: {}", invoice.getInvoiceNumber());
  }

  private String generateInvoiceNumber(Integer year, Integer month) {
    long count = invoiceRepository.count() + 1;
    return String.format("INV-%04d-%02d-%05d", year, month, count);
  }

  private String getMonthName(Integer month) {
    return LocalDate.of(2024, month, 1).getMonth().toString();
  }

  private String generatePdfContent(Invoice invoice) {
    // Simplified PDF generation
    StringBuilder pdf = new StringBuilder();
    pdf.append("INVOICE: ").append(invoice.getInvoiceNumber()).append("\n");
    pdf.append("Period: ").append(invoice.getPeriod().getDescription()).append("\n");
    pdf.append("Total: $").append(invoice.getTotal()).append("\n");
    return pdf.toString();
  }

  private InvoiceResponse mapToResponse(Invoice invoice) {
    return InvoiceResponse.builder()
        .id(invoice.getId())
        .invoiceNumber(invoice.getInvoiceNumber())
        .userId(invoice.getUserId())
        .period(invoice.getPeriod())
        .items(invoice.getItems())
        .subtotal(invoice.getSubtotal())
        .tax(invoice.getTax())
        .total(invoice.getTotal())
        .status(invoice.getStatus())
        .generatedAt(invoice.getGeneratedAt())
        .dueDate(invoice.getDueDate())
        .pdfUrl(invoice.getPdfUrl())
        .build();
  }
}