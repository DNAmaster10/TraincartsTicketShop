package com.dnamaster10.tcgui.objects.guis;

import com.dnamaster10.tcgui.util.database.GuiAccessor;
import com.dnamaster10.tcgui.util.database.LinkerAccessor;
import com.dnamaster10.tcgui.util.database.databaseobjects.LinkerDatabaseObject;
import com.dnamaster10.tcgui.util.gui.GuiBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.List;

import static com.dnamaster10.tcgui.objects.buttons.DataKeys.DEST_GUI_ID;
import static com.dnamaster10.tcgui.objects.buttons.DataKeys.DEST_GUI_PAGE;

public class LinkerSearchGui extends SearchGui {
    @Override
    public void open() {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                generate();
            } catch (SQLException e) {
                removeCursorItemAndClose();
                getPlugin().reportSqlError(getPlayer(), e);
            }
            Bukkit.getScheduler().runTask(getPlugin(), () -> getPlayer().openInventory(getInventory()));
        });
    }

    @Override
    protected void generate() throws SQLException {
        GuiBuilder builder = new GuiBuilder(getDisplayName());

        LinkerAccessor linkerAccessor = new LinkerAccessor();
        LinkerDatabaseObject[] linkerDatabaseObjects = linkerAccessor.searchLinkers(getSearchGuiId(), getPage() * 45, getSearchTerm());

        builder.addLinkers(linkerDatabaseObjects);

        if (linkerAccessor.getTotalLinkerSearchResults(getSearchGuiId(), getSearchTerm()) > (getPage() + 1) * 45) {
            builder.addPrevPageButton();
        }

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
                case "linker" -> {
                    link(item);
                    return;
                }
                case "next_page" -> {
                    nextPage();
                    return;
                }
                case "prev_page" -> {
                    prevPage();
                    return;
                }
            }
        }
    }
    private void link(ItemStack linker) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            //Get dest page
            ItemMeta meta = linker.getItemMeta();
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

            getPlugin().getGuiManager().addGui(getPlayer(), newGui);
            removeCursorItem();
            newGui.open();
        });
    }
    @Override
    protected void nextPage() {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                //Check that other pages exist beyond this one
                LinkerAccessor linkerAccessor = new LinkerAccessor();
                if (!(linkerAccessor.getTotalLinkerSearchResults(getSearchGuiId(), getSearchTerm()) > (getPage() + 1) * 45)) {
                    removeCursorItem();
                    return;
                }
            } catch (SQLException e) {
                removeCursorItemAndClose();
                getPlugin().reportSqlError(getPlayer(), e);
                return;
            }
            setPage(getPage() + 1);
            removeCursorItem();
            open();
        });
    }

    @Override
    protected void prevPage() {
        if (getPage() - 1 < 0) {
            setPage(0);
            removeCursorItem();
            return;
        }
        setPage(getPage() - 1);
        removeCursorItem();
        open();
    }
    public LinkerSearchGui(String searchGuiName, String searchTerm, int page, Player p) throws SQLException{
        setSearchGuiName(searchGuiName);
        setSearchTerm(searchTerm);
        setPage(page);
        setPlayer(p);

        //Get gui id and display name
        GuiAccessor guiAccessor = new GuiAccessor();
        setSearchGuiId(guiAccessor.getGuiIdByName(searchGuiName));
        setDisplayName("Searching: " + guiAccessor.getColouredGuiDisplayName(searchGuiName));
    }
    public LinkerSearchGui(String searchGuiName, String searchTerm, Player p) throws SQLException {
        this(searchGuiName, searchTerm, 0, p);
    }
}
