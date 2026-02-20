package com.timetracking.billing.repository;

import com.timetracking.billing.domain.document.Invoice;
import com.timetracking.billing.domain.document.InvoiceStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends MongoRepository<Invoice, String> {

  Optional<Invoice> findByTimesheetId(UUID timesheetId);

  List<Invoice> findByUserId(UUID userId);

  List<Invoice> findByUserIdOrderByGeneratedAtDesc(UUID userId);

  @Query("{ 'period.year': ?0, 'period.month': ?1 }")
  List<Invoice> findByPeriod(Integer year, Integer month);

  List<Invoice> findByStatus(InvoiceStatus status);

  boolean existsByTimesheetId(UUID timesheetId);

  long countByUserIdAndStatus(UUID userId, InvoiceStatus status);
}
