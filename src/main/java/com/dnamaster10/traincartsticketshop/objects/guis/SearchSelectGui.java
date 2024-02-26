package com.dnamaster10.traincartsticketshop.objects.guis;

import com.dnamaster10.traincartsticketshop.objects.buttons.SimpleItemButton;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;
import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;

public class SearchSelectGui extends Gui {
    //The gui which will be searched
    private final int searchGuiId;
    @Override
    public void open() {
        generate();
        Bukkit.getScheduler().runTask(getPlugin(), () -> getPlayer().openInventory(getInventory()));
    }
    protected void generate() {
        //Build gui and add to inventory
        PageBuilder pageBuilder = new PageBuilder();

        if (getSession().checkBack()) {
            pageBuilder.addBackButton();
        }

        SimpleItemButton ticketSearchButton = new SimpleItemButton("search_tickets", Material.PAPER, "Search Tickets");
        pageBuilder.addButton(12, ticketSearchButton);

        SimpleItemButton linkerSearchbutton = new SimpleItemButton("search_linkers", Material.ENCHANTED_BOOK, "Search Linkers");
        pageBuilder.addButton(14, linkerSearchbutton);

        setInventory(new InventoryBuilder(pageBuilder.getPage(), getDisplayName()).getInventory());
    }

    @Override
    public void handleClick(InventoryClickEvent event, ItemStack clickedItem) {
        String buttonType = getButtonType(clickedItem);
        if (buttonType == null) {
            return;
        }
        //Remove cursor item since it is a button
        getPlayer().setItemOnCursor(null);
        switch (buttonType) {
            case "search_tickets" -> searchTickets();
            case "search_linkers" -> searchLinkers();
            case "back" -> back();
        }
    }
    private String getGuiName() throws DQLException {
        //Returns the name of the gui which is being searched from the id
        GuiAccessor guiAccessor = new GuiAccessor();
        return guiAccessor.getGuiNameById(searchGuiId);
    }
    private void guiDeletedOrMoved() {
        openErrorGui("Gui was deleted or moved");
    }
    private void searchTickets() {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            //Get the search gui name
            String searchGuiName;
            try {
                searchGuiName = getGuiName();
            } catch (DQLException e) {
                getPlugin().handleSqlException(e);
                return;
            }
            if (searchGuiName == null) {
                guiDeletedOrMoved();
                return;
            }

            TextComponent message1;
            message1 = new TextComponent(ChatColor.AQUA + "|");
            message1.setBold(true);
            TextComponent message2 = new TextComponent(ChatColor.AQUA + "| >>>Click me to search tickets<<<");
            message2.setBold(true);
            message2.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/traincartsticketshop gui searchTickets " + searchGuiName +" "));

            getPlayer().spigot().sendMessage(message1);
            getPlayer().spigot().sendMessage(message2);
            getPlayer().spigot().sendMessage(message1);

            removeCursorItemAndClose();
        });
    }
    private void searchLinkers() {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            String searchGuiName;
            try {
                searchGuiName = getGuiName();
            } catch (DQLException e) {
                getPlugin().handleSqlException(e);
                return;
            }
            if (searchGuiName == null) {
                guiDeletedOrMoved();
                return;
            }
            TextComponent message1;
            message1 = new TextComponent(ChatColor.AQUA + "|");
            message1.setBold(true);
            TextComponent message2 = new TextComponent(ChatColor.AQUA + "| >>>Click me to search linkers<<<");
            message2.setBold(true);
            message2.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/traincartsticketshop gui searchLinkers " + searchGuiName +" "));

            getPlayer().spigot().sendMessage(message1);
            getPlayer().spigot().sendMessage(message2);
            getPlayer().spigot().sendMessage(message1);

            removeCursorItemAndClose();
        });
    }

    public SearchSelectGui(int searchGuiId, Player p) {
        setPlayer(p);
        setDisplayName("Select a search type");
        this.searchGuiId = searchGuiId;
    }
}
