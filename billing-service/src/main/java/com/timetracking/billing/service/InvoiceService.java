package com.timetracking.billing.service;

import com.timetracking.billing.domain.document.Invoice;
import com.timetracking.billing.domain.document.InvoiceStatus;
import com.timetracking.billing.domain.dto.InvoiceResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface InvoiceService {
  InvoiceResponse generateInvoice(UUID timesheetId, UUID userId, Integer month, Integer year, BigDecimal totalHours,
      BigDecimal ratePerHour);

  InvoiceResponse getInvoiceById(String invoiceId);

  List<InvoiceResponse> getUserInvoices(UUID userId);

  InvoiceResponse updateInvoiceStatus(String invoiceId, InvoiceStatus status);

  byte[] generateInvoicePdf(String invoiceId);
}
