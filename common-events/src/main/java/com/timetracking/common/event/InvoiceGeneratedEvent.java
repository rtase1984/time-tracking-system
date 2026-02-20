package com.timetracking.common.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceGeneratedEvent {
    private EventPayload payload;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventPayload {
        private UUID eventId;
        private String invoiceId;
        private String invoiceNumber;
        private UUID userId;
        private BigDecimal total;
        private LocalDateTime generatedAt;
    }
}
