package com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces;

import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.GuiDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

import java.util.List;

public interface GuiAccessor {
    boolean checkGuiByName(String name) throws QueryException;
    boolean checkGuiById(int id) throws QueryException;
    boolean checkGuiOwnerByUuid(int guiId, String ownerUuid) throws QueryException;
    boolean playerCanEdit(int guiId, String uuid) throws QueryException;

    List<GuiDatabaseObject> getGuis() throws QueryException;
    Integer getGuiIdByName(String name) throws QueryException;
    String getGuiNameById(int id) throws QueryException;
    int getHighestPageNumber(int guiId) throws QueryException;
    String getDisplayNameById(int guiId) throws QueryException;
    String getOwnerUsername(int guiID) throws QueryException;

    void updateGuiName(int guiId, String newName) throws ModificationException;
    void updateGuiDisplayName(int guiId, String colouredDisplayName, String rawDisplayName) throws ModificationException;
    void updateGuiOwner(int guiId, String uuid) throws ModificationException;
    void addGui(String name, String colouredDisplayName, String rawDisplayName, String ownerUuid) throws ModificationException;
    void insertPage(int guiId, int currentPage) throws ModificationException;
    void deleteGuiById(int guiId) throws ModificationException;
    void deletePage(int guiId, int page) throws ModificationException;
}
