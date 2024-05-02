package com.techelevator.tenmo.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

public class DaoException extends RuntimeException {
    public DaoException() {
        super();
    }
    public DaoException(String message) {
        super(message);
    }
    public DaoException(String message, Exception cause) {
        super(message, cause);
    }
    private void handleDbException(Exception ex, String verb) {
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
}
