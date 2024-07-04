package com.dnamaster10.traincartsticketshop.util.database.dbaccessorinterfaces;

import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;

public interface DatabaseTableCreator {
    void createTables() throws ModificationException;
}
