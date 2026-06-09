package com.firstclub.membership.service.criteria;

import com.firstclub.membership.entity.*;
import org.springframework.stereotype.Component;

@Component
public class CohortCriteria implements TierCriteriaEvaluator {
    public CriterionKind kind() { return CriterionKind.COHORT; }
    public boolean isConfigured(MembershipTier tier) {
        return tier.getCohortTag() != null && !tier.getCohortTag().isBlank();
    }
    public boolean evaluate(UserMembership m, MembershipTier t) {
        return !isConfigured(t) || t.getCohortTag().equalsIgnoreCase(m.getCohortTag());
    }
}
