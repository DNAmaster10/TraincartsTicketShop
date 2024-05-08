package com.dnamaster10.traincartsticketshop.util.newdatabase.dbaccessorinterfaces;

import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;

public interface DatabaseTableCreator {
    void createTables() throws ModificationException;
}
