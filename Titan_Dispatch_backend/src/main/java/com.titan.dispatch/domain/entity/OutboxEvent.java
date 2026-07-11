package com.titan.dispatch.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String aggregateType; // e.g., "DISPATCH_ALLOCATION"

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false)
    private String eventType; // e.g., "DISPATCH_COMPLETED"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload; // JSON representation of the event data

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean processed = false;
}