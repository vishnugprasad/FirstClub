package com.firstclub.membership.service;

import com.firstclub.membership.entity.MembershipPlan;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.MembershipPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final MembershipPlanRepository repository;

    public List<MembershipPlan> getAllActivePlans() {
        return repository.findByActiveTrueOrderByDurationMonthsAsc();
    }

    public MembershipPlan getPlanById(Long id) {
        return repository.findById(id).filter(MembershipPlan::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Plan " + id + " not found"));
    }
}
