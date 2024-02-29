package com.dnamaster10.traincartsticketshop.util.exceptions;

import java.sql.SQLException;

public class ModificationException extends SQLException {
    //Data Manipulation Language Exceptions
    //For statements which alter the database (eg INSERT or UPDATE)
    public ModificationException(SQLException cause) {
        super(cause);
    }
}
