package com.acegames.backend.domain.model;

import lombok.Data;

import java.util.List;

@Data
public class Step {
    private int step;
    private Integer requiredStep;
    private List<Reward> rewards;
}
