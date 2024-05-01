package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {
    BigDecimal sendMoney(BigDecimal amountToSend, int fromUserId, int toUserId);


//    List<Transfer> getTransferHistory(int userId);

    List<Transfer> getTransfersByStatus(int transferId, int status, int userId);

    //So, do they really want to let you look up any transfer by transferID
    //without regard to if it involves the logged in user?
    Transfer getTransferByTransferId(int transferId, int userId);

    String requestMoney(BigDecimal amountRequested, int requesterUserId, int requesteeUserId);



}
