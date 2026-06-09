package com.firstclub.membership.service;

import com.firstclub.membership.entity.*;
import com.firstclub.membership.enums.*;
import com.firstclub.membership.event.*;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.UserMembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final UserMembershipRepository membershipRepository;
    private final PlanService planService;
    private final TierService tierService;
    private final EventLogService eventLogService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UserMembership subscribe(Long userId, Long planId, Long tierId, String cohortTag) {
        if (membershipRepository.existsByUserIdAndStatus(userId, MembershipStatus.ACTIVE)) {
            throw new IllegalStateException("User already has an active membership");
        }
        MembershipPlan plan = planService.getPlanById(planId);
        MembershipTier tier = tierService.getTierById(tierId);
        LocalDate today = LocalDate.now();
        UserMembership membership = membershipRepository.save(UserMembership.builder()
                .userId(userId).plan(plan).tier(tier).status(MembershipStatus.ACTIVE)
                .startDate(today).expiryDate(today.plusMonths(plan.getDurationMonths()))
                .autoRenew(false).totalOrderCount(0).totalOrderValue(BigDecimal.ZERO)
                .cohortTag(normalize(cohortTag)).build());
        eventLogService.log(membership, MembershipEventType.SUBSCRIBED, null, tier.getId(),
                null, plan.getId(), "Membership subscribed");
        return membership;
    }

    @Transactional
    public UserMembership upgrade(Long membershipId, Long newTierId) {
        UserMembership membership = activeMembership(membershipId);
        MembershipTier target = tierService.getTierById(newTierId);
        if (target.getRank() <= membership.getTier().getRank()) {
            throw new IllegalArgumentException("Upgrade target must have a higher rank");
        }
        Long previous = membership.getTier().getId();
        membership.setTier(target);
        UserMembership saved = membershipRepository.save(membership);
        eventLogService.log(saved, MembershipEventType.UPGRADED, previous, target.getId(),
                null, null, "Tier upgraded manually");
        eventPublisher.publishEvent(new MembershipTierChangedEvent(saved.getId(), previous, target.getId()));
        return saved;
    }

    @Transactional
    public UserMembership downgrade(Long membershipId, Long newTierId) {
        UserMembership membership = activeMembership(membershipId);
        MembershipTier target = tierService.getTierById(newTierId);
        if (target.getRank() >= membership.getTier().getRank()) {
            throw new IllegalArgumentException("Downgrade target must have a lower rank");
        }
        Long previous = membership.getTier().getId();
        membership.setTier(target);
        UserMembership saved = membershipRepository.save(membership);
        eventLogService.log(saved, MembershipEventType.DOWNGRADED, previous, target.getId(),
                null, null, "Tier downgraded manually");
        return saved;
    }

    @Transactional
    public UserMembership cancel(Long membershipId) {
        UserMembership membership = activeMembership(membershipId);
        membership.setStatus(MembershipStatus.CANCELLED);
        UserMembership saved = membershipRepository.save(membership);
        eventLogService.log(saved, MembershipEventType.CANCELLED, saved.getTier().getId(), null,
                saved.getPlan().getId(), null, "Membership cancelled");
        return saved;
    }

    @Transactional(readOnly = true)
    public UserMembership getCurrentMembership(Long userId) {
        return membershipRepository.findFirstByUserIdAndStatusOrderByIdDesc(userId, MembershipStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active membership for user " + userId));
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 4,
            backoff = @Backoff(delay = 50, multiplier = 2))
    @Transactional
    public UserMembership recordOrder(Long userId, BigDecimal orderValue) {
        if (orderValue == null || orderValue.signum() <= 0) {
            throw new IllegalArgumentException("Order value must be greater than zero");
        }
        UserMembership membership = getCurrentMembership(userId);
        membership.setTotalOrderCount(membership.getTotalOrderCount() + 1);
        membership.setTotalOrderValue(membership.getTotalOrderValue().add(orderValue));
        UserMembership saved = membershipRepository.saveAndFlush(membership);
        eventPublisher.publishEvent(new OrderRecordedEvent(saved.getId()));
        return saved;
    }

    @Transactional
    public void autoUpgrade(Long membershipId) {
        UserMembership membership = activeMembership(membershipId);
        MembershipTier target = tierService.evaluateTierForMembership(membership);
        if (target.getRank() <= membership.getTier().getRank()) {
            return;
        }
        Long previous = membership.getTier().getId();
        membership.setTier(target);
        membershipRepository.save(membership);
        eventLogService.log(membership, MembershipEventType.TIER_CHANGED, previous, target.getId(),
                null, null, "Tier upgraded automatically after order evaluation");
    }

    private UserMembership activeMembership(Long id) {
        UserMembership membership = membershipRepository.findDetailedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership " + id + " not found"));
        if (membership.getStatus() != MembershipStatus.ACTIVE) {
            throw new IllegalStateException("Membership is not active");
        }
        return membership;
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toUpperCase();
    }
}
