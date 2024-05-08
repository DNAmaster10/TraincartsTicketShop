package com.dnamaster10.traincartsticketshop.util.newdatabase;

import com.dnamaster10.traincartsticketshop.util.newdatabase.dbaccessorinterfaces.GuiDatabaseAccessor;
import com.dnamaster10.traincartsticketshop.util.newdatabase.dbaccessorinterfaces.GuiEditorsDatabaseAccessor;
import com.dnamaster10.traincartsticketshop.util.newdatabase.dbaccessorinterfaces.PlayerDatabaseAccessor;
import com.dnamaster10.traincartsticketshop.util.newdatabase.mariadb.MariaGuiDatabaseAccessor;
import com.dnamaster10.traincartsticketshop.util.newdatabase.mariadb.MariaGuiEditorsAccessor;
import com.dnamaster10.traincartsticketshop.util.newdatabase.mariadb.MariaPlayerAccessor;
import com.dnamaster10.traincartsticketshop.util.newdatabase.sqlite.SQLiteGuiDatabaseAccessor;
import com.dnamaster10.traincartsticketshop.util.newdatabase.sqlite.SQLiteGuiEditorsAccessor;
import com.dnamaster10.traincartsticketshop.util.newdatabase.sqlite.SQLitePlayerAccessor;

import java.util.Objects;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class DatabaseAccessorFactory {
    enum DatabaseType {
        MARIA,
        SQLITE
    }
    private static final DatabaseType databaseType;

    static {
        if (getPlugin().getConfig().getString("database.type") == null) {
            databaseType = null;
            getPlugin().getLogger().severe("Please set a database type in the config");
            getPlugin().disable();
        } else {
            String dbType = Objects.requireNonNull(getPlugin().getConfig().getString("database.type")).toLowerCase();
            if (dbType.equals("mariadb")) {
                databaseType = DatabaseType.MARIA;
            } else if (dbType.equals("sqlite")) {
                databaseType = DatabaseType.SQLITE;
            } else {
                databaseType = null;
                getPlugin().getLogger().severe("Database type \"" + dbType + "\" is not supported");
                getPlugin().disable();
            }
        }
    }

    public static PlayerDatabaseAccessor getPlayerDatabaseAccessor() {
        switch (databaseType) {
            case MARIA -> {
                return new MariaPlayerAccessor();
            }
            case SQLITE -> {
                return new SQLitePlayerAccessor();
            }
        }
        return new SQLitePlayerAccessor();
    }

    public static GuiEditorsDatabaseAccessor getGuiEditorsDatabaseAccessor() {
        switch (databaseType) {
            case MARIA -> {
                return new MariaGuiEditorsAccessor();
            }
            case SQLITE -> {
                return new SQLiteGuiEditorsAccessor();
            }
        }
        return new SQLiteGuiEditorsAccessor();
    }

    public static GuiDatabaseAccessor getGuiDatabaseAccessor() {
        switch (databaseType) {
            case MARIA -> {
                return new MariaGuiDatabaseAccessor();
            }
            case SQLITE -> {
                return new SQLiteGuiDatabaseAccessor();
            }
        }
        return new SQLiteGuiDatabaseAccessor();
    }
}
