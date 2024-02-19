package com.dnamaster10.traincartsticketshop.objects.guis;

import com.dnamaster10.traincartsticketshop.objects.guis.multipageguis.ShopGui;
import com.dnamaster10.traincartsticketshop.util.Session;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
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
    private Inventory inventory;
    private int guiId;
    private Player player;
    private String displayName;
    public abstract void open();
    public abstract void handleClick(InventoryClickEvent event, ItemStack clickedItem);
    public Inventory getInventory() {
        return this.inventory;
    }
    protected void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
    public int getGuiId() {
        return this.guiId;
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
        //Removes item on cursor and closes inventory at the same time
        player.setItemOnCursor(null);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> player.closeInventory(), 1L);
    }
    protected void closeInventory() {
        //Closed the inventory the player has open
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> player.closeInventory(), 1L);
    }
    protected void openErrorGui(String errorMessage) {
        ErrorGui errorGui = new ErrorGui(errorMessage, player);
        player.setItemOnCursor(null);
        errorGui.open();
    }
    protected Session getSession() {
        return getPlugin().getGuiManager().getSession(getPlayer());
    }
    protected void link(ItemStack linker) {
        //Get button info
        ItemMeta meta = linker.getItemMeta();
        if (meta == null) {
            return;
        }

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        Integer linkedGuiId = dataContainer.get(DEST_GUI_ID, PersistentDataType.INTEGER);
        Integer linkedGuiPage = dataContainer.get(DEST_GUI_PAGE, PersistentDataType.INTEGER);
        if (linkedGuiId == null || linkedGuiPage == null) {
            //This is not a valid linker
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            //Get and check info from database
            ShopGui newGui;
            try {
                GuiAccessor guiAccessor = new GuiAccessor();
                if (!guiAccessor.checkGuiById(linkedGuiId)) {
                    return;
                }
                //Gui exists, create the new gui
                newGui = new ShopGui(linkedGuiId, linkedGuiPage, getPlayer());
            } catch (DQLException e) {
                getPlugin().handleSqlException(getPlayer(), e);
                return;
            }
            getSession().addGui(newGui);
            newGui.open();
        });
    }
}
