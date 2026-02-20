package com.timetracking.billing.domain.document;


import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItem {
  private String description;
  private BigDecimal hours;
  private BigDecimal ratePerHour;
  private BigDecimal total;
}
