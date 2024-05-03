package com.techelevator.tenmo.Service;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Service

public class TransferService {
    private final AccountDao accountDao;
    private final TransferDao transferDao;
    private final UserDao userDao;

    public TransferService(AccountDao accountDao,
                           TransferDao transferDao, UserDao userDao){
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.userDao = userDao;
    }

    public BigDecimal getBalanceByUser(String userName){
        return accountDao.getBalanceByUser(userName);
    }

    public User[] findAllUsers(){
        return userDao.findAll();
    }

    @Transactional
    public Transfer createTransfer(Transfer transfer, Principal principal){
        Account fromAccount = accountDao.getAccountByAccountId(accountDao.getAccountId(transfer.getAccountFrom()));
        Account toAccount = accountDao.getAccountByAccountId(accountDao.getAccountId(transfer.getAccountTo()));
        User fromUser = userDao.getUserById(fromAccount.getUserId());
        User toUser = userDao.getUserById(toAccount.getUserId());

        boolean sufficientFunds = false;
        boolean accountIdNotTheSame = false;
        boolean sendingToOtherAccount = false;
        boolean notNull = false;
        boolean amountIsPositive = false;

        if (transfer.getTransferAmount().compareTo(BigDecimal.ZERO) > 0) {
            amountIsPositive = true;
        }
        notNull = transfer.getTransferAmount() != null;

        if (fromAccount.getBalance().subtract(transfer.getTransferAmount()).compareTo(BigDecimal.ZERO) > 0){
            sufficientFunds = true;
        }

        accountIdNotTheSame = !fromAccount.equals(toAccount);
        String userName = principal.getName();
        int loggedInUserAccountId = accountDao.getAccountId(userDao.findByUsername(userName).getId());

        //checks if user is sending money to their own account
        if (transfer.getTransferTypeId() == 2){
            if (loggedInUserAccountId == transfer.getAccountTo()){
                sendingToOtherAccount = true;
            }
            //checks if user is requesting money from their own account
        } else if (transfer.getTransferTypeId() == 1){
            if (loggedInUserAccountId == transfer.getAccountFrom()){
                sendingToOtherAccount = true;
            }
        }
        BigDecimal updatedSourceBalance = null;
        BigDecimal updatedDestinationBalance = null;
        if (notNull && sufficientFunds && amountIsPositive && accountIdNotTheSame && !sendingToOtherAccount){
            BigDecimal debit = transfer.getTransferAmount().multiply(new BigDecimal("-1"));
            accountDao.updateBalanceByUser(fromUser.getUsername(), debit);
            accountDao.updateBalanceByUser(toUser.getUsername(), transfer.getTransferAmount());
            updatedSourceBalance = fromAccount.getBalance();
            updatedDestinationBalance = toAccount.getBalance();
        }

        int transferId = transferDao.createTransfer(transfer.getTransferTypeId(),
                transfer.getTransferStatusId(), transfer.getAccountFrom(), transfer.getAccountTo(),
                transfer.getTransferAmount());

        transfer.setTransferId(transferId);

        return transfer;
    }

//    public Transfer[] getAllTransfers(Principal principal){
//        Transfer[] Transfers;
//
//    }


}
