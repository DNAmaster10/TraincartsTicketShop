package com.dnamaster10.traincartsticketshop.util.database;

import com.dnamaster10.traincartsticketshop.util.database.caches.GuiCache;

public class DatabaseAccessor {
    private static final GuiCache guiCache;

    static {
        guiCache = new GuiCache();
    }

    public GuiCache getGuiCache() {
        return guiCache;
    }
}
