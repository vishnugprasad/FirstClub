package com.firstclub.membership.service;

import com.firstclub.membership.entity.*;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.*;
import com.firstclub.membership.service.criteria.TierCriteriaEvaluator;
import com.firstclub.membership.service.criteria.TierCriteriaEvaluator.CriterionKind;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TierService {
    private final MembershipTierRepository tierRepository;
    private final TierBenefitRepository benefitRepository;
    private final List<TierCriteriaEvaluator> evaluators;

    public List<MembershipTier> getAllActiveTiers() {
        return tierRepository.findByActiveTrueOrderByRankAsc();
    }

    public MembershipTier getTierById(Long id) {
        return tierRepository.findById(id).filter(MembershipTier::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Tier " + id + " not found"));
    }

    public List<TierBenefit> getBenefitsForTier(Long tierId) {
        getTierById(tierId);
        return benefitRepository.findByTierIdAndActiveTrueOrderById(tierId);
    }

    public MembershipTier evaluateTierForMembership(UserMembership membership) {
        return tierRepository.findByActiveTrueOrderByRankDesc().stream()
                .filter(tier -> isEligible(membership, tier))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No default membership tier configured"));
    }

    private boolean isEligible(UserMembership membership, MembershipTier tier) {
        List<TierCriteriaEvaluator> activity = configured(tier, CriterionKind.ACTIVITY);
        List<TierCriteriaEvaluator> cohorts = configured(tier, CriterionKind.COHORT);
        boolean activityEligible = activity.isEmpty()
                || activity.stream().anyMatch(e -> e.evaluate(membership, tier));
        boolean cohortEligible = cohorts.stream().allMatch(e -> e.evaluate(membership, tier));
        return activityEligible && cohortEligible;
    }

    private List<TierCriteriaEvaluator> configured(MembershipTier tier, CriterionKind kind) {
        return evaluators.stream()
                .filter(e -> e.kind() == kind && e.isConfigured(tier))
                .toList();
    }
}
