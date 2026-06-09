package com.firstclub.membership.config;

import com.firstclub.membership.entity.*;
import com.firstclub.membership.enums.BenefitType;
import com.firstclub.membership.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seed(MembershipPlanRepository plans, MembershipTierRepository tiers,
                           TierBenefitRepository benefits) {
        return args -> {
            if (plans.count() > 0) {
                return;
            }
            plans.saveAll(List.of(
                    plan("Monthly", 1, "299"),
                    plan("Quarterly", 3, "799"),
                    plan("Yearly", 12, "2499")
            ));
            MembershipTier silver = tiers.save(tier("SILVER", 1, null, null, "1.00"));
            MembershipTier gold = tiers.save(tier("GOLD", 2, 5, "2000", "1.25"));
            MembershipTier platinum = tiers.save(tier("PLATINUM", 3, 15, "8000", "1.50"));
            benefits.saveAll(List.of(
                    benefit(silver, BenefitType.FREE_DELIVERY, null, "Free delivery on every order"),
                    benefit(silver, BenefitType.EXTRA_DISCOUNT, "5", "Extra 5% member discount"),
                    benefit(gold, BenefitType.FREE_DELIVERY, null, "Free delivery on every order"),
                    benefit(gold, BenefitType.EXTRA_DISCOUNT, "10", "Extra 10% member discount"),
                    benefit(gold, BenefitType.EXCLUSIVE_DEALS, null, "Access to member-only deals"),
                    benefit(platinum, BenefitType.FREE_DELIVERY, null, "Free delivery on every order"),
                    benefit(platinum, BenefitType.EXTRA_DISCOUNT, "15", "Extra 15% member discount"),
                    benefit(platinum, BenefitType.EXCLUSIVE_DEALS, null, "Access to member-only deals"),
                    benefit(platinum, BenefitType.EARLY_SALE_ACCESS, null, "Shop sales before everyone else"),
                    benefit(platinum, BenefitType.PRIORITY_SUPPORT, null, "Priority customer support")
            ));
        };
    }

    private MembershipPlan plan(String name, int months, String price) {
        return MembershipPlan.builder().name(name).durationMonths(months)
                .basePrice(new BigDecimal(price)).active(true).build();
    }

    private MembershipTier tier(String name, int rank, Integer count, String value, String multiplier) {
        return MembershipTier.builder().name(name).rank(rank).minOrderCount(count)
                .minOrderValue(value == null ? null : new BigDecimal(value))
                .priceMultiplier(new BigDecimal(multiplier)).active(true).build();
    }

    private TierBenefit benefit(MembershipTier tier, BenefitType type, String discount, String description) {
        return TierBenefit.builder().tier(tier).benefitType(type)
                .discountPercent(discount == null ? null : new BigDecimal(discount))
                .description(description).active(true).build();
    }
}
