package com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces;

import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.GuiDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.GuiEditorDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

import java.util.List;

public interface GuiEditorsAccessor {
    boolean checkGuiEditorByUuid(int guiId, String uuid);

    List<GuiEditorDatabaseObject> getAllGuiEditorsFromDatabase() throws QueryException;
    List<String> getEditorUsernames(int guiId);
    //Returns a list of guis where a player is a registered editor
    List<GuiDatabaseObject> getGuisEditableByEditor(String uuid);

    void addGuiEditor(int guiId, String uuid) throws ModificationException;
    void removeGuiEditor(int guiId, String uuid) throws ModificationException;
}
