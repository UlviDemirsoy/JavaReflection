package com.acegames.backend.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Requirement {
    minLevel,
    daysSinceRegistration,
    playTimeHours,
    lastLoginDaysAgo,
    hasSubscription,
    region,
    hasPremiumAccess;

    @JsonValue
    public String getValue() {
        return this.name();
    }
}
