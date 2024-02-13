package com.dnamaster10.traincartsticketshop.objects.guis.multipageguis;

import com.dnamaster10.traincartsticketshop.objects.buttons.Button;
import com.dnamaster10.traincartsticketshop.objects.guis.InventoryBuilder;
import com.dnamaster10.traincartsticketshop.objects.guis.PageBuilder;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.database.LinkerAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.LinkerDatabaseObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.List;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;
import static com.dnamaster10.traincartsticketshop.objects.buttons.Buttons.getButtonType;
import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.DEST_GUI_ID;
import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.DEST_GUI_PAGE;

public class LinkerSearchGui extends SearchGui {
    @Override
    protected Button[] generateNewPage() throws SQLException {
        PageBuilder pageBuilder = new PageBuilder();
        LinkerAccessor linkerAccessor = new LinkerAccessor();

        LinkerDatabaseObject[] linkerDatabaseObjects = linkerAccessor.searchLinkers(getSearchGuiId(), getPageNumber() * 45, getSearchTerm());
        pageBuilder.addLinkers(linkerDatabaseObjects);

        if (linkerAccessor.getTotalLinkerSearchResults(getSearchGuiId(), getSearchTerm()) > (getPageNumber() + 1) * 45) {
            pageBuilder.addNextPageButton();
        }
        if (getPageNumber() > 0) {
            pageBuilder.addPrevPageButton();
        }

        return pageBuilder.getPage();
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
        //Get button info
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
            removeCursorItem();
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            //Get info from database
            ShopGui newGui;
            try {
                GuiAccessor guiAccessor = new GuiAccessor();
                if (!guiAccessor.checkGuiById(linkedGuiId)) {
                    removeCursorItem();
                    return;
                }
                newGui = new ShopGui(linkedGuiId, linkedGuiPage, getPlayer());
            } catch (SQLException e) {
                removeCursorItemAndClose();
                getPlugin().reportSqlError(getPlayer(), e);
                return;
            }
            getSession().addGui(newGui);
            newGui.open();
        });
    }
    public LinkerSearchGui(int searchGuiId, String searchTerm, Player p) throws SQLException {
        //Set basic values
        setSearchGuiId(searchGuiId);
        setSearchTerm(searchTerm);
        setPageNumber(0);
        setPlayer(p);

        //Get gui display name
        GuiAccessor guiAccessor = new GuiAccessor();
        setDisplayName("Searching: " + guiAccessor.getColouredDisplayNameById(searchGuiId));
    }
}
