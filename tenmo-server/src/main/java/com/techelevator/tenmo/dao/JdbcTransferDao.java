package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private final JdbcTemplate jdbcTemplate;
    private final JdbcAccountDao jdbcAccountDao;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate, JdbcAccountDao jdbcAccountDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcAccountDao = jdbcAccountDao;
    }

    @Override
    public int createTransfer(int transferTypeId, int transferStatusId, int fromUserId,
                              int toUserId, BigDecimal amountToSend) {
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, " +
                "account_from, account_to, amount)" +
                "VALUES (?, ?, ?, ?, ?) RETURNING transfer_id;";
        int fromAccount = jdbcAccountDao.getAccountId(fromUserId);
        int toAccount = jdbcAccountDao.getAccountId(toUserId);
        double amount = amountToSend.doubleValue();
        int transferId = 0;
        try {
            transferId = jdbcTemplate.queryForObject(sql, Integer.class, transferTypeId, transferStatusId,
                    fromAccount, toAccount, amount);

        } catch (DaoException ex) {
            handleDbException(ex, "create transfer");
        }
        return transferId;
    }

    @Override
    public List<Transfer> getTransfersByStatus(int transferStatusId, int accountId) {
        List<Transfer> transferList = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE transfer_status_id = ? " +
                "AND(account_from = ? OR account_to =? );";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferStatusId, accountId, accountId);
            while (results.next()) {
                transferList.add(mapRawToTransfer(results));
            }
        } catch (DaoException ex){
            handleDbException(ex, "get transfers by status");
        }
        return transferList;
    }

    @Override
    public Transfer getTransferByTransferId(int transferId, int userId) {
        return null;
    }

    @Override
    public String requestMoney(BigDecimal amountRequested, int requesterUserId, int requesteeUserId) {
        return null;
    }

    public void handleDbException(Exception ex, String verb) {
        if (ex instanceof CannotGetJdbcConnectionException) {
            throw new DaoException("Could not connect to database: "
                    + ex.getMessage(), ex);
        } else if (ex instanceof BadSqlGrammarException) {
            throw new DaoException("Error in SQL" + ex.getMessage(), ex);
        } else if (ex instanceof DataIntegrityViolationException) {
            throw new DaoException("Could not " + verb + "due to data integrity issues: " + ex.getMessage());
        } else {
            throw new DaoException("Could not " + verb + ex.getMessage());
        }
    }

    private Transfer mapRawToTransfer(SqlRowSet result){
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
