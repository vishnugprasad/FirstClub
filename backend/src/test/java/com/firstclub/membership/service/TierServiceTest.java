package com.firstclub.membership.service;

import com.firstclub.membership.entity.*;
import com.firstclub.membership.repository.*;
import com.firstclub.membership.service.criteria.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TierServiceTest {
    @Test
    void choosesHighestTierWhenEitherActivityThresholdIsMet() {
        MembershipTier silver = tier(1L, "SILVER", 1, null, null);
        MembershipTier gold = tier(2L, "GOLD", 2, 5, "2000");
        MembershipTier platinum = tier(3L, "PLATINUM", 3, 15, "8000");
        MembershipTierRepository tiers = mock(MembershipTierRepository.class);
        when(tiers.findByActiveTrueOrderByRankDesc()).thenReturn(List.of(platinum, gold, silver));
        UserMembership membership = UserMembership.builder().totalOrderCount(2)
                .totalOrderValue(new BigDecimal("2500")).build();

        TierService service = new TierService(tiers, mock(TierBenefitRepository.class),
                List.of(new OrderCountCriteria(), new OrderValueCriteria(), new CohortCriteria()));

        assertThat(service.evaluateTierForMembership(membership).getName()).isEqualTo("GOLD");
    }

    private MembershipTier tier(Long id, String name, int rank, Integer count, String value) {
        return MembershipTier.builder().id(id).name(name).rank(rank).minOrderCount(count)
                .minOrderValue(value == null ? null : new BigDecimal(value)).active(true).build();
    }
}
