package com.dnamaster10.traincartsticketshop.util.database.caches;

import com.dnamaster10.traincartsticketshop.util.database.AccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.GuiEditorsAccessor;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.PlayerAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.GuiEditorsDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.PlayerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

//TODO this needs finishing
public class GuiEditorsCache {
    ConcurrentHashMap<Integer, GuiEditorsDatabaseObject> guiIdEditorsMap;

    public void initialize() throws QueryException {
        GuiEditorsAccessor editorsAccessor = AccessorFactory.getGuiEditorsAccessor();
        guiIdEditorsMap = new ConcurrentHashMap<>(editorsAccessor.getAllGuiEditorsFromDatabase());
    }

    public boolean checkGuiEditorByUuid(int guiId, String uuid) {
        if (!guiIdEditorsMap.containsKey(guiId)) return false;
        return guiIdEditorsMap.get(guiId).editorUuids().contains(uuid);
    }

    public List<String> getEditorUsernamesForGui(int id) {
        if (!guiIdEditorsMap.containsKey(id)) return new ArrayList<>();
        List<String> usernames = new ArrayList<>();
        PlayerAccessor playerAccessor = AccessorFactory.getPlayerAccessor();
        for (String uuid : guiIdEditorsMap.get(id).editorUuids()) {
            PlayerDatabaseObject player = playerAccessor.getPlayerByUuid(uuid);
            usernames.add(player.username());
        }
        return usernames;
    }

    public void addGuiEditor(String uuid, int guiId) {
        GuiEditorsDatabaseObject editors;
        if (guiIdEditorsMap.containsKey(guiId)) editors = guiIdEditorsMap.get(guiId);
        else editors = new GuiEditorsDatabaseObject(guiId, new ArrayList<>());
        if (!editors.editorUuids().contains(uuid)) editors.editorUuids().add(uuid);
        guiIdEditorsMap.put(guiId, editors);
    }

    public void removeGuiEditor(String uuid, int guiId) {
        if (!guiIdEditorsMap.containsKey(guiId)) return;
        GuiEditorsDatabaseObject editors = guiIdEditorsMap.get(guiId);
        editors.editorUuids().remove(uuid);
        if (editors.editorUuids().isEmpty()) guiIdEditorsMap.remove(guiId);
    }
    public void removeGui(int guiId) {
        guiIdEditorsMap.remove(guiId);
    }
}
