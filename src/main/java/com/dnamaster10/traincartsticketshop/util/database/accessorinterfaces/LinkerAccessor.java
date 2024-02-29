package com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces;

import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.LinkerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

import java.util.List;

public interface LinkerAccessor {
    LinkerDatabaseObject[] getLinkersByGuiId(int guiId, int page) throws QueryException;
    LinkerDatabaseObject[] searchLinkers(int guiId, int offset, String searchTerm) throws QueryException;
    int getTotalLinkers(int guiId) throws QueryException;
    int getTotalLinkerSearchResults(int guiId, String searchTerm) throws QueryException;

    void saveLinkerPage(int guiId, int page, List<LinkerDatabaseObject> linkers) throws ModificationException;
}
