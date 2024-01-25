package com.dnamaster10.tcgui.objects;

import com.dnamaster10.tcgui.util.gui.GuiBuilder;
import com.dnamaster10.tcgui.util.Traincarts;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import com.dnamaster10.tcgui.util.gui.GuiManager;
import com.dnamaster10.tcgui.util.gui.LastGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ShopGui extends Gui {
    @Override
    public void open(Player p) {
        //Method should be run synchronous
        if (Bukkit.isPrimaryThread()) {
            p.openInventory(getInventory());
            return;
        }
        Bukkit.getScheduler().runTask(getPlugin(), () -> p.openInventory(getInventory()));
    }

    @Override
    public void nextPage(Player p) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                //Check if any other pages exist above this one
                GuiAccessor guiAccessor = new GuiAccessor();
                int guiId = guiAccessor.getGuiIdByName(getGuiName());
                int maxPage = guiAccessor.getTotalPages(guiId);
                if (getPage() + 1 > maxPage) {
                    return;
                }
                //Increment page
                setPage(getPage() + 1);

                //Build new inventory
                generateGui();
                removeCursorItem(p);
            } catch (SQLException e) {
                p.closeInventory();
                getPlugin().reportSqlError(p, e.toString());
            }
        });
    }

    @Override
    public void prevPage(Player p) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                //Check that any pages exist before the current page
                if (getPage() - 1 < 0) {
                    setPage(0);
                    return;
                }
                setPage(getPage() - 1);

                //Build the new page
                generateGui();
                removeCursorItem(p);
            } catch (SQLException e) {
                p.closeInventory();
                getPlugin().reportSqlError(p, e.toString());
            }
        });
    }
    public void handleLink(ItemStack button, Player p) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            //First get the destination page
            ItemMeta meta = button.getItemMeta();
            assert meta!= null;
            NamespacedKey key = new NamespacedKey(getPlugin(), "gui");
            int linkedGuiId = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);

            String destGuiName = null;
            //Check the destination gui exists and get the gui name
            try {
                GuiAccessor guiAccessor = new GuiAccessor();
                if (!guiAccessor.checkGuiById(linkedGuiId)) {
                    removeCursorItem(p);
                    return;
                }
                destGuiName = guiAccessor.getGuiNameById(linkedGuiId);
            } catch (SQLException e) {
                getPlugin().reportSqlError(p, e.toString());
            }

            //Add the current gui info to the previous gui stack
            getPlugin().getGuiManager().addPrevGui(getGuiName(), getPage(), p);

            //Now change the current gui to the new gui
            setPage(0);
            setGuiName(destGuiName);

            try {
                generateGui();
                removeCursorItem(p);
            }
            catch (SQLException e) {
                getPlugin().reportSqlError(p, e.toString());
            }
        });
    }
    private void back(Player p) {
        //Check that there is a previous gui that the player was on
        if (!getPlugin().getGuiManager().checkPrevGui(p)) {
            //If not, remove the button from their cursor
            removeCursorItem(p);
            return;
        }
        //If there is a previous gui, get the name of the last gui
        LastGui lastGui = getPlugin().getGuiManager().getPrevGui(p);
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                //Check that the gui exists
                GuiAccessor guiAccessor = new GuiAccessor();
                if (!guiAccessor.checkGuiByName(lastGui.getLastGuiName())) {
                    p.sendMessage(ChatColor.RED + "Previous gui does not exist");
                    removeCursorItem(p);
                    return;
                }
                //Remove the item from cursor
                removeCursorItem(p);

                //Next, we need to generate the new gui.
                setGuiName(lastGui.getLastGuiName());
                setPage(lastGui.getLastPageNum());

                generateGui();
            }
            catch (SQLException e) {
                getPlugin().reportSqlError(p, e.toString());
            }
        });
    }
    private void handleButtonClick(InventoryClickEvent event, String buttonType, ItemStack button) {
        switch (buttonType) {
            case "next_page" -> {
                nextPage((Player) event.getWhoClicked());
            }
            case "prev_page" -> {
                prevPage((Player) event.getWhoClicked());
            }
            case "linker" -> {
                handleLink(button, (Player) event.getWhoClicked());
            }
            case "back" -> {
                back((Player) event.getWhoClicked());
            }
        }
    }
    private void handleTicketClick(InventoryClickEvent event, ItemStack ticket) {
        //Get ticket tc name
        NamespacedKey key = new NamespacedKey(getPlugin(), "tc_name");
        String tcName = Objects.requireNonNull(ticket.getItemMeta()).getPersistentDataContainer().get(key, PersistentDataType.STRING);

        //Check that the tc ticket exists
        if (!Traincarts.checkTicket(tcName)) {
            return;
        }
        //Give the ticket to the player
        Player p = (Player) event.getWhoClicked();
        Traincarts.giveTicketItem(tcName, 0, p);
        p.setItemOnCursor(null);
        p.closeInventory();
    }

    @Override
    public void handleClick(InventoryClickEvent event, List<ItemStack> items) {
        //Check if player interacted with a button
        for (ItemStack item : items) {
            if (!item.hasItemMeta()) {
                continue;
            }
            NamespacedKey key = new NamespacedKey(getPlugin(), "type");
            if (!Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                continue;
            }
            if (Objects.equals(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING), "button")) {
                //This is a button, handle button click
                NamespacedKey buttonKey = new NamespacedKey(getPlugin(), "button_type");
                String buttonType = item.getItemMeta().getPersistentDataContainer().get(buttonKey, PersistentDataType.STRING);
                handleButtonClick(event, buttonType, item);
                break;
            }
            else if (Objects.equals(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING), "ticket")) {
                //This is a ticket, handle ticket click
                handleTicketClick(event, item);
                break;
            }
        }
    }
    private void generateGui() throws SQLException {
        //Adds tickets, buttons etc based on gui name

        //Build tickets
        GuiBuilder builder = new GuiBuilder(getGuiName(), getPage());
        builder.addTickets();
        builder.addLinkers();

        //Check if there are any more pages
        GuiAccessor accessor = new GuiAccessor();
        int guiId = accessor.getGuiIdByName(getGuiName());

        if (accessor.getTotalPages(guiId) > getPage()) {
            builder.addNextPageButton();
        }
        if (getPage() > 0) {
            builder.addPrevPageButton();
        }

        //Check if back button is needed
        if (getPlugin().getGuiManager().checkPrevGui(getPlayer())) {
            builder.addBackButton();
        }
        updateNewInventory(builder.getInventory());
    }

    public ShopGui(String guiName, Player p) throws SQLException {
        //Should be called from async thread
        //Instantiate gui
        setInventory(Bukkit.getServer().createInventory(p, 54));

        //Set the owner
        setPlayer(p);

        //Set page
        setPage(0);

        //Set the gui
        setGuiName(guiName);
        generateGui();
    }
}
