package com.acegames.backend.domain.model;

import lombok.Data;

import java.util.List;

@Data
public class StepGroup {
    private String group;
    private List<Step> steps;
}
