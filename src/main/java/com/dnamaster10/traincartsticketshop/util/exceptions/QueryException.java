package com.dnamaster10.traincartsticketshop.util.exceptions;

import java.sql.SQLException;

public class QueryException extends SQLException {
    //Data Query Language Exceptions
    //For statements which *only* read the database contents (eg SELECT)
    public QueryException(SQLException cause) {
        super(cause);
    }
}
