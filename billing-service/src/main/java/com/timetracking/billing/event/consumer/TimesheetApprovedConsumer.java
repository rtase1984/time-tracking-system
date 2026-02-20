package com.timetracking.billing.event.consumer;

import com.timetracking.billing.service.InvoiceService;
import com.timetracking.common.event.TimesheetApprovedEvent;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TimesheetApprovedConsumer {

  private final InvoiceService invoiceService;

  @KafkaListener(topics = "timesheet.approved", groupId = "billing-service")
  public void consumeTimesheetApproved(TimesheetApprovedEvent event) {
    log.info("Received timesheet approved event: {}", event.getPayload().getTimesheetId());

    try {
      // Generate invoice automatically
      BigDecimal ratePerHour = BigDecimal.valueOf(25.00); // Should come from user config

      invoiceService.generateInvoice(
          event.getPayload().getTimesheetId(),
          event.getPayload().getUserId(),
          event.getPayload().getPeriodMonth(),
          event.getPayload().getPeriodYear(),
          event.getPayload().getTotalHours(),
          ratePerHour);

      log.info("Invoice generated successfully for timesheet: {}", event.getPayload().getTimesheetId());
    } catch (Exception e) {
      log.error("Failed to generate invoice for timesheet: {}", event.getPayload().getTimesheetId(), e);
    }
  }
}
