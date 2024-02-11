package com.dnamaster10.tcgui.objects.guis;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public class SearchSelectGui extends Gui {
    //The gui which will be searched
    @Override
    public void open() {
        generate();
        Bukkit.getScheduler().runTask(getPlugin(), () -> getPlayer().openInventory(getInventory()));
    }

    @Override
    protected void generate() {
        //Build gui and add to inventory
        GuiBuilder builder = new GuiBuilder(getDisplayName());

        if (getPlugin().getGuiManager().checkLastGui(getPlayer())) {
            builder.addBackButton();
        }

        SimpleButton ticketSearchButton = new SimpleButton("search_tickets", Material.PAPER, "Search Tickets");
        builder.addSimpleButton(ticketSearchButton, 12);

        SimpleButton linkerSearchButton = new SimpleButton("search_linkers", Material.ENCHANTED_BOOK, "Search Linkers");
        builder.addSimpleButton(linkerSearchButton, 14);

        setInventory(builder.getInventory());
    }

    @Override
    public void handleClick(InventoryClickEvent event, List<ItemStack> items) {
        for (ItemStack item : items) {
            String buttonType = getButtonType(item);
            if (buttonType == null) {
                continue;
            }
            switch (buttonType) {
                case "search_tickets" -> {
                    searchTickets();
                    return;
                }
                case "search_linkers" -> {
                    searchLinkers();
                    return;
                }
                case "back" -> {
                    back();
                    return;
                }
            }
        }
    }
    private void searchTickets() {
        TextComponent message1;
        message1 = new TextComponent(ChatColor.AQUA + "|");
        message1.setBold(true);
        TextComponent message2 = new TextComponent(ChatColor.AQUA + "| >>>Click me to search tickets<<<");
        message2.setBold(true);
        message2.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tcgui gui searchTickets " + getGuiName() + " "));

        getPlayer().spigot().sendMessage(message1);
        getPlayer().spigot().sendMessage(message2);
        getPlayer().spigot().sendMessage(message1);

        removeCursorItemAndClose();
    }
    private void searchLinkers() {
        TextComponent message1;
        message1 = new TextComponent(ChatColor.AQUA + "|");
        message1.setBold(true);
        TextComponent message2 = new TextComponent(ChatColor.AQUA + "| >>>Click me to search linkers<<<");
        message2.setBold(true);
        message2.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tcgui gui searchLinkers " + getGuiName() + " "));

        getPlayer().spigot().sendMessage(message1);
        getPlayer().spigot().sendMessage(message2);
        getPlayer().spigot().sendMessage(message1);

        removeCursorItemAndClose();
    }

    public SearchSelectGui(String searchGuiName, Player p) {
        setPlayer(p);
        setGuiName(searchGuiName);
        setDisplayName("Select a search type");
    }
}
