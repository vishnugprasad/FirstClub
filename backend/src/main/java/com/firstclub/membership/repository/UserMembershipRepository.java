package com.firstclub.membership.repository;

import com.firstclub.membership.entity.UserMembership;
import com.firstclub.membership.enums.MembershipStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;

public interface UserMembershipRepository extends JpaRepository<UserMembership, Long> {
    boolean existsByUserIdAndStatus(Long userId, MembershipStatus status);

    @EntityGraph(attributePaths = {"plan", "tier"})
    Optional<UserMembership> findFirstByUserIdAndStatusOrderByIdDesc(Long userId, MembershipStatus status);

    List<UserMembership> findByStatusAndExpiryDateBefore(MembershipStatus status, LocalDate date);

    @Query("select m from UserMembership m join fetch m.plan join fetch m.tier where m.id = :id")
    Optional<UserMembership> findDetailedById(@Param("id") Long id);
}
