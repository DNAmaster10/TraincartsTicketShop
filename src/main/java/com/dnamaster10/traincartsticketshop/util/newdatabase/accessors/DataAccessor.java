package com.dnamaster10.traincartsticketshop.util.newdatabase.accessors;

import com.dnamaster10.traincartsticketshop.util.newdatabase.caches.GuiCache;
import com.dnamaster10.traincartsticketshop.util.newdatabase.caches.GuiEditorsCache;
import com.dnamaster10.traincartsticketshop.util.newdatabase.caches.PlayerCache;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;

public class DataAccessor {
    private static final GuiCache guiCache = new GuiCache();
    private static final PlayerCache playerCache = new PlayerCache();
    private static final GuiEditorsCache guiEditorsCache = new GuiEditorsCache();

    public void initializeCaches() throws QueryException {
        playerCache.initialize();
        guiCache.initialize();
        guiEditorsCache.initialize();
    }

    public GuiCache getGuiCache() {return guiCache;}
    public PlayerCache getPlayerCache() {return playerCache;}
    public GuiEditorsCache getGuiEditorsCache() {return guiEditorsCache;}
}
