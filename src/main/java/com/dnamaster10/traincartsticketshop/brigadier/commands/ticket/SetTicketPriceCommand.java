package com.dnamaster10.traincartsticketshop.brigadier.commands.ticket;

import com.dnamaster10.traincartsticketshop.brigadier.commands.TicketShopCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;
import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.PRICE;
import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;

public class SetTicketPriceCommand implements TicketShopCommand {
    @Override
    public LiteralCommandNode<CommandSourceStack> getRootNode() {
        double minPrice = getPlugin().getConfig().getDouble("MinTicketPrice");
        double maxPrice = getPlugin().getConfig().getDouble("MaxTicketPrice");

        return Commands.literal("setPrice")
                .requires(ctx -> ctx.getExecutor() instanceof Player player &&
                        player.hasPermission("traincartsticketshop.ticket.setprice"))
                .then(Commands.argument("price", DoubleArgumentType.doubleArg(minPrice, maxPrice))
                        .executes(this::execute)).build();
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx) {
        Player executor = (Player) ctx.getSource().getExecutor();
        assert executor != null;

        if (!getPlugin().getConfig().getBoolean("UseEconomy")) {
            Component component = MiniMessage.miniMessage().deserialize("<red>Economy support is disabled in the config!");
            executor.sendMessage(component);
            return Command.SINGLE_SUCCESS;
        }

        if (!getPlugin().getConfig().getBoolean("AllowCustomTicketPrices")) {
            Component component = MiniMessage.miniMessage().deserialize("<red>Custom ticket prices are disabled in the config!");
            executor.sendMessage(component);
            return Command.SINGLE_SUCCESS;
        }

        double price = DoubleArgumentType.getDouble(ctx, "price");

        ItemStack ticket = executor.getInventory().getItemInMainHand();
        String buttonType = getButtonType(ticket);
        if (buttonType == null || !buttonType.equals("ticket")) {
            Component component = MiniMessage.miniMessage().deserialize("<red>You must be holding a ticket to perform that action.");
            executor.sendMessage(component);
            return Command.SINGLE_SUCCESS;
        }

        ItemMeta meta = ticket.getItemMeta();
        assert meta != null;
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(PRICE, PersistentDataType.DOUBLE, price);
        ticket.setItemMeta(meta);

        Component component = MiniMessage.miniMessage().deserialize("<green>Successfully changed ticket price.");
        executor.sendMessage(component);
        return Command.SINGLE_SUCCESS;
    }
}
