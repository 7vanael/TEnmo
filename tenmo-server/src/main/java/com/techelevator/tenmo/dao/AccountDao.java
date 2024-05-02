package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

public interface AccountDao {

    BigDecimal getBalanceById(int accountId);
    int getAccountId(int userId);

    BigDecimal updateBalanceById(int accountId, BigDecimal amountChanged);

}
