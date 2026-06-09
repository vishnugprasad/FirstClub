package com.firstclub.membership.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class MembershipTier {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(name = "tier_rank")
    private int rank;
    private Integer minOrderCount;
    @Column(precision = 12, scale = 2)
    private BigDecimal minOrderValue;
    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal priceMultiplier;
    private String cohortTag;
    private boolean active;
}
