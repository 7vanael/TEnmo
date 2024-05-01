package com.techelevator.tenmo.dao;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao{
    @Override
    public BigDecimal getBalance(int accountId) {
        return null;
    }

    @Override
    public int getAccountId(int userId) {
        return 0;
    }

    @Override
    public BigDecimal updateBalanceById(int accountId, BigDecimal amountChanged) {
        return null;
    }
}
