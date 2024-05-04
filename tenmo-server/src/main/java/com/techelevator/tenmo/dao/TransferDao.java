package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

public interface TransferDao {
    int createTransfer(int transferTypeId, int transferStatusId, int fromUserId,
                  int toUserId, BigDecimal amountToSend);

    List<Transfer> getTransfersByStatus(int transferStatusId, int accountId);

    String requestMoney(BigDecimal amountRequested, int requesterUserId, int requesteeUserId);


    Transfer[] getTransferArrayByAccountId(Principal principal);

    Transfer getTransferByTransferId(int transferId, Principal principal);

}
