package com.timetracking.auth.repository;

import com.timetracking.auth.domain.entity.User;
import com.timetracking.auth.domain.entity.UserRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  List<User> findByRole(UserRole role);

  List<User> findBySupervisorId(UUID supervisorId);

  @Query("SELECT u FROM User u WHERE u.supervisor.id = :supervisorId AND u.active = true")
  List<User> findActiveWorkersBySupervisor(@Param("supervisorId") UUID supervisorId);

  List<User> findByActiveTrue();
}