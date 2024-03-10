package com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces;

import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.GuiDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

import java.util.List;

public interface GuiAccessor {
    boolean checkGuiByName(String name);
    boolean checkGuiById(int id);
    boolean checkGuiOwnerByUuid(int guiId, String ownerUuid);
    boolean playerCanEdit(int guiId, String uuid);

    List<GuiDatabaseObject> getGuisFromDatabase() throws QueryException;
    Integer getGuiIdByName(String name);
    String getGuiNameById(int id);
    int getHighestPageNumber(int guiId) throws QueryException;
    String getDisplayNameById(int guiId);
    String getOwnerUsername(int guiID);
    List<String> getPartialNameMatches(String match);
    List<GuiDatabaseObject> getGuisOwnedBy(String uuid);
    List<GuiDatabaseObject> getGuisEditableBy(String uuid);
    GuiDatabaseObject getGuiById(int guiId);

    void updateGuiName(int guiId, String newName) throws ModificationException;
    void updateGuiDisplayName(int guiId, String colouredDisplayName, String rawDisplayName) throws ModificationException;
    void updateGuiOwner(int guiId, String uuid) throws ModificationException;
    void addGui(String name, String colouredDisplayName, String rawDisplayName, String ownerUuid) throws ModificationException;
    void insertPage(int guiId, int currentPage) throws ModificationException;
    void deleteGuiById(int guiId) throws ModificationException;
    void deletePage(int guiId, int page) throws ModificationException;
}
