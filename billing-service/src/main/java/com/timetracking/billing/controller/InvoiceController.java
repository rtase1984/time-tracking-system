package com.timetracking.billing.controller;

import com.timetracking.billing.domain.document.InvoiceStatus;
import com.timetracking.billing.domain.dto.InvoiceResponse;
import com.timetracking.billing.service.InvoiceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import com.timetracking.billing.domain.dto.InvoiceGenerationRequest;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoices")
public class InvoiceController {

  private final InvoiceService invoiceService;

  @GetMapping("/{invoiceId}")
  @PreAuthorize("hasAnyRole('WORKER', 'ADMIN')")
  public ResponseEntity<InvoiceResponse> getInvoice(@PathVariable String invoiceId) {
    return ResponseEntity.ok(invoiceService.getInvoiceById(invoiceId));
  }

  @GetMapping("/user/{userId}")
  @PreAuthorize("hasAnyRole('WORKER', 'ADMIN')")
  public ResponseEntity<List<InvoiceResponse>> getUserInvoices(@PathVariable UUID userId) {
    return ResponseEntity.ok(invoiceService.getUserInvoices(userId));
  }

  @PutMapping("/{invoiceId}/status")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<InvoiceResponse> updateStatus(
      @PathVariable String invoiceId,
      @RequestParam InvoiceStatus status) {
    return ResponseEntity.ok(invoiceService.updateInvoiceStatus(invoiceId, status));
  }

  @GetMapping("/{invoiceId}/pdf")
  @PreAuthorize("hasAnyRole('WORKER', 'ADMIN')")
  public ResponseEntity<byte[]> downloadPdf(@PathVariable String invoiceId) {
    byte[] pdf = invoiceService.generateInvoicePdf(invoiceId);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDisposition(
        ContentDisposition.builder("attachment")
            .filename("invoice.pdf")
            .build());

    return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
  }

  @PostMapping("/generate")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<InvoiceResponse> generateManualInvoice(
      @RequestBody InvoiceGenerationRequest request) {
    InvoiceResponse response = invoiceService.generateInvoice(
        request.getTimesheetId(),
        request.getUserId(),
        request.getMonth(),
        request.getYear(),
        request.getTotalHours(),
        request.getRatePerHour());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
