package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.Utilities.Utility;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.rowMapper.AccountRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao {

    private final JdbcTemplate jdbcTemplate;
    private final UserDao userDao;
    //private final DaoException daoException;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate, UserDao userDao) { //DaoException daoException) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = userDao;
       // this.daoException = daoException;
    }

    @Override
    public BigDecimal getBalanceByUser(String userName) {
        String sql = "SELECT balance FROM account JOIN tenmo_user ON tenmo_user.user_id = account.user_id " +
                "WHERE username = ?";
        BigDecimal balance = BigDecimal.ZERO;
        try {
            Integer result = jdbcTemplate.queryForObject(sql, Integer.class, userName);

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
    public BigDecimal updateBalanceByUser(String userName, BigDecimal amountChanged) {
        String sql = "UPDATE account SET balance = ? WHERE account_id = ?";
        BigDecimal startingBalance = getBalanceByUser(userName);
        BigDecimal newBalance = startingBalance.add(amountChanged);
        //converted to double to avoid sending BigDecimal into Sql
        //double updatedBalance = newBalance.doubleValue();

        try {
            int userId = userDao.findIdByUsername(userName);
            int accountId = getAccountId(userId);
            jdbcTemplate.update(sql, newBalance, accountId);
//            convertedBalance = new BigDecimal(result);

        } catch (Exception ex) {
            Utility.handleDbException(ex, "update balance by user");
        }
        return getBalanceByUser(userName);
    }

    @Override
    public Account getAccountByAccountId(int accountId){
        String sql = "SELECT * FROM account WHERE account_id = ?";
        Account result = null;
        try {
            SqlRowSet resultSet = jdbcTemplate.queryForRowSet(sql, accountId);
            while(resultSet.next()){
                result = mapRowAccount(resultSet);
            }
        }catch (Exception ex) {
            Utility.handleDbException(ex, "get account by account id ");
        }
        return result;
    }

    public Account mapRowAccount (SqlRowSet rowset){
        return new Account(
                rowset.getInt("account_id"),
                rowset.getInt("user_id"),
                rowset.getBigDecimal("balance")
        );
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
