package com.firstclub.membership.dto;

import com.firstclub.membership.enums.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;

public final class ApiDtos {
    private ApiDtos() {}

    public record BenefitResponse(Long id, BenefitType benefitType, BigDecimal discountPercent,
                                  String description) {}
    public record TierResponse(Long id, String name, int rank, Integer minOrderCount,
                               BigDecimal minOrderValue, BigDecimal priceMultiplier,
                               BigDecimal price, String cohortTag,
                               List<BenefitResponse> benefits) {}
    public record PlanResponse(Long id, String name, int durationMonths, BigDecimal basePrice,
                               List<TierResponse> tiers) {}
    public record MembershipResponse(Long id, Long userId, PlanResponse plan, TierResponse tier,
                                     MembershipStatus status, LocalDate startDate, LocalDate expiryDate,
                                     boolean autoRenew, int totalOrderCount, BigDecimal totalOrderValue,
                                     String cohortTag, Long version) {}
    public record EventResponse(Long id, Long membershipId, Long userId, MembershipEventType eventType,
                                Long fromTierId, Long toTierId, Long fromPlanId, Long toPlanId,
                                LocalDateTime occurredAt, String remarks) {}
    public record SubscribeRequest(@NotNull @Positive Long userId, @NotNull @Positive Long planId,
                                   @NotNull @Positive Long tierId, String cohortTag) {}
    public record TierChangeRequest(@NotNull @Positive Long newTierId) {}
    public record RecordOrderRequest(@NotNull @DecimalMin("0.01") BigDecimal orderValue) {}
    public record ErrorResponse(String error, String message, LocalDateTime timestamp) {}
}
