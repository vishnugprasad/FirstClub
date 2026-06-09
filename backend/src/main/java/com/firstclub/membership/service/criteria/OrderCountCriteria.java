package com.firstclub.membership.service.criteria;

import com.firstclub.membership.entity.*;
import org.springframework.stereotype.Component;

@Component
public class OrderCountCriteria implements TierCriteriaEvaluator {
    public CriterionKind kind() { return CriterionKind.ACTIVITY; }
    public boolean isConfigured(MembershipTier tier) { return tier.getMinOrderCount() != null; }
    public boolean evaluate(UserMembership m, MembershipTier t) {
        return !isConfigured(t) || m.getTotalOrderCount() >= t.getMinOrderCount();
    }
}
