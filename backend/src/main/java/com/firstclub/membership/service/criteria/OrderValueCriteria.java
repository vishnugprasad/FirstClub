package com.firstclub.membership.service.criteria;

import com.firstclub.membership.entity.*;
import org.springframework.stereotype.Component;

@Component
public class OrderValueCriteria implements TierCriteriaEvaluator {
    public CriterionKind kind() { return CriterionKind.ACTIVITY; }
    public boolean isConfigured(MembershipTier tier) { return tier.getMinOrderValue() != null; }
    public boolean evaluate(UserMembership m, MembershipTier t) {
        return !isConfigured(t) || m.getTotalOrderValue().compareTo(t.getMinOrderValue()) >= 0;
    }
}
