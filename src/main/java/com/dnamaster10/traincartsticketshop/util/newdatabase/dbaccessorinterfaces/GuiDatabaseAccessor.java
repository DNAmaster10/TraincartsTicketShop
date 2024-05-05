package com.dnamaster10.traincartsticketshop.util.newdatabase.dbaccessorinterfaces;

import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.GuiDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

import java.util.List;

public interface GuiDatabaseAccessor {
    List<GuiDatabaseObject> getGuisFromDatabase() throws QueryException;
    int getHighestPageNumber(int guiId) throws QueryException;

    void updateGuiName(int guiId, String newName) throws ModificationException;
    void updateGuiDisplayName(int guiId, String colouredDisplayName, String rawDisplayName) throws ModificationException;
    void updateGuiOwner(int guiId, String uuid) throws ModificationException;
    //Returns an Integer indicating the ID of the inserted row, used within the cache.
    Integer addGui(String name, String colouredDisplayName, String rawDisplayName, String ownerUuid) throws ModificationException;
    void insertPage(int guiId, int currentPage) throws ModificationException;
    void deleteGuiById(int guiId) throws ModificationException;
    void deletePage(int guiId, int page) throws ModificationException;
}
