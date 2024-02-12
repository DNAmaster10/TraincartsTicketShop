package com.dnamaster10.tcgui.objects.guis;

import com.dnamaster10.tcgui.util.Traincarts;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;
import static com.dnamaster10.tcgui.objects.buttons.DataKeys.*;

public class ShopGui extends MultipageGui {
    @Override
    protected void generate() throws SQLException {
        GuiAccessor accessor = new GuiAccessor();
        int totalPages = accessor.getMaxPage(getGuiId());
        if (getPageNumber() > totalPages) {
            setPageNumber(totalPages);
        }
        if (getPageNumber() < 0) {
            setPageNumber(0);
        }
        //Build tickets
        GuiBuilder builder = new GuiBuilder(getDisplayName());
        builder.addTicketsFromDatabase(getGuiName(), getPageNumber());
        builder.addLinkersFromDatabase(getGuiName(), getPageNumber());

        //Check if there are any more pages
        if (totalPages > getPageNumber()) {
            builder.addNextPageButton();
        }
        if (getPageNumber() > 0) {
            builder.addPrevPageButton();
        }

        //Check if back button is needed
        if (getPlugin().getGuiManager().checkLastGui(getPlayer())) {
            builder.addBackButton();
        }

        SimpleButton searchButton = new SimpleButton("search", Material.SPYGLASS, "Search This Gui");
        builder.addSimpleButton(searchButton, 49);
        setInventory(builder.getInventory());
    }
    @Override
    public void handleClick(InventoryClickEvent event, List<ItemStack> items) {
        //Check if player interacted with a button
        for (ItemStack item : items) {
            String buttonType = getButtonType(item);
            if (buttonType == null) {
                continue;
            }
            switch (buttonType) {
                case "ticket" -> {
                    handleTicketClick(item);
                    return;
                }
                case "prev_page" -> {
                    prevPage();
                    return;
                }
                case "next_page" -> {
                    nextPage();
                    return;
                }
                case "linker" -> {
                    handleLink(item);
                    return;
                }
                case "back" -> {
                    back();
                    return;
                }
                case "search" -> {
                    search();
                    return;
                }
            }
        }
    }
    @Override
    public void nextPage() {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            int maxPage;
            try {
                //Check if any other pages exist above this one
                GuiAccessor guiAccessor = new GuiAccessor();
                maxPage = guiAccessor.getMaxPage(getGuiId());
            } catch (SQLException e) {
                getPlayer().closeInventory();
                getPlugin().reportSqlError(getPlayer(), e);
                return;
            }
            if (getPageNumber() + 1 > maxPage) {
                removeCursorItem();
                return;
            }
            //Increment page
            setPageNumber(getPageNumber() + 1);

            //Build new inventory
            removeCursorItem();
            open();
        });
    }

    @Override
    public void prevPage() {
        //Check that any pages exist before this oen
        if (getPageNumber() - 1 < 0) {
            setPageNumber(0);
            removeCursorItem();
            return;
        }
        setPageNumber(getPageNumber() - 1);
        removeCursorItem();
        open();
    }
    public void handleLink(ItemStack button) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            //First get the destination page
            ItemMeta meta = button.getItemMeta();
            if (meta == null) {
                removeCursorItem();
                return;
            }

            PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
            Integer linkedGuiId = dataContainer.get(DEST_GUI_ID, PersistentDataType.INTEGER);
            Integer linkedGuiPage = dataContainer.get(DEST_GUI_PAGE, PersistentDataType.INTEGER);
            if (linkedGuiId == null) {
                removeCursorItem();
                return;
            }
            if (linkedGuiPage == null) {
                linkedGuiPage = 0;
            }

            String destGuiName;
            ShopGui newGui;
            try {
                //Get dest gui name
                GuiAccessor guiAccessor = new GuiAccessor();
                if (!guiAccessor.checkGuiById(linkedGuiId)) {
                    removeCursorItem();
                    return;
                }
                destGuiName = guiAccessor.getGuiNameById(linkedGuiId);
                newGui = new ShopGui(destGuiName, linkedGuiPage, getPlayer());

            } catch (SQLException e) {
                removeCursorItemAndClose();
                getPlugin().reportSqlError(getPlayer(), e);
                return;
            }
            //Add gui to gui manager
            getPlugin().getGuiManager().addGui(getPlayer(), newGui);
            removeCursorItem();
            newGui.open();
        });
    }
    private void search() {
        SearchSelectGui gui = new SearchSelectGui(getGuiName(), getPlayer());

        getPlugin().getGuiManager().addGui(getPlayer(), gui);
        removeCursorItem();
        gui.open();
    }
    private void handleTicketClick(ItemStack ticket) {
        //Get ticket tc name;
        String tcName = Objects.requireNonNull(ticket.getItemMeta()).getPersistentDataContainer().get(TC_TICKET_NAME, PersistentDataType.STRING);

        //Check that the tc ticket exists
        if (!Traincarts.checkTicket(tcName)) {
            removeCursorItem();
            return;
        }
        //Give the ticket to the player
        removeCursorItem();
        Traincarts.giveTicketItem(tcName, 0, getPlayer());
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {}, 1L);
        getPlayer().closeInventory();
    }
    public ShopGui(int guiId, int page, Player p) throws SQLException {
        //Should be called from async thread
        //Instantiate gui
        GuiAccessor guiAccessor = new GuiAccessor();
        String displayName = guiAccessor.getColouredGuiDisplayName(guiName);
        int guiId = guiAccessor.getGuiIdByName(guiName);

        setGuiName(guiName);
        setDisplayName(displayName);
        setPlayer(p);
        setPageNumber(page);
        setGuiId(guiId);
    }
    public ShopGui(String guiName, Player p) throws SQLException {
        this(guiName, 0, p);
    }
}
