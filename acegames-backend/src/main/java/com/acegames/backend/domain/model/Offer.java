package com.acegames.backend.domain.model;

import lombok.Data;

import java.util.List;

@Data
public class Offer {
    private String _id;
    private String name;
    private String purchaseProductId;
    private List<RequirementCondition> requirements;
}
