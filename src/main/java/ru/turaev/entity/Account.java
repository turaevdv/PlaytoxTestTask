package ru.turaev.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.turaev.exception.NotEnoughMoneyException;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Data
public class Account {
    private final String id;
    private int money;

    public Account() {
        this.id = UUID.randomUUID().toString();
        this.money = 10_000;
    }

    public void transferTo(Account accountTo, int amount) {
        if (Objects.equals(this.id, accountTo.id)) {
            log.warn("An attempt to transfer money from one account to the same account with id = {}", this.id);
            throw new UnsupportedOperationException("The same account");
        }

        if (this.id.compareTo(accountTo.id) > 0) {
            synchronized (this) {
                synchronized (accountTo) {
                    doTransferTo(accountTo, amount);
                }
            }
        } else {
            synchronized (accountTo) {
                synchronized (this) {
                    doTransferTo(accountTo, amount);
                }
            }
        }
        log.info("Transfer {} from id = {} to id = {}", amount, this.id, accountTo.id);
    }

    private void doTransferTo(Account to, int amount) {
        if (amount > this.money) {
            log.warn("Not enough money = {} to transfer from id = {} to id = {}", amount, this.id, to.id);
            throw new NotEnoughMoneyException();
        }
        this.money -= amount;
        to.money += amount;
    }
}
