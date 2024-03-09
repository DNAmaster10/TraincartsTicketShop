package com.dnamaster10.traincartsticketshop.util.database;

import com.dnamaster10.traincartsticketshop.util.database.caches.GuiCache;
import com.dnamaster10.traincartsticketshop.util.database.caches.GuiEditorsCache;
import com.dnamaster10.traincartsticketshop.util.database.caches.PlayerCache;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

public class DatabaseAccessor {
    private static final GuiCache guiCache;
    private static final PlayerCache playerCache;
    private static final GuiEditorsCache guiEditorsCache;

    static {
        guiCache = new GuiCache();
        playerCache = new PlayerCache();
        guiEditorsCache = new GuiEditorsCache();
    }

    public void initializeCaches() throws QueryException {
        guiCache.initialize();
        playerCache.initialize();
        guiEditorsCache.initialize();
    }

    public GuiCache getGuiCache() {
        return guiCache;
    }

    public PlayerCache getPlayerCache() {
        return playerCache;
    }

    public GuiEditorsCache getGuiEditorsCache() {
        return guiEditorsCache;
    }
}
