package com.techelevator.tenmo.Service;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service

public class TransferService {
    private final AccountDao accountDao;
    private final TransferDao transferDao;

    public TransferService(AccountDao accountDao, TransferDao transferDao){
        this.accountDao = accountDao;
        this.transferDao = transferDao;
    }

    public BigDecimal getBalanceById(int accountId){
        return accountDao.getBalanceById(accountId);
    }


}
