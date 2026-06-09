package com.firstclub.membership.entity;

import com.firstclub.membership.enums.MembershipStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(indexes = @Index(name = "idx_membership_user_status", columnList = "user_id,status"))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class UserMembership {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private MembershipPlan plan;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private MembershipTier tier;
    @Enumerated(EnumType.STRING)
    private MembershipStatus status;
    private LocalDate startDate;
    private LocalDate expiryDate;
    private boolean autoRenew;
    private int totalOrderCount;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalOrderValue;
    private String cohortTag;
    @Version
    private Long version;
}
