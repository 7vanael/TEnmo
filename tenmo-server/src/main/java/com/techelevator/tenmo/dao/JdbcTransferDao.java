package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    @Override
    public BigDecimal sendMoney(BigDecimal amountToSend, int fromUserId, int toUserId) {
        return null;
    }

    @Override
    public List<Transfer> getTransfersByStatus(int transferId, int status, int userId) {
        return null;
    }

    @Override
    public Transfer getTransferByTransferId(int transferId, int userId) {
        return null;
    }

    @Override
    public String requestMoney(BigDecimal amountRequested, int requesterUserId, int requesteeUserId) {
        return null;
    }
}
