package com.timetracking.billing.domain.dto;

import com.timetracking.billing.domain.document.InvoiceItem;
import com.timetracking.billing.domain.document.InvoiceStatus;
import com.timetracking.billing.domain.document.Period;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceResponse {
  private String id;
  private String invoiceNumber;
  private UUID userId;
  private Period period;
  private List<InvoiceItem> items;
  private BigDecimal subtotal;
  private BigDecimal tax;
  private BigDecimal total;
  private InvoiceStatus status;
  private LocalDateTime generatedAt;
  private LocalDateTime dueDate;
  private String pdfUrl;
}

