package com.firstclub.membership.event;

import com.firstclub.membership.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TierEvaluationListener {
    private final SubscriptionService subscriptionService;

    @Async("membershipTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderRecorded(OrderRecordedEvent event) {
        subscriptionService.autoUpgrade(event.membershipId());
    }
}
