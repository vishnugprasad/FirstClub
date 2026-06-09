package com.firstclub.membership.controller;

import com.firstclub.membership.dto.ApiDtos.*;
import com.firstclub.membership.entity.*;
import com.firstclub.membership.service.BenefitResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.RoundingMode;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ApiMapper {
    private final BenefitResolver benefitResolver;

    public BenefitResponse benefit(TierBenefit value) {
        return new BenefitResponse(value.getId(), value.getBenefitType(), value.getDiscountPercent(),
                value.getDescription());
    }

    public TierResponse tier(MembershipTier value) {
        return tier(value, null);
    }

    public TierResponse tier(MembershipTier value, MembershipPlan plan) {
        var price = plan == null ? null : plan.getBasePrice()
                .multiply(value.getPriceMultiplier())
                .setScale(0, RoundingMode.HALF_UP);
        return new TierResponse(value.getId(), value.getName(), value.getRank(),
                value.getMinOrderCount(), value.getMinOrderValue(), value.getPriceMultiplier(),
                price, value.getCohortTag(),
                benefitResolver.resolve(value).stream().map(this::benefit).toList());
    }

    public PlanResponse plan(MembershipPlan value, List<MembershipTier> tiers) {
        return new PlanResponse(value.getId(), value.getName(), value.getDurationMonths(),
                value.getBasePrice(), tiers.stream().map(tier -> tier(tier, value)).toList());
    }

    public MembershipResponse membership(UserMembership value) {
        return new MembershipResponse(value.getId(), value.getUserId(),
                plan(value.getPlan(), List.of()), tier(value.getTier()), value.getStatus(),
                value.getStartDate(), value.getExpiryDate(), value.isAutoRenew(),
                value.getTotalOrderCount(), value.getTotalOrderValue(), value.getCohortTag(),
                value.getVersion());
    }

    public EventResponse event(MembershipEvent value) {
        return new EventResponse(value.getId(), value.getMembershipId(), value.getUserId(),
                value.getEventType(), value.getFromTierId(), value.getToTierId(),
                value.getFromPlanId(), value.getToPlanId(), value.getOccurredAt(), value.getRemarks());
    }
}
