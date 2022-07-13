package ru.turaev.entity;

import lombok.Data;

import java.util.UUID;

@Data
public class Account {
    private String id;
    private int money;

    public Account() {
        this.id = UUID.randomUUID().toString();
        this.money = 10_000;
    }
}
