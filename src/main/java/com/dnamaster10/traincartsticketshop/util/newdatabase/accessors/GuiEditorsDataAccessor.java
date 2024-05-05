package com.dnamaster10.traincartsticketshop.util.newdatabase.accessors;

import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.GuiDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.GuiEditorDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import com.dnamaster10.traincartsticketshop.util.newdatabase.DatabaseAccessorFactory;
import com.dnamaster10.traincartsticketshop.util.newdatabase.dbaccessorinterfaces.GuiEditorsDatabaseAccessor;

import java.util.List;

public class GuiEditorsDataAccessor extends DataAccessor {
    private final GuiEditorsDatabaseAccessor guiEditorsDatabaseAccessor = DatabaseAccessorFactory.getGuiEditorsDatabaseAccessor();

    public boolean checkGuiEditorByUuid(int guiId, String uuid) {
        return getGuiEditorsCache().checkGuiEditorByUuid(guiId, uuid);
    }

    public List<GuiEditorDatabaseObject> getAllGuiEditorsFromDatabase() throws QueryException {
        return guiEditorsDatabaseAccessor.getAllGuiEditorsFromDatabase();
    }
    public List<String> getEditorUsernames(int guiId) {
        return getGuiEditorsCache().getEditorUsernamesForGui(guiId);
    }
    public List<GuiDatabaseObject> getGuisEditableByEditor(String uuid) {
        //Note that this does not return guis the given player owns
        return getGuiEditorsCache().getGuisEditableByEditor(uuid);
    }

    public void addGuiEditor(int guiId, String uuid) throws ModificationException {
        getGuiEditorsCache().addGuiEditor(uuid, guiId);
        guiEditorsDatabaseAccessor.addGuiEditor(guiId, uuid);
    }
    public void removeGuiEditor(int guiId, String uuid) throws ModificationException {
        getGuiEditorsCache().removeGuiEditor(uuid, guiId);
        guiEditorsDatabaseAccessor.removeGuiEditor(guiId, uuid);
    }
}
