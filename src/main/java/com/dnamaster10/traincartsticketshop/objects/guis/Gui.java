package com.dnamaster10.traincartsticketshop.objects.guis;

import com.dnamaster10.traincartsticketshop.objects.guis.multipageguis.ShopGui;
import com.dnamaster10.traincartsticketshop.util.Session;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import com.dnamaster10.traincartsticketshop.util.newdatabase.accessors.GuiDataAccessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;
import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.DEST_GUI_ID;
import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.DEST_GUI_PAGE;

public abstract class Gui {
    //The inventory currently open within this gui
    public abstract void open();
    public abstract void handleClick(InventoryClickEvent event);

    private Inventory inventory;
    private int guiId;
    private Player player;
    private String displayName;

    protected void removeCursorItem() {
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> player.setItemOnCursor(null), 1L);
    }
    protected void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
    protected void setGuiId(int id) {
        this.guiId = id;
    }
    protected String getDisplayName() {
        return this.displayName;
    }
    protected void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    protected Player getPlayer() {
        return player;
    }
    protected void setPlayer(Player p) {
        player = p;
    }
    protected void closeInventory() {Bukkit.getScheduler().runTaskLater(getPlugin(), () -> player.closeInventory(), 1L);}
    protected Session getSession() {
        return getPlugin().getGuiManager().getSession(getPlayer());
    }
    protected void back() {
        getPlayer().setItemOnCursor(null);
        //Check there is a previous gui
        Session currentSession = getSession();
        if (!currentSession.checkBack()) {
            return;
        }
        //If there is, go back
        currentSession.back();
    }
    protected void removeCursorItemAndClose() {
        //TODO might need to be removed
        //Removes item on cursor and closes inventory at the same time
        player.setItemOnCursor(null);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> player.closeInventory(), 1L);
    }
    protected void openErrorGui(String errorMessage) {
        ErrorGui errorGui = new ErrorGui(errorMessage, player);
        player.setItemOnCursor(null);
        errorGui.open();
    }
    protected void link(ItemStack link) {
        //Get button info
        ItemMeta meta = link.getItemMeta();
        if (meta == null) {
            return;
        }

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        Integer linkedGuiId = dataContainer.get(DEST_GUI_ID, PersistentDataType.INTEGER);
        Integer linkedGuiPage = dataContainer.get(DEST_GUI_PAGE, PersistentDataType.INTEGER);
        if (linkedGuiId == null || linkedGuiPage == null) {
            //This is not a valid link
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            //Get and check info from database
            ShopGui newGui;
            try {
                GuiDataAccessor guiAccessor = new GuiDataAccessor();
                if (!guiAccessor.checkGuiById(linkedGuiId)) {
                    return;
                }
                //Gui exists, create the new gui
                newGui = new ShopGui(linkedGuiId, linkedGuiPage, getPlayer());
            } catch (QueryException e) {
                getPlugin().handleSqlException(getPlayer(), e);
                return;
            }
            getSession().addGui(newGui);
            newGui.open();
        });
    }

    public Inventory getInventory() {
        return this.inventory;
    }
    public int getGuiId() {
        return this.guiId;
    }
}
