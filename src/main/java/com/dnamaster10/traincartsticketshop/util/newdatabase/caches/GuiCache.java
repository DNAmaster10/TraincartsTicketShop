package com.dnamaster10.traincartsticketshop.util.newdatabase.caches;

import com.dnamaster10.traincartsticketshop.util.newdatabase.accessors.GuiDataAccessor;
import com.dnamaster10.traincartsticketshop.util.newdatabase.databaseobjects.GuiDatabaseObject;
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
    ConcurrentHashMap<String, List<GuiDatabaseObject>> ownerGuiMap = new ConcurrentHashMap<>();

    public void initialize() throws QueryException {
        GuiDataAccessor guiAccessor = new GuiDataAccessor();
        List<GuiDatabaseObject> guis = guiAccessor.getGuisFromDatabase();
        for (GuiDatabaseObject gui : guis) {
            guiNames.add(gui.name());
            idGuiMap.put(gui.id(), gui);
            nameGuiMap.put(gui.name().toLowerCase(), gui);
            if (ownerGuiMap.containsKey(gui.ownerUuid())) ownerGuiMap.get(gui.ownerUuid()).add(gui);
            else {
                List<GuiDatabaseObject> ownedGuis = new ArrayList<>();
                ownedGuis.add(gui);
                ownerGuiMap.put(gui.ownerUuid(), ownedGuis);
            }
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
    public List<GuiDatabaseObject> getGuisOwnedBy(String uuid) {
        if (!ownerGuiMap.containsKey(uuid)) return new ArrayList<>();
        return ownerGuiMap.get(uuid);
    }

    public void updateGuiName(int id, String newName) {
        GuiDatabaseObject gui = idGuiMap.get(id);
        String oldName = gui.name();
        gui.setName(newName);
        guiNames.remove(oldName);
        guiNames.add(newName);
        nameGuiMap.remove(oldName.toLowerCase());
        nameGuiMap.put(newName.toLowerCase(), gui);

    }
    public void updateGuiDisplayName(int id, String displayName) {
        idGuiMap.get(id).setDisplayName(displayName);
    }
    public void updateGuiOwner(int id, String uuid) {
        GuiDatabaseObject gui = idGuiMap.get(id);
        String oldOwner = gui.ownerUuid();

        List<GuiDatabaseObject> oldOwnerGuis = ownerGuiMap.get(oldOwner);
        oldOwnerGuis.remove(gui);
        if (oldOwnerGuis.isEmpty()) ownerGuiMap.remove(oldOwner);

        gui.setOwnerUuid(uuid);

        if (!ownerGuiMap.containsKey(uuid)) ownerGuiMap.put(uuid, new ArrayList<>());
        List<GuiDatabaseObject> newOwnerGuis = ownerGuiMap.get(uuid);
        newOwnerGuis.add(gui);
    }
    public void addGui(GuiDatabaseObject gui) {
        guiNames.add(gui.name());
        nameGuiMap.put(gui.name().toLowerCase(), gui);
        idGuiMap.put(gui.id(), gui);

        if (ownerGuiMap.containsKey(gui.ownerUuid())) ownerGuiMap.get(gui.ownerUuid()).add(gui);
        else {
            List<GuiDatabaseObject> ownerGuis = new ArrayList<>();
            ownerGuis.add(gui);
            ownerGuiMap.put(gui.ownerUuid(), ownerGuis);
        }
    }
    public void deleteGuiById(int id) {
        GuiDatabaseObject gui = idGuiMap.get(id);
        guiNames.remove(gui.name());
        nameGuiMap.remove(gui.name().toLowerCase());
        idGuiMap.remove(id);

        List<GuiDatabaseObject> ownerGuis = ownerGuiMap.get(gui.ownerUuid());
        ownerGuis.remove(gui);
        if (ownerGuis.isEmpty()) ownerGuiMap.remove(gui.ownerUuid());
    }

}
