package com.firstclub.membership.scheduler;

import com.firstclub.membership.entity.UserMembership;
import com.firstclub.membership.enums.*;
import com.firstclub.membership.repository.UserMembershipRepository;
import com.firstclub.membership.service.EventLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class MembershipExpiryScheduler {
    private final UserMembershipRepository membershipRepository;
    private final EventLogService eventLogService;

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void expireMemberships() {
        for (UserMembership membership : membershipRepository
                .findByStatusAndExpiryDateBefore(MembershipStatus.ACTIVE, LocalDate.now())) {
            membership.setStatus(MembershipStatus.EXPIRED);
            membershipRepository.save(membership);
            eventLogService.log(membership, MembershipEventType.EXPIRED,
                    membership.getTier().getId(), null, membership.getPlan().getId(), null,
                    "Membership expired");
        }
    }
}
