package com.acegames.backend.domain.model;

import com.acegames.backend.domain.enums.TradeType;
import com.acegames.backend.domain.enums.EventType;
import lombok.Data;

@Data
public class Reward {
    private TradeType tradeType;
    private EventType eventType;
    private int value;
    private Integer remainingSeconds;
}
