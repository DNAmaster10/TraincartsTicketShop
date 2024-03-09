package com.dnamaster10.traincartsticketshop.util.database.caches;

import com.dnamaster10.traincartsticketshop.util.database.AccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.GuiDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class GuiCache {
    List<String> guiNames = new CopyOnWriteArrayList<>();
    ConcurrentHashMap<Integer, GuiDatabaseObject> idGuiMap = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, GuiDatabaseObject> nameGuiMap = new ConcurrentHashMap<>();

    public void initialize() throws QueryException {
        GuiAccessor guiAccessor = AccessorFactory.getGuiAccessor();
        List<GuiDatabaseObject> guis = guiAccessor.getGuisFromDatabase();
        for (GuiDatabaseObject gui : guis) {
            guiNames.add(gui.name());
            idGuiMap.put(gui.id(), gui);
            nameGuiMap.put(gui.name().toLowerCase(), gui);
        }
    }

    public boolean checkGuiByName(String name) {
        return nameGuiMap.containsKey(name.toLowerCase());
    }
    public boolean checkGuiById(int id) {
        return idGuiMap.containsKey(id);
    }
    public boolean checkGuiOwnerByUuid(int id, String uuid) {
        GuiDatabaseObject gui = idGuiMap.get(id);
        return gui.ownerUuid().equalsIgnoreCase(uuid);
    }

    public GuiDatabaseObject getGuiById(int id) {
        return idGuiMap.get(id);
    }
    public int getGuiIdByName(String name) {
        //TODO maybe we could return a gui database object instead of getting id and then info?
        GuiDatabaseObject gui = nameGuiMap.get(name.toLowerCase());
        return gui.id();
    }
    public String getGuiNameById(int id) {
        GuiDatabaseObject gui = idGuiMap.get(id);
        return gui.name();
    }
    public String getDisplayNameById(int id) {
        GuiDatabaseObject gui = idGuiMap.get(id);
        return gui.displayName();
    }
    public List<String> getPartialNameMatches(String inputString) {
        return StringUtil.copyPartialMatches(inputString, guiNames, new ArrayList<>());
    }

    public void updateGuiName(int id, String newName) {
        GuiDatabaseObject oldGui = idGuiMap.get(id);
        GuiDatabaseObject newGui = new GuiDatabaseObject(
                oldGui.id(),
                newName,
                oldGui.displayName(),
                oldGui.ownerUuid()
        );
        guiNames.remove(oldGui.name());
        nameGuiMap.remove(oldGui.name());

        guiNames.add(newName);
        nameGuiMap.put(newName.toLowerCase(), newGui);
        idGuiMap.put(oldGui.id(), newGui);
    }
    public void updateGuiDisplayName(int id, String displayName) {
        GuiDatabaseObject oldGui = idGuiMap.get(id);
        GuiDatabaseObject newGui = new GuiDatabaseObject(
                oldGui.id(),
                oldGui.name(),
                displayName,
                oldGui.ownerUuid()
        );
        nameGuiMap.put(oldGui.name().toLowerCase(), newGui);
        idGuiMap.put(id, newGui);
    }
    public void updateGuiOwner(int id, String uuid) {
        GuiDatabaseObject oldGui = idGuiMap.get(id);
        GuiDatabaseObject newGui = new GuiDatabaseObject(
                oldGui.id(),
                oldGui.name(),
                oldGui.displayName(),
                uuid
        );
        nameGuiMap.put(oldGui.name().toLowerCase(), newGui);
        idGuiMap.put(id, newGui);
    }
    public void addGui(GuiDatabaseObject gui) {
        guiNames.add(gui.name());
        nameGuiMap.put(gui.name().toLowerCase(), gui);
        idGuiMap.put(gui.id(), gui);
    }
    public void deleteGuiById(int id) {
        GuiDatabaseObject gui = idGuiMap.get(id);
        guiNames.remove(gui.name());
        nameGuiMap.remove(gui.name().toLowerCase());
        idGuiMap.remove(id);
    }
























}
