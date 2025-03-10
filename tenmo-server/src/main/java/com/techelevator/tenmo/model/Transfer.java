package com.techelevator.tenmo.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public class Transfer {

    private int transferId;
    @NotNull(message = "Transfer type is required.")
    private int transferTypeId;
    @NotNull(message = "Transfer status is required.")
    private int transferStatusId;
    @NotNull(message = "From account is required.")
    private int accountFrom;
    @NotNull(message = "To account is required.")
    private int accountTo;
    @NotNull(message = "Transfer amount is required.")
    private BigDecimal transferAmount;

    public Transfer(){}
    public Transfer(int transferId, int transferTypeId, int transferStatusId,
                    int accountFrom, int accountTo, BigDecimal transferAmount) {
        this.transferId = transferId;
        this.transferTypeId = transferTypeId;
        this.transferStatusId = transferStatusId;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.transferAmount = transferAmount;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public void setTransferTypeId(int transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer transfer = (Transfer) o;
        return transferId == transfer.transferId && transferTypeId == transfer.transferTypeId
                && transferStatusId == transfer.transferStatusId && accountFrom == transfer.accountFrom
                && accountTo == transfer.accountTo && Objects.equals(transferAmount, transfer.transferAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transferId, transferTypeId, transferStatusId, accountFrom, accountTo, transferAmount);
    }
}
