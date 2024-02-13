package com.dnamaster10.tcgui.objects.buttons;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;
import static com.dnamaster10.tcgui.objects.buttons.DataKeys.*;

public class Buttons {
    //A utility class for buttons
    public static Button getNewButton(String buttonType, ItemStack item) {
        //Returns a new button object from a button type and an item
        //If any of the item keys are invalid, null will be returned
        if (!item.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        String displayName = meta.getDisplayName();

        switch (buttonType) {
            case "ticket" -> {
                PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
                if (!dataContainer.has(TC_TICKET_NAME, PersistentDataType.STRING)) {
                    return null;
                }
                String tcTicketName = dataContainer.get(TC_TICKET_NAME, PersistentDataType.STRING);
                if (tcTicketName == null) {
                    return null;
                }
                return new Ticket(tcTicketName, displayName);
            }
            case "linker" -> {
                PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
                if (!dataContainer.has(DEST_GUI_ID, PersistentDataType.INTEGER)) {
                    return null;
                }
                Integer linkedGuiId = dataContainer.get(DEST_GUI_ID, PersistentDataType.INTEGER);
                if (linkedGuiId == null) {
                    return null;
                }
                Integer linkedGuiPage = dataContainer.get(DEST_GUI_PAGE, PersistentDataType.INTEGER);
                if (linkedGuiPage == null) {
                    linkedGuiPage = 0;
                }
                return new Linker(linkedGuiId, linkedGuiPage, displayName);
            }
            default -> {
                //Item is a simple button, handle as such
                //Check if the simple button is a head button or an item button
                Material material = item.getType();
                if (material == Material.PLAYER_HEAD) {
                    //Item is a payer head button, create new button for that
                    //First get the head type
                    HeadData.HeadType type = HeadData.getHeadTypeFromItem(item);
                    if (type == null) {
                        return null;
                    }
                    return new SimpleHeadButton(buttonType, type, displayName);
                }
                else {
                    //Item is a regular item button, create new button for that
                    return new SimpleItemButton(buttonType, material, displayName);
                }
            }
        }
    }
}
