package com.firstclub.membership.controller;

import com.firstclub.membership.dto.ApiDtos.*;
import com.firstclub.membership.service.TierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tiers")
@RequiredArgsConstructor
public class TierController {
    private final TierService tierService;
    private final ApiMapper mapper;

    @GetMapping
    public List<TierResponse> all() {
        return tierService.getAllActiveTiers().stream().map(mapper::tier).toList();
    }

    @GetMapping("/{id}/benefits")
    public List<BenefitResponse> benefits(@PathVariable Long id) {
        return tierService.getBenefitsForTier(id).stream().map(mapper::benefit).toList();
    }
}
