package com.dnamaster10.traincartsticketshop.util.newdatabase.accessors;

import com.dnamaster10.traincartsticketshop.util.newdatabase.databaseobjects.GuiDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import com.dnamaster10.traincartsticketshop.util.newdatabase.DatabaseAccessorFactory;
import com.dnamaster10.traincartsticketshop.util.newdatabase.dbaccessorinterfaces.GuiDatabaseAccessor;

import java.util.List;

public class GuiDataAccessor extends DataAccessor {
    private final GuiDatabaseAccessor guiDatabaseAccessor = DatabaseAccessorFactory.getGuiDatabaseAccessor();

    public boolean checkGuiByName(String name) {
        return getGuiCache().checkGuiByName(name);
    }
    public boolean checkGuiById(int id) {
        return getGuiCache().checkGuiById(id);
    }
    public boolean checkGuiOwnerByUuid(int guiId, String ownerUuid) {
        return getGuiCache().checkGuiOwnerByUuid(guiId, ownerUuid);
    }
    public boolean playerCanEdit(int guiId, String uuid) {
        return getGuiCache().checkGuiOwnerByUuid(guiId, uuid) || getGuiEditorsCache().checkGuiEditorByUuid(guiId, uuid);
    }

    public List<GuiDatabaseObject> getGuisFromDatabase() throws QueryException {
        return guiDatabaseAccessor.getGuisFromDatabase();
    }
    public Integer getGuiIdByName(String name) {
        return getGuiCache().getGuiIdByName(name);
    }
    public String getGuiNameById(int id) {
        return getGuiCache().getGuiNameById(id);
    }
    public int getHighestPageNumber(int guiId) throws QueryException {
        return guiDatabaseAccessor.getHighestPageNumber(guiId);
    }
    public String getDisplayName(int guiId) {
        return getGuiCache().getDisplayNameById(guiId);
    }
    public String getOwnerUsername(int guiId) {
        String uuid = getGuiCache().getGuiById(guiId).ownerUuid();
        return getPlayerCache().getPlayerByUuid(uuid).username();
    }
    public List<String> getPartialNameMatches(String argument) {
        return getGuiCache().getPartialNameMatches(argument);
    }
    public List<GuiDatabaseObject> getGuisOwnerBy(String uuid) {
        return getGuiCache().getGuisOwnedBy(uuid);
    }
    public List<GuiDatabaseObject> getGuisEditableBy(String uuid) {
        //Returns a list of guis which a player is able to edit. Note that this includes both guis the player owns
        //and also guis the player is a registered editor of. The GuiEditorAccessor must be used to *only* get guis
        //which a player is a registered editor of.
        List<GuiDatabaseObject> ownedGuis = getGuisOwnerBy(uuid);
        List<GuiDatabaseObject> editorGuis = getGuiEditorsCache().getGuisEditableByEditor(uuid);
        ownedGuis.addAll(editorGuis);
        return ownedGuis;
    }
    public GuiDatabaseObject getGuiById(int guiId) {
        return getGuiCache().getGuiById(guiId);
    }

    public void updateGuiDisplayName(int guiId, String colouredDisplayName, String rawDisplayName) throws ModificationException {
        guiDatabaseAccessor.updateGuiDisplayName(guiId, colouredDisplayName, rawDisplayName);
        getGuiCache().updateGuiDisplayName(guiId, colouredDisplayName);
    }
    public void updateGuiOwner(int guiId, String uuid) throws ModificationException {
        guiDatabaseAccessor.updateGuiOwner(guiId, uuid);
        getGuiCache().updateGuiOwner(guiId, uuid);
    }

    public void addGui(String name, String colouredDisplayName, String rawDisplayName, String ownerUuid) throws ModificationException {
        Integer guiId = guiDatabaseAccessor.addGui(name, colouredDisplayName, rawDisplayName, ownerUuid);
        if (guiId == null) return;
        getGuiCache().addGui(new GuiDatabaseObject(guiId, name, colouredDisplayName, ownerUuid));
    }
    public void insertPage(int guiId, int currentPage) throws ModificationException {
        guiDatabaseAccessor.insertPage(guiId, currentPage);
    }
    public void deleteGuiById(int id) throws ModificationException {
        guiDatabaseAccessor.deleteGuiById(id);
        getGuiCache().deleteGuiById(id);
        getGuiEditorsCache().removeGui(id);
    }
    public void deletePage(int guiId, int page) throws ModificationException {
        guiDatabaseAccessor.deletePage(guiId, page);
    }
}