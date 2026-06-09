package com.firstclub.membership.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class MembershipPlan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    private int durationMonths;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;
    private boolean active;
}
