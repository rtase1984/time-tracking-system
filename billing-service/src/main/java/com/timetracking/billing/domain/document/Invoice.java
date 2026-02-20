package com.timetracking.billing.domain.document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "invoices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

  @Id
  private String id;

  private String invoiceNumber;
  private UUID userId;
  private UUID timesheetId;

  private Period period;
  private List<InvoiceItem> items;

  private BigDecimal subtotal;
  private BigDecimal tax;
  private BigDecimal total;

  private InvoiceStatus status;

  private LocalDateTime generatedAt;
  private LocalDateTime dueDate;
  private LocalDateTime paidAt;

  private String pdfUrl;
  private Map<String, String> metadata;

}