package com.firstclub.membership.repository;

import com.firstclub.membership.entity.MembershipEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MembershipEventRepository extends JpaRepository<MembershipEvent, Long> {
    List<MembershipEvent> findByMembershipIdOrderByOccurredAtDesc(Long membershipId);
}
