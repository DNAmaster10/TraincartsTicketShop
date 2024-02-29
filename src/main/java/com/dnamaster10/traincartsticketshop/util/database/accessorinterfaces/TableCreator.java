package com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces;

import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;

public interface TableCreator {
    void createTables() throws ModificationException;
}
