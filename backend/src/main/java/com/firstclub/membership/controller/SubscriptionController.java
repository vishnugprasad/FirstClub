package com.firstclub.membership.controller;

import com.firstclub.membership.dto.ApiDtos.*;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.UserMembershipRepository;
import com.firstclub.membership.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final EventLogService eventLogService;
    private final UserMembershipRepository membershipRepository;
    private final ApiMapper mapper;

    @PostMapping
    public ResponseEntity<MembershipResponse> subscribe(@Valid @RequestBody SubscribeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.membership(
                subscriptionService.subscribe(request.userId(), request.planId(),
                        request.tierId(), request.cohortTag())));
    }

    @GetMapping("/user/{userId}")
    public MembershipResponse current(@PathVariable Long userId) {
        return mapper.membership(subscriptionService.getCurrentMembership(userId));
    }

    @PatchMapping("/{id}/upgrade")
    public MembershipResponse upgrade(@PathVariable Long id, @Valid @RequestBody TierChangeRequest request) {
        return mapper.membership(subscriptionService.upgrade(id, request.newTierId()));
    }

    @PatchMapping("/{id}/downgrade")
    public MembershipResponse downgrade(@PathVariable Long id, @Valid @RequestBody TierChangeRequest request) {
        return mapper.membership(subscriptionService.downgrade(id, request.newTierId()));
    }

    @DeleteMapping("/{id}")
    public MembershipResponse cancel(@PathVariable Long id) {
        return mapper.membership(subscriptionService.cancel(id));
    }

    @PostMapping("/user/{userId}/record-order")
    public MembershipResponse order(@PathVariable Long userId, @Valid @RequestBody RecordOrderRequest request) {
        return mapper.membership(subscriptionService.recordOrder(userId, request.orderValue()));
    }

    @GetMapping("/{id}/history")
    public List<EventResponse> history(@PathVariable Long id) {
        if (!membershipRepository.existsById(id)) {
            throw new ResourceNotFoundException("Membership " + id + " not found");
        }
        return eventLogService.history(id).stream().map(mapper::event).toList();
    }
}
