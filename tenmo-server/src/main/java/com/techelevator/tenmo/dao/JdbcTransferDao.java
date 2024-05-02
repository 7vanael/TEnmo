package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.Utilities.Utility;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.rowMapper.TransferRowMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
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
            Utility.handleDbException(ex, "create transfer");
        }
        return transferId;
    }

    @Override
    public List<Transfer> getTransfersByStatus(int transferStatusId, int accountId) {
        List<Transfer> transferList = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE transfer_status_id = ? " +
                "AND(account_from = ? OR account_to =? );";
        try {
            transferList = jdbcTemplate.query(sql, new TransferRowMapper(), transferStatusId, accountId, accountId);
        } catch (DaoException ex){
            Utility.handleDbException(ex, "get transfers by status");
        }
        return transferList;
    }

    @Override
    public Transfer getTransferByTransferId(int transferId, int userId) {
        Transfer transfer = new Transfer();
        String sql = "SELECT * FROM transfer WHERE transfer_id = ? AND (account_from = " +
                "(SELECT account_id FROM account WHERE user_id = ?) OR account_to = " +
                "(SELECT account_id FROM account WHERE user_id = ?))";
        try{
            transfer = jdbcTemplate.queryForObject(sql, new TransferRowMapper(), transferId, userId, userId);
        }catch (DaoException ex){
            Utility.handleDbException(ex, "get transfer by transfer ID");
        }
        return transfer;
    }

    /*

    Ready for a challenge??

    */

    @Override
    public String requestMoney(BigDecimal amountRequested, int requesterUserId, int requesteeUserId) {
        return null;
    }


//    public void handleDbException(Exception ex, String verb) {
//        if (ex instanceof CannotGetJdbcConnectionException) {
//            throw new DaoException("Could not connect to database: "
//                    + ex.getMessage(), ex);
//        } else if (ex instanceof BadSqlGrammarException) {
//            throw new DaoException("Error in SQL grammar" + ex.getMessage(), ex);
//        }else if (ex instanceof SQLException){
//            throw new DaoException("SQL exception" + ex.getMessage(), ex);
//        } else if (ex instanceof DataIntegrityViolationException) {
//            throw new DaoException("Could not " + verb + "due to data integrity issues: " + ex.getMessage());
//        } else {
//            throw new DaoException("Could not " + verb + ex.getMessage());
//        }
//    }


}
