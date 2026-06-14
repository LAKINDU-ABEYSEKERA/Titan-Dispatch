package com.titan.dispatch.repository;

import com.titan.dispatch.domain.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    // Grabs the oldest unprocessed events, locking the rows to prevent concurrent worker collisions (SKIP LOCKED)
    @Query(value = "SELECT * FROM outbox_events WHERE processed = false ORDER BY created_at ASC LIMIT 50 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    List<OutboxEvent> findUnprocessedEvents();
}