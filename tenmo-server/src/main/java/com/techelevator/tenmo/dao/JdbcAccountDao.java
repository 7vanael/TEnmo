package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.Utilities.Utility;
import com.techelevator.tenmo.exception.DaoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.SQLException;

@Component
public class JdbcAccountDao implements AccountDao {

    private final JdbcTemplate jdbcTemplate;
    //private final DaoException daoException;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) { //DaoException daoException) {
        this.jdbcTemplate = jdbcTemplate;
       // this.daoException = daoException;
    }

    @Override
    public BigDecimal getBalanceById(int accountId) {
        String sql = "SELECT balance FROM account WHERE account_id = ?";
        BigDecimal balance = BigDecimal.ZERO;
        try {
            Integer result = jdbcTemplate.queryForObject(sql, Integer.class, accountId);

            if (result != null) {
                balance = BigDecimal.valueOf(result);
            }

        } catch (Exception ex) {
            Utility.handleDbException(ex, "get balance");
        }
        return balance;
    }

    @Override
    public int getAccountId(int userId) {
        String sql = "SELECT account_id FROM account WHERE user_id = ?";
        Integer result = null;

        try {
            result = jdbcTemplate.queryForObject(sql, Integer.class, userId);

        } catch (Exception ex) {
            Utility.handleDbException(ex, "get account id");
        }
        return result;
    }

    @Override
    public BigDecimal updateBalanceById(int accountId, BigDecimal amountChanged) {
        String sql = "UPDATE account SET balance = ? WHERE account_id = ?";
        BigDecimal startingBalance = getBalanceById(accountId);
        BigDecimal newBalance = startingBalance.add(amountChanged);
        double updatedBalance = newBalance.doubleValue();
        BigDecimal convertedBalance = null;

        try {
            Integer result = jdbcTemplate.queryForObject(sql, Integer.class, updatedBalance, accountId);
            convertedBalance = new BigDecimal(result);

        } catch (Exception ex) {
            Utility.handleDbException(ex, "get account id");
        }
        return convertedBalance;
    }

//    public void handleDbException(Exception ex, String verb) {
//        if (ex instanceof CannotGetJdbcConnectionException) {
//            throw new DaoException("Could not connect to database: "
//                    + ex.getMessage(), ex);
//        } else if (ex instanceof BadSqlGrammarException) {
//            throw new DaoException("Error in SQL grammar" + ex.getMessage(), ex);
//        } else if (ex instanceof SQLException) {
//            throw new DaoException("SQL exception" + ex.getMessage(), ex);
//        } else if (ex instanceof DataIntegrityViolationException) {
//            throw new DaoException("Could not " + verb + "due to data integrity issues: " + ex.getMessage());
//        } else {
//            throw new DaoException("Could not " + verb + ex.getMessage());
//        }
//    }

}
