package com.dnamaster10.traincartsticketshop.util.newdatabase.dbaccessorinterfaces;

import com.dnamaster10.traincartsticketshop.util.newdatabase.databaseobjects.GuiEditorDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

import java.util.List;

public interface GuiEditorsDatabaseAccessor {
    List<GuiEditorDatabaseObject> getAllGuiEditorsFromDatabase() throws QueryException;

    void addGuiEditor(int guiId, String uuid) throws ModificationException;
    void removeGuiEditor(int guiId, String uuid) throws ModificationException;
}
