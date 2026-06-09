package com.firstclub.membership.repository;

import com.firstclub.membership.entity.MembershipTier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MembershipTierRepository extends JpaRepository<MembershipTier, Long> {
    List<MembershipTier> findByActiveTrueOrderByRankAsc();
    List<MembershipTier> findByActiveTrueOrderByRankDesc();
}
