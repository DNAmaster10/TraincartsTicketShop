package com.dnamaster10.traincartsticketshop.util.database.caches;

import com.dnamaster10.traincartsticketshop.util.database.AccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.GuiEditorsAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.GuiEditorsDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

import java.util.concurrent.ConcurrentHashMap;

//TODO this needs finishing
public class GuiEditorsCache {
    ConcurrentHashMap<Integer, GuiEditorsDatabaseObject> guiIdEditorsMap;

    public void initialize() throws QueryException {
        GuiEditorsAccessor editorsAccessor = AccessorFactory.getGuiEditorsAccessor();
        guiIdEditorsMap = new ConcurrentHashMap<>(editorsAccessor.getAllGuiEditorsFromDatabase());
    }

    public boolean checkGuiEditorByUuid(int guiId, String uuid) throws QueryException {
        if (!guiIdEditorsMap.containsKey(guiId)) return false;
        return guiIdEditorsMap.get(guiId).editorUuids().contains(uuid);
    }

    public int getTotalEditors(int guiId) {
        if (!guiIdEditorsMap.containsKey(guiId)) return 0;
        return guiIdEditorsMap.get(guiId).editorUuids().size();
    }

    public void addGuiEditor(int guiId, String uuid) {

    }
}
