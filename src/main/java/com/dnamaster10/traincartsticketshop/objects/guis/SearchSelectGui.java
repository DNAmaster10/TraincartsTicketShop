package com.dnamaster10.traincartsticketshop.objects.guis;

import com.dnamaster10.traincartsticketshop.objects.buttons.SimpleItemButton;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import com.dnamaster10.traincartsticketshop.util.newdatabase.accessors.GuiDataAccessor;
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

        if (getSession().checkBack()) pageBuilder.addBackButton();

        SimpleItemButton ticketSearchButton = new SimpleItemButton("search_tickets", Material.PAPER, "Search Tickets");
        pageBuilder.addButton(12, ticketSearchButton);

        SimpleItemButton linkSearchbutton = new SimpleItemButton("search_links", Material.ENCHANTED_BOOK, "Search Links");
        pageBuilder.addButton(14, linkSearchbutton);

        setInventory(new InventoryBuilder(pageBuilder.getPage(), getDisplayName()).getInventory());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        String buttonType = getButtonType(clickedItem);
        if (buttonType == null) return;

        //Remove cursor item since it is a button
        removeCursorItem();
        switch (buttonType) {
            case "search_tickets" -> searchTickets();
            case "search_links" -> searchLinks();
            case "back" -> back();
        }
    }
    private String getGuiName() throws QueryException {
        //Returns the name of the gui which is being searched from the id
        GuiDataAccessor guiAccessor = new GuiDataAccessor();
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
            } catch (QueryException e) {
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
    private void searchLinks() {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            String searchGuiName;
            try {
                searchGuiName = getGuiName();
            } catch (QueryException e) {
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
            TextComponent message2 = new TextComponent(ChatColor.AQUA + "| >>>Click me to search links<<<");
            message2.setBold(true);
            message2.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/traincartsticketshop gui searchLinks " + searchGuiName +" "));

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
