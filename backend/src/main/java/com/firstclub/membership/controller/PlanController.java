package com.firstclub.membership.controller;

import com.firstclub.membership.dto.ApiDtos.PlanResponse;
import com.firstclub.membership.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;
    private final TierService tierService;
    private final ApiMapper mapper;

    @GetMapping
    public List<PlanResponse> all() {
        var tiers = tierService.getAllActiveTiers();
        return planService.getAllActivePlans().stream().map(plan -> mapper.plan(plan, tiers)).toList();
    }

    @GetMapping("/{id}")
    public PlanResponse one(@PathVariable Long id) {
        return mapper.plan(planService.getPlanById(id), tierService.getAllActiveTiers());
    }
}
