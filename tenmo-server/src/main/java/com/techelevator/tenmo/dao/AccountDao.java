package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

public interface AccountDao {

    BigDecimal getBalanceByUser(String userName);
    int getAccountId(int userId);

    BigDecimal updateBalanceById(String userName, BigDecimal amountChanged);

}
