package com.dnamaster10.traincartsticketshop.objects.guis;

import com.dnamaster10.traincartsticketshop.objects.buttons.SimpleItemButton;
import com.dnamaster10.traincartsticketshop.objects.guis.interfaces.ClickHandler;
import com.dnamaster10.traincartsticketshop.util.Session;
import com.dnamaster10.traincartsticketshop.util.Utilities;
import com.dnamaster10.traincartsticketshop.util.database.accessors.GuiDataAccessor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;
import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;
import static net.kyori.adventure.text.event.ClickEvent.suggestCommand;

/**
 * A Gui used to select the type of search to perform, such as searching tickets or links.
 */
public class SearchSelectGui extends Gui implements InventoryHolder, ClickHandler {
    private final Player player;
    private final int guiId;
    private final Inventory inventory;

    /**
     * @param player The player who will open this Gui
     * @param searchGuiId The ID of the Gui to search
     */
    public SearchSelectGui(Player player, int searchGuiId) {
        this.player = player;
        this.guiId = searchGuiId;

        getPlugin().getGuiManager().getSession(player).addGui(this);

        Page page = new Page();
        page.setDisplayName(Utilities.parseColour("Select Search Type"));

        if (getPlugin().getGuiManager().getSession(player).checkBack()) page.addBackButton();

        SimpleItemButton ticketSearchButton = new SimpleItemButton("search_tickets", Material.PAPER, "Search Tickets");
        page.addButton(12, ticketSearchButton);

        SimpleItemButton linkSearchButton = new SimpleItemButton("search_links", Material.ENCHANTED_BOOK, "Search Links");
        page.addButton(14, linkSearchButton);

        inventory = page.getAsInventory(this);
    }

    @Override
    public void open() {
        Bukkit.getScheduler().runTask(getPlugin(), () -> player.openInventory(inventory));
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        String buttonType = getButtonType(clickedItem);
        if (buttonType == null) return;

        switch (buttonType) {
            case "search_tickets" -> searchTickets();
            case "search_links" -> searchLinks();
            case "back" -> {
                Session session = getPlugin().getGuiManager().getSession(player);
                if (!session.checkBack()) return;
                session.back();
            }
        }
    }

    private void searchTickets() {
        GuiDataAccessor guiDataAccessor = new GuiDataAccessor();
        if (!guiDataAccessor.checkGuiById(guiId)) {
            player.closeInventory();
            player.sendMessage(Utilities.parseColour("<red>Gui was deleted or moved!"));
            return;
        }

        String guiName = guiDataAccessor.getGuiNameById(guiId);
        Component clickComponent = Utilities.parseColour("<aqua>| >>>Click me to search tickets<<<")
                .clickEvent(suggestCommand("/tshop gui searchTickets " + guiName + " "));
        Component lineComponent = Utilities.parseColour("<aqua>|");

        player.sendMessage(lineComponent);
        player.sendMessage(clickComponent);
        player.sendMessage(lineComponent);

        //TODO Do we need to use run task to close still with Paper?
        Bukkit.getScheduler().runTask(getPlugin(), () -> player.closeInventory());
    }

    private void searchLinks() {
        GuiDataAccessor guiDataAccessor = new GuiDataAccessor();
        if (!guiDataAccessor.checkGuiById(guiId)) {
            player.closeInventory();
            player.sendMessage(Utilities.parseColour("<red>Gui was deleted or moved!"));
            return;
        }

        String guiName = guiDataAccessor.getGuiNameById(guiId);
        Component clickComponent = Utilities.parseColour("<aqua>| >>>Click me to search links<<<")
                .clickEvent(suggestCommand("/tshop gui searchLinks " + guiName + " "));
        Component lineComponent = Utilities.parseColour("<aqua>|");

        player.sendMessage(lineComponent);
        player.sendMessage(clickComponent);
        player.sendMessage(lineComponent);

        Bukkit.getScheduler().runTask(getPlugin(), () -> player.closeInventory());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
