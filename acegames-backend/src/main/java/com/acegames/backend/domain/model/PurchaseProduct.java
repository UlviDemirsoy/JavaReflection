package com.acegames.backend.domain.model;

import lombok.Data;

@Data
public class PurchaseProduct {
    private String _id;        // MongoDB id
    private String name;
    private double price;
    private String currency;
}
