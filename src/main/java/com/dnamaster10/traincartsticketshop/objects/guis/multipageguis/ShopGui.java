package com.dnamaster10.traincartsticketshop.objects.guis.multipageguis;

import com.dnamaster10.traincartsticketshop.objects.buttons.Button;
import com.dnamaster10.traincartsticketshop.objects.buttons.SimpleItemButton;
import com.dnamaster10.traincartsticketshop.objects.guis.PageBuilder;
import com.dnamaster10.traincartsticketshop.objects.guis.multipageguis.MultipagePurchasableGui;
import com.dnamaster10.traincartsticketshop.util.database.GuiAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.DQLException;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ShopGui extends MultipagePurchasableGui {
    @Override
    protected Button[] getNewPage() throws DQLException {
        PageBuilder pageBuilder = new PageBuilder();
        pageBuilder.addTicketsFromDatabase(getGuiId(), getPageNumber());
        pageBuilder.addLinkersFromDatabase(getGuiId(), getPageNumber());

        if (getTotalPages() > getPageNumber()) pageBuilder.addNextPageButton();
        if (getPageNumber() > 0) pageBuilder.addPrevPageButton();
        if (getSession().checkBack()) pageBuilder.addBackButton();

        SimpleItemButton searchButton = new SimpleItemButton("search", Material.SPYGLASS, "Search this gui");
        pageBuilder.addButton(49, searchButton);

        return pageBuilder.getPage();
    }

    public ShopGui(int guiId, int page, Player player) throws DQLException {
        GuiAccessor guiAccessor = new GuiAccessor();
        String displayName = guiAccessor.getColouredDisplayNameById(guiId);

        setDisplayName(displayName);
        setPlayer(player);
        setPageNumber(page);
        setGuiId(guiId);
        setTotalPages(guiAccessor.getHighestPageNumber(guiId));
    }
    public ShopGui(int guiId, Player player) throws DQLException {
        this(guiId, 0, player);
    }
}
