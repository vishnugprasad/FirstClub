package com.firstclub.membership.event;

public record MembershipTierChangedEvent(Long membershipId, Long fromTierId, Long toTierId) {}
