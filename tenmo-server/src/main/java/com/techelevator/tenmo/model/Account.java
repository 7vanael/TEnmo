package com.techelevator.tenmo.model;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class Account {

    private final int accountId;
    private final int userId;
    @NotNull(message = "Initial balance is required.")
    private BigDecimal balance;

    public Account(int accountId, int userId, BigDecimal balance) {
        this.accountId = accountId;
        this.userId = userId;
        this.balance = balance;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
