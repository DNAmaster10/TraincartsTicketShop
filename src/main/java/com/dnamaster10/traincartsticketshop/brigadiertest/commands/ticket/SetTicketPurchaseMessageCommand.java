package com.dnamaster10.traincartsticketshop.brigadiertest.commands.ticket;

import com.dnamaster10.traincartsticketshop.brigadiertest.commands.TicketShopCommand;
import com.dnamaster10.traincartsticketshop.util.Utilities;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
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

import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.PURCHASE_MESSAGE;
import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;

public class SetTicketPurchaseMessageCommand implements TicketShopCommand {
    @Override
    public LiteralCommandNode<CommandSourceStack> getRootNode() {
        return Commands.literal("setPurchaseMessage")
                .requires(ctx -> ctx.getExecutor() instanceof Player player &&
                        player.hasPermission("traincartsticketshop.ticket.setpurchasemessage"))
                .then(Commands.argument("purchase message", StringArgumentType.string())
                        .executes(this::execute)).build();
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx) {
        Player executor = (Player) ctx.getSource().getExecutor();
        assert executor != null;
        String newMessage = StringArgumentType.getString(ctx, "purchase message");

        ItemStack ticket = executor.getInventory().getItemInMainHand();
        String buttonType = getButtonType(ticket);
        if (buttonType == null || !buttonType.equals("ticket")) {
            Component component = MiniMessage.miniMessage().deserialize("<red>You must be holding a ticket to perform that action.");
            executor.sendMessage(component);
            return Command.SINGLE_SUCCESS;
        }

        Component colouredMessage = Utilities.parseColour(newMessage);
        String rawMessage = Utilities.stripColour(colouredMessage);

        if (rawMessage.isBlank()) {
            Component component = MiniMessage.miniMessage().deserialize("<red>Purchase messages cannot be blank.");
            executor.sendMessage(component);
            return Command.SINGLE_SUCCESS;
        }

        if (newMessage.length() > 500) {
            Component component = MiniMessage.miniMessage().deserialize("<red>Purchase messages cannot be more than 500 characters in length.");
            executor.sendMessage(component);
            return Command.SINGLE_SUCCESS;
        }

        ItemMeta meta = ticket.getItemMeta();
        assert meta != null;
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(PURCHASE_MESSAGE, PersistentDataType.STRING, newMessage);
        ticket.setItemMeta(meta);

        Component component = MiniMessage.miniMessage().deserialize("<green>Purchase message was set to \"")
                .append(colouredMessage).append(MiniMessage.miniMessage().deserialize("\""));
        executor.sendMessage(component);
        return Command.SINGLE_SUCCESS;
    }
}
