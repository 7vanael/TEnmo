package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

public interface TransferDao {
    int createTransfer(int transferTypeId, int transferStatusId, int fromUserId,
                  int toUserId, BigDecimal amountToSend);


//    List<Transfer> getTransferHistory(int userId);

    List<Transfer> getTransfersByStatus(int transferStatusId, int accountId);

    //So, do they really want to let you look up any transfer by transferID
    //without regard to if it involves the logged in user? - No
    //Yoav clarified, should only be transfers to/from the logged in user.
    Transfer getTransferByTransferId(int transferId, int userId);

    String requestMoney(BigDecimal amountRequested, int requesterUserId, int requesteeUserId);

//    void handleDbException(Exception ex, String verb);
    // add getTransferById to interface for view past transfers feature
    Transfer[] getTransferArrayByAccountId(Principal principal);

}
