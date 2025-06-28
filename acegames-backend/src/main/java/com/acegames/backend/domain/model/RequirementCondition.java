package com.acegames.backend.domain.model;

import com.acegames.backend.domain.enums.Requirement;
import lombok.Data;

@Data
public class RequirementCondition {
    private Requirement requirement;
    private String operator;
    private Integer value;
}
