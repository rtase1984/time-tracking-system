package com.timetracking.tracking.event.publiser;

import com.timetracking.tracking.domain.entity.TimeEntry;
import com.timetracking.tracking.event.model.TimeEntryRegisteredEvent;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeEntryEventPublisher {

  private static final String TOPIC = "time.entry.registered";
  private final KafkaTemplate<String, TimeEntryRegisteredEvent> kafkaTemplate;

  public void publishTimeEntryRegistered(TimeEntry entry) {
    TimeEntryRegisteredEvent event = TimeEntryRegisteredEvent.builder()
        .eventId(UUID.randomUUID())
        .eventType("TimeEntryRegistered")
        .timestamp(LocalDateTime.now())
        .payload(TimeEntryRegisteredEvent.TimeEntryPayload.builder()
            .entryId(entry.getId())
            .userId(entry.getUserId())
            .entryType(entry.getEntryType())
            .entryTimestamp(entry.getEntryTimestamp())
            .location(entry.getLocation())
            .build())
        .build();

    kafkaTemplate.send(TOPIC, entry.getUserId().toString(), event)
        .whenComplete((result, ex) -> {
          if (ex == null) {
            log.info("Time entry event published: entryId={}, topic={}",
                entry.getId(), TOPIC);
          } else {
            log.error("Failed to publish time entry event: entryId={}",
                entry.getId(), ex);
          }
        });
  }
}
