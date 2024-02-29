package com.dnamaster10.traincartsticketshop.util.database;

import com.dnamaster10.traincartsticketshop.util.database.accessorinterfaces.*;
import com.dnamaster10.traincartsticketshop.util.database.mariadb.*;

import java.util.Objects;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class AccessorFactory {
    private static final String databaseType;
    static {
        if (getPlugin().getConfig().getString("database.type") == null) {
            databaseType = null;
            getPlugin().getLogger().severe("Please set a database type in the config");
            getPlugin().disable();
        } else {
            databaseType = Objects.requireNonNull(getPlugin().getConfig().getString("database.type")).toLowerCase();
            if (!databaseType.equals("mariadb")) {
                getPlugin().getLogger().severe("Database type \"" + databaseType + "\" is not supported");
                getPlugin().disable();
            }
        }
    }

    public static PlayerAccessor getPlayerAccessor() {
        switch (databaseType) {
            case "mariadb" -> {
                return new MariaDBPlayerAccessor();
            }
        }
        throw new IllegalArgumentException("Database type \"" + databaseType + "\" is not supported");
    }

    public static GuiAccessor getGuiAccessor(){
        switch (databaseType) {
            case "mariadb" -> {
                return new MariaDBGuiAccessor();
            }
        }
        throw new IllegalArgumentException("Database type \"" + databaseType + "\" is not supported");
    }

    public static TicketAccessor getTicketAccessor() {
        switch (databaseType) {
            case "mariadb" -> {
                return new MariaDBTicketAccessor();
            }
        }
        throw new IllegalArgumentException("Database type \"" + databaseType + "\" is not supported");
    }

    public static LinkAccessor getLinkAccessor() {
        switch (databaseType) {
            case "mariadb" -> {
                return new MariaDBLinkAccessor();
            }
        }
        throw new IllegalArgumentException("Database type \"" + databaseType + "\" is not supported");
    }

    public static GuiEditorsAccessor getGuiEditorsAccessor() {
        switch (databaseType) {
            case "mariadb" -> {
                return new MariaDBGuiEditorsAccessor();
            }
        }
        throw new IllegalArgumentException("Database type \"" + databaseType + "\" is not supported");
    }

    public static TableCreator getTableCreator() {
        switch (databaseType) {
            case "mariadb" -> {
                return new MariaDBTableCreator();
            }
        }
        throw new IllegalArgumentException("Database type \"" + databaseType + "\" is not supported");
    }
}
