package com.firstclub.membership.entity;

import com.firstclub.membership.enums.BenefitType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class TierBenefit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private MembershipTier tier;
    @Enumerated(EnumType.STRING)
    private BenefitType benefitType;
    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercent;
    private String description;
    private boolean active;
}
