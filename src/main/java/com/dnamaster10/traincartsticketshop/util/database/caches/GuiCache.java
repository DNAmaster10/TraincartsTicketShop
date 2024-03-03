package com.dnamaster10.traincartsticketshop.util.database.caches;

import com.dnamaster10.traincartsticketshop.util.database.AccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.GuiDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GuiCache {
    private static final List<String> namesList = new ArrayList<>();
    private static final HashMap<Integer, GuiDatabaseObject> idGuiMap = new HashMap<>();
    private static final HashMap<String, GuiDatabaseObject> nameGuiMap = new HashMap<>();

    public static void initialize() throws QueryException {
        GuiAccessor guiAccessor = AccessorFactory.getGuiAccessor();

        List<GuiDatabaseObject> guis = guiAccessor.getGuis();
        for (GuiDatabaseObject gui : guis) {
            namesList.add(gui.name());
            nameGuiMap.put(gui.name(), gui);
            idGuiMap.put(gui.id(), gui);
        }

    }

    public static boolean checkGuiByName(String name) {
        return nameGuiMap.containsKey(name);
    }

    public static boolean checkGuiById(int id) {
        return idGuiMap.containsKey(id);
    }

    public static boolean checkGuiOwnerByUuid(int id, String uuid) {
        if (!idGuiMap.containsKey(id)) return false;
        return idGuiMap.get(id).ownerUuid().equals(uuid);
    }

    public static Integer getGuiIdByName(String name) {
        if (!nameGuiMap.containsKey(name)) return null;
        return nameGuiMap.get(name).id();
    }

    public static String getGuiNameById(int id) {
        if (!idGuiMap.containsKey(id)) return null;
        return idGuiMap.get(id).name();
    }

    public static String getDisplayNameById(int id) {
        if (!idGuiMap.containsKey(id)) return null;
        return idGuiMap.get(id).displayName();
    }

    public static void updateGuiName(int id, String newName) {
        if (!idGuiMap.containsKey(id)) return;
        GuiDatabaseObject gui = idGuiMap.get(id);
        namesList.remove(gui.name());
        idGuiMap.remove(id);
        nameGuiMap.remove(gui.name());
        GuiDatabaseObject newGui = new GuiDatabaseObject(
                gui.id(),
                newName,
                gui.displayName(),
                gui.ownerUuid()
        );
        namesList.add(newName);
        idGuiMap.put(id, newGui);
        nameGuiMap.put(newName, newGui);
    }

    public static void updateGuiDisplayName(int id, String displayName) {

    }
}
