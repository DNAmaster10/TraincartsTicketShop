package com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces;

import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.GuiEditorsDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

import java.util.HashMap;
import java.util.List;

public interface GuiEditorsAccessor {
    boolean checkGuiEditorByUuid(int guiId, String uuid) throws QueryException;

    HashMap<Integer, GuiEditorsDatabaseObject> getAllGuiEditorsFromDatabase() throws QueryException;
    List<String> getEditorUsernames(int guiId, int startIndex, int limit) throws QueryException;


    void addGuiEditor(int guiId, String uuid) throws ModificationException;
    void removeGuiEditor(int guiId, String uuid) throws ModificationException;
}
