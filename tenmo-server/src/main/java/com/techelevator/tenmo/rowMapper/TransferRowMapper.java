package com.techelevator.tenmo.rowMapper;
import org.springframework.jdbc.core.RowMapper;
import com.techelevator.tenmo.model.Transfer;

import java.sql.SQLException;
import java.sql.ResultSet;

public class TransferRowMapper implements RowMapper<Transfer>{
    @Override
    public Transfer mapRow(ResultSet result , int i) throws SQLException{
        Transfer transfer = new Transfer();
        transfer.setTransferId(result.getInt("transfer_id"));
        transfer.setTransferTypeId(result.getInt("transfer_type_id"));
        transfer.setTransferStatusId(result.getInt("transfer_status_id"));
        transfer.setAccountFrom(result.getInt("account_from"));
        transfer.setAccountTo(result.getInt("account_to"));
        transfer.setTransferAmount(result.getBigDecimal("amount"));
        return transfer;
    }
}
