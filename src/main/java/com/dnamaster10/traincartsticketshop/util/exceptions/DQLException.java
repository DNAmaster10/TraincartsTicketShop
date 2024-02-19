package com.dnamaster10.traincartsticketshop.util.exceptions;

import java.sql.SQLException;

public class DQLException extends SQLException {
    //Data Query Language Exceptions
    //For statements which *only* read the database contents (eg SELECT)
    public DQLException(SQLException cause) {
        super(cause);
    }
}
