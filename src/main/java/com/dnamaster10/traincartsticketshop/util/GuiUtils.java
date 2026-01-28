package com.dnamaster10.traincartsticketshop.util;

import com.dnamaster10.traincartsticketshop.objects.guis.ShopGui;
import com.dnamaster10.traincartsticketshop.util.database.accessors.GuiDataAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.UUID;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;
import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.*;

public class GuiUtils {
    private static final String DEFAULT_PURCHASE_MESSAGE = getPlugin().getConfig().getString("PurchaseMessage");

    /**
     * Handles a ticket purchase.
     *
     * @param ticketItem The Ticket Shop Ticket ItemStack
     * @param player The player making the purchase
     */
    public static void handleTicketItemPurchase(ItemStack ticketItem, Player player, int guiId) {
        ItemMeta meta = ticketItem.getItemMeta();
        if (meta == null) {
            player.sendMessage(Utilities.parseColour("<red>Ticket info is broken, failed to purchase ticket"));
            return;
        }
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        if (!dataContainer.has(TC_TICKET_NAME, PersistentDataType.STRING)) {
            player.sendMessage(Utilities.parseColour("<red>Ticket info is broken, failed to purchase ticket"));
            return;
        }
        String tcName = dataContainer.get(TC_TICKET_NAME, PersistentDataType.STRING);
        if (tcName == null || !Traincarts.checkTicket(tcName)) {
            player.sendMessage(Utilities.parseColour("<red>Traincarts ticket \"" + tcName + "\" is invalid or has been deleted"));
            return;
        }
        String purchaseMessage = null;
        if (dataContainer.has(PURCHASE_MESSAGE, PersistentDataType.STRING)) {
            purchaseMessage = dataContainer.get(PURCHASE_MESSAGE, PersistentDataType.STRING);
            if (purchaseMessage != null && purchaseMessage.isBlank()) purchaseMessage = null;
        }

        //TODO Revisit this code. Should negatives balances be allowed? How can we check that? Can this be made more readable?
        //TODO Can we use EconomyResponse instead of checking negative balance?
        //Handle economy checks
        Double ticketPrice = 0.0;
        boolean shouldTransact = false;
        VaultHook vaultHook = getPlugin().getVaultHook();
        if (getPlugin().getConfig().getBoolean("UseEconomy") && vaultHook.hasEconomy()) {

            double defaultTicketPrice = getPlugin().getConfig().getDouble("DefaultTicketPrice");

            //Check and correct ticket price
            if (!getPlugin().getConfig().getBoolean("AllowCustomTicketPrices")) {
                ticketPrice = defaultTicketPrice;
            } else {
                double minTicketPrice = getPlugin().getConfig().getDouble("MinTicketPrice");
                double maxTicketPrice = getPlugin().getConfig().getDouble("MaxTicketPrice");

                if (!dataContainer.has(PRICE, PersistentDataType.DOUBLE)) {
                    ticketPrice = dataContainer.get(PRICE, PersistentDataType.DOUBLE);
                    if (ticketPrice != null) {
                        if (ticketPrice < minTicketPrice) ticketPrice = minTicketPrice;
                        else if (ticketPrice > maxTicketPrice) ticketPrice = maxTicketPrice;
                    } else {
                        ticketPrice = defaultTicketPrice;
                    }
                } else {
                    ticketPrice = defaultTicketPrice;
                }
            }

            //Check if transaction is possible
            double playerBalance = vaultHook.getBalance(player);
            if (playerBalance - ticketPrice < 0) {
                player.sendMessage(Utilities.parseColour("<red>You can't afford that ticket!"));
                return;
            }
            shouldTransact = true;
        }

        //Handle transaction
        if (shouldTransact && ticketPrice > 0d) {
            EconomyResponse response = vaultHook.withdrawMoney(player, ticketPrice);
            if (response.type == EconomyResponse.ResponseType.FAILURE) {
                player.sendMessage(Utilities.parseColour("<red>" + response.errorMessage));
                return;
            }
            player.sendMessage(Utilities.parseColour("<yellow>" + vaultHook.format(response.amount) + " has been taken from your account for purchasing a ticket!"));

            if (Objects.equals(getPlugin().getConfig().getString("EconomyMode"), "guiOwners")) {
                //Send money to gui owner as well.
                //TODO does this need to be asynchronous?
                final double transferAmount = ticketPrice;
                Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
                    GuiDataAccessor guiDataAccessor = new GuiDataAccessor();
                    String ownerUuid = guiDataAccessor.getOwnerUuid(guiId);
                    Bukkit.getScheduler().runTask(getPlugin(), () -> {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(ownerUuid));
                        vaultHook.depositMoney(offlinePlayer, transferAmount);
                    });
                });
            }
        }

        //Handle purchase
        Traincarts.giveTicketItem(tcName, player);
        player.sendMessage(Utilities.parseColour("<green>" + DEFAULT_PURCHASE_MESSAGE));
        if (purchaseMessage != null) {
            player.sendMessage("");
            player.sendMessage(Utilities.parseColour(purchaseMessage));
            player.sendMessage("");
        }

    }

    /**
     * Handles a link between two Guis.
     *
     * @param linkItem The link ItemStack which is being handled
     * @param player The player who is being linked
     */
    public static void linkGui(ItemStack linkItem, Player player) {
        ItemMeta meta = linkItem.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        Integer linkedGuiId = dataContainer.get(DEST_GUI_ID, PersistentDataType.INTEGER);
        Integer linkedGuiPage = dataContainer.get(DEST_GUI_PAGE, PersistentDataType.INTEGER);

        if (linkedGuiId == null || linkedGuiPage == null) return;

        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            GuiDataAccessor guiDataAccessor = new GuiDataAccessor();
            if (!guiDataAccessor.checkGuiById(linkedGuiId)) return;
            int goalPage = linkedGuiPage;
            if (goalPage > 0) {
                try {
                    if (guiDataAccessor.getHighestPageNumber(linkedGuiId) < goalPage) {
                        goalPage = 0;
                    }
                } catch (QueryException e) {
                    getPlugin().handleSqlException(player, e);
                    return;
                }
            }
            ShopGui shopGui = new ShopGui(player, linkedGuiId, goalPage);
            shopGui.open();
        });
    }
}
