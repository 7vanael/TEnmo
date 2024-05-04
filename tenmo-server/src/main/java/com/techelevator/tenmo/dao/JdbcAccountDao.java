package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.Utilities.Utility;
import com.techelevator.tenmo.exception.DaoException;
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

    public JdbcAccountDao(JdbcTemplate jdbcTemplate, UserDao userDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = userDao;
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

        try {
            int userId = userDao.findIdByUsername(userName);
            int accountId = getAccountId(userId);
            jdbcTemplate.update(sql, newBalance, accountId);

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

    @Override
    public Account getAccountByUserId(int userId) {
        String sql = "SELECT * FROM account WHERE user_id = ?;";
        Account account = new Account();
        try{
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            while(results.next()){
                account = mapRowAccount(results);
            }
        } catch (Exception ex) {
            Utility.handleDbException(ex, "get account by user id");
        }
        return account;
    }

    public String getUsernameByAccountId(int accountId){
        String username = "";
        String sql = "SELECT username FROM account a\n" +
                "JOIN tenmo_user tu ON a.user_id = tu.user_id\n" +
                "WHERE account_id = ?;";

        try {
            username = jdbcTemplate.queryForObject(sql, String.class, accountId);

        } catch (DaoException ex){
            Utility.handleDbException(ex, "get username by account id");
        }
        return username;
    }


    //This row mapper not the same as the TransferRowMapper-
    //was made under influence of a Fellow who was helping us debug.
    public Account mapRowAccount (SqlRowSet rowset){
        return new Account(
                rowset.getInt("account_id"),
                rowset.getInt("user_id"),
                rowset.getBigDecimal("balance")
        );
    }

}
