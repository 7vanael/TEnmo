package com.techelevator.tenmo.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class Transfer {

    private final int transferId;
    @NotNull(message = "Transfer type is required.")
    private final int transferTypeId;
    @NotNull(message = "Transfer status is required.")
    private int transferStatusId;
    @NotNull(message = "From account is required.")
    private int accountFrom;
    @NotNull(message = "To account is required.")
    private int accountTo;
    @NotNull(message = "Transfer amount is required.")
    private BigDecimal transferAmount;

    public Transfer(int transferId, int transferTypeId, int transferStatusId,
                    int accountFrom, int accountTo, BigDecimal transferAmount) {
        this.transferId = transferId;
        this.transferTypeId = transferTypeId;
        this.transferStatusId = transferStatusId;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.transferAmount = transferAmount;
    }

    public int getTransferId() {
        return transferId;
    }

    public int getTransferTypeId() {
        return transferTypeId;
    }

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }
}
