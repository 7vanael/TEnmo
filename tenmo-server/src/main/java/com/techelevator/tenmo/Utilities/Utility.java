package com.techelevator.tenmo.Utilities;

import com.techelevator.tenmo.exception.DaoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import java.sql.SQLException;

public class Utility {

    public static void handleDbException(Exception ex, String verb) {
        if (ex instanceof CannotGetJdbcConnectionException) {
            throw new DaoException("Could not connect to database: "
                    + ex.getMessage(), ex);
        } else if (ex instanceof BadSqlGrammarException) {
            throw new DaoException("Error in SQL grammar" + ex.getMessage(), ex);
        } else if (ex instanceof SQLException) {
            throw new DaoException("SQL exception" + ex.getMessage(), ex);
        } else if (ex instanceof DataIntegrityViolationException) {
            throw new DaoException("Could not " + verb + "due to data integrity issues: " + ex.getMessage());
        } else {
            throw new DaoException("Could not " + verb + ex.getMessage());
        }
    }
}
