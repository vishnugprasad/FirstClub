package com.firstclub.membership.service;

import com.firstclub.membership.entity.*;
import com.firstclub.membership.repository.TierBenefitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BenefitResolver {
    private final TierBenefitRepository benefitRepository;

    public List<TierBenefit> resolve(MembershipTier tier) {
        return benefitRepository.findByTierIdAndActiveTrueOrderById(tier.getId());
    }
}
