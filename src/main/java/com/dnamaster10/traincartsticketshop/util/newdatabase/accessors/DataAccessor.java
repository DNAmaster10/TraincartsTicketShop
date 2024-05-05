package com.dnamaster10.traincartsticketshop.util.newdatabase.accessors;

import com.dnamaster10.traincartsticketshop.util.database.caches.GuiCache;
import com.dnamaster10.traincartsticketshop.util.database.caches.GuiEditorsCache;
import com.dnamaster10.traincartsticketshop.util.database.caches.PlayerCache;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

public class DataAccessor {
    private static final GuiCache guiCache = new GuiCache();
    private static final PlayerCache playerCache = new PlayerCache();
    private static final GuiEditorsCache guiEditorsCache = new GuiEditorsCache();

    public void initializeCaches() throws QueryException {
        guiCache.initialize();
        playerCache.initialize();
        guiEditorsCache.initialize();
    }

    public GuiCache getGuiCache() {return guiCache;}
    public PlayerCache getPlayerCache() {return playerCache;}
    public GuiEditorsCache getGuiEditorsCache() {return guiEditorsCache;}
}
