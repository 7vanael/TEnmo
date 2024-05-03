package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDao {

    BigDecimal getBalanceByUser(String userName);
    int getAccountId(int userId);

    BigDecimal updateBalanceByUser(String userName, BigDecimal amountChanged);

    Account getAccountByAccountId(int accountId);


}
