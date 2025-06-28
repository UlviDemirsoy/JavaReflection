package com.acegames.backend.domain.model;

import lombok.Data;

import java.util.List;

@Data
public class Cascade {
    private String _id;
    private String name;
    private Integer skinId;
    private Long startDate;
    private Long endDate;
    private List<TileWeight> stepRule;
    private List<StepGroup> stepInfo;
}
