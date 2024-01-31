package com.dnamaster10.tcgui.objects.guis;

import com.dnamaster10.tcgui.util.gui.GuiBuilder;
import com.dnamaster10.tcgui.util.Traincarts;
import com.dnamaster10.tcgui.util.database.GuiAccessor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
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

public class ShopGui extends MultipageGui {
    @Override
    public void open() {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            //Generate the gui
            try {
                generate();
            } catch (SQLException e) {
                removeCursorItemAndClose();
                getPlugin().reportSqlError(getPlayer(), e.toString());
            }
            Bukkit.getScheduler().runTask(getPlugin(), () -> getPlayer().openInventory(getInventory()));
        });
    }
    protected void generate() throws SQLException {
        //Build tickets
        GuiBuilder builder = new GuiBuilder(getDisplayName());
        builder.addTicketsFromDatabase(getGuiName(), getPage());
        builder.addLinkersFromDatabase(getGuiName(), getPage());

        //Check if there are any more pages
        GuiAccessor accessor = new GuiAccessor();

        if (accessor.getTotalPages(getGuiId()) > getPage()) {
            builder.addNextPageButton();
        }
        if (getPage() > 0) {
            builder.addPrevPageButton();
        }

        //Check if back button is needed
        if (getPlugin().getGuiManager().checkLastGui(getPlayer())) {
            builder.addBackButton();
        }

        builder.addSearchButton();
        setInventory(builder.getInventory());
    }

    @Override
    public void nextPage() {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            int maxPage;
            try {
                //Check if any other pages exist above this one
                GuiAccessor guiAccessor = new GuiAccessor();
                maxPage = guiAccessor.getTotalPages(getGuiId());
            } catch (SQLException e) {
                getPlayer().closeInventory();
                getPlugin().reportSqlError(getPlayer(), e.toString());
                return;
            }
            if (getPage() + 1 > maxPage) {
                removeCursorItem();
                return;
            }
            //Increment page
            setPage(getPage() + 1);

            //Build new inventory
            removeCursorItem();
            open();
        });
    }

    @Override
    public void prevPage() {
        //Check that any pages exist before this oen
        if (getPage() - 1 < 0) {
            setPage(0);
            removeCursorItem();
            return;
        }
        setPage(getPage() - 1);
        removeCursorItem();
        open();
    }
    public void handleLink(ItemStack button) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            //First get the destination page
            ItemMeta meta = button.getItemMeta();
            assert meta!= null;
            NamespacedKey key = new NamespacedKey(getPlugin(), "gui");
            Integer linkedGuiId = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
            if (linkedGuiId == null) {
                removeCursorItem();
                return;
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
                newGui = new ShopGui(destGuiName, getPlayer());

            } catch (SQLException e) {
                removeCursorItemAndClose();
                getPlugin().reportSqlError(getPlayer(), e.toString());
                return;
            }
            //Add gui to gui manager
            getPlugin().getGuiManager().addGui(getPlayer(), newGui);
            removeCursorItem();
            newGui.open();
        });
    }
    private void back() {
        //Check that there is a previous gui that the player was on
        if (!getPlugin().getGuiManager().checkLastGui(getPlayer())) {
            //If not, remove the button from their cursor
            removeCursorItem();
            return;
        }
        //If there is, go back
        removeCursorItem();
        getPlugin().getGuiManager().back(getPlayer());
    }
    private void search() {
        SearchSelectGui gui = new SearchSelectGui(getGuiName(), getPlayer());

        getPlugin().getGuiManager().addGui(getPlayer(), gui);
        removeCursorItem();
        gui.open();
    }
    private void handleTicketClick(ItemStack ticket) {
        //Get ticket tc name
        NamespacedKey key = new NamespacedKey(getPlugin(), "tc_name");
        String tcName = Objects.requireNonNull(ticket.getItemMeta()).getPersistentDataContainer().get(key, PersistentDataType.STRING);

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

    public ShopGui(String guiName, int page, Player p) throws SQLException {
        //Should be called from async thread
        //Instantiate gui
        GuiAccessor guiAccessor = new GuiAccessor();
        String displayName = guiAccessor.getColouredGuiDisplayName(guiName);
        int guiId = guiAccessor.getGuiIdByName(guiName);

        setGuiName(guiName);
        setDisplayName(displayName);
        setPlayer(p);
        setPage(page);
        setGuiId(guiId);
    }
    public ShopGui(String guiName, Player p) throws SQLException {
        this(guiName, 0, p);
    }
}
