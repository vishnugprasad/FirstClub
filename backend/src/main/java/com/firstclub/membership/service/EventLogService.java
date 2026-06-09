package com.firstclub.membership.service;

import com.firstclub.membership.entity.*;
import com.firstclub.membership.enums.MembershipEventType;
import com.firstclub.membership.repository.MembershipEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventLogService {
    private final MembershipEventRepository repository;

    public MembershipEvent log(UserMembership membership, MembershipEventType type,
                               Long fromTierId, Long toTierId, Long fromPlanId,
                               Long toPlanId, String remarks) {
        return repository.save(MembershipEvent.builder()
                .membershipId(membership.getId()).userId(membership.getUserId()).eventType(type)
                .fromTierId(fromTierId).toTierId(toTierId).fromPlanId(fromPlanId).toPlanId(toPlanId)
                .occurredAt(LocalDateTime.now()).remarks(remarks).build());
    }

    public List<MembershipEvent> history(Long membershipId) {
        return repository.findByMembershipIdOrderByOccurredAtDesc(membershipId);
    }
}
