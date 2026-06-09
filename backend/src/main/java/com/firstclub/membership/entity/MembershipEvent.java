package com.firstclub.membership.entity;

import com.firstclub.membership.enums.MembershipEventType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(indexes = @Index(name = "idx_event_membership_time", columnList = "membership_id,occurred_at"))
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class MembershipEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "membership_id")
    private Long membershipId;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private MembershipEventType eventType;
    private Long fromTierId;
    private Long toTierId;
    private Long fromPlanId;
    private Long toPlanId;
    @Column(name = "occurred_at")
    private LocalDateTime occurredAt;
    private String remarks;
}
