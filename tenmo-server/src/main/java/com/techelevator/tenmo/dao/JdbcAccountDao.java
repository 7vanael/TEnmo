package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public BigDecimal getBalance(int accountId) {
        String sql = "SELECT balance FROM account WHERE account_id = ?";
        BigDecimal balance = BigDecimal.ZERO;
        try{
            Integer result = jdbcTemplate.queryForObject(sql, Integer.class, accountId);

            if (result != null){
                balance = BigDecimal.valueOf(result);
            }

        } catch (NullPointerException | EmptyResultDataAccessException e) {
            throw new DaoException("Unable to retrieve balance");
        }

        return balance;
    }

    @Override
    public int getAccountId(int userId) {
        String sql = "SELECT account_id FROM account WHERE user_id = ?";
        Integer result;

        try {
            result = jdbcTemplate.queryForObject(sql, Integer.class, userId);

        } catch (EmptyResultDataAccessException e){
            throw new DaoException("")
        }
        return 0;
    }

    @Override
    public BigDecimal updateBalanceById(int accountId, BigDecimal amountChanged) {
        return null;
    }
}
