package com.firstclub.membership.service.criteria;

import com.firstclub.membership.entity.*;

public interface TierCriteriaEvaluator {
    CriterionKind kind();
    boolean isConfigured(MembershipTier tier);
    boolean evaluate(UserMembership membership, MembershipTier tier);

    enum CriterionKind { ACTIVITY, COHORT }
}
