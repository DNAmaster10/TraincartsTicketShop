package com.dnamaster10.traincartsticketshop.brigadier.commands.ticket;

import com.dnamaster10.traincartsticketshop.brigadier.commands.TicketShopCommand;
import com.mojang.brigadier.Command;
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

import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.PURCHASE_MESSAGE;
import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;

public class RemoveTicketPurchaseMessageCommand implements TicketShopCommand {
    @Override
    public LiteralCommandNode<CommandSourceStack> getRootNode() {
        return Commands.literal("removePurchaseMessage")
                .requires(ctx -> {
                    //TODO potentially check item in player hand here?
                    return ctx.getExecutor() instanceof Player player &&
                            player.hasPermission("traincartsticketshop.ticket.removepurchasemessage");
                }).executes(this::execute).build();
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getExecutor();
        assert player != null;

        ItemStack ticket = player.getInventory().getItemInMainHand();
        String buttonType = getButtonType(ticket);
        if (buttonType == null || !buttonType.equals("ticket")) {
            Component component = MiniMessage.miniMessage().deserialize("<red>You must be holding a ticket to perform that action!");
            player.sendMessage(component);
            return Command.SINGLE_SUCCESS;
        }

        ItemMeta meta = ticket.getItemMeta();
        assert meta != null;
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        if (!dataContainer.has(PURCHASE_MESSAGE)) {
            Component component = MiniMessage.miniMessage().deserialize("<red>The held ticket does not have a purchase message set.");
            player.sendMessage(component);
            return Command.SINGLE_SUCCESS;
        }
        dataContainer.remove(PURCHASE_MESSAGE);
        ticket.setItemMeta(meta);
        Component component = MiniMessage.miniMessage().deserialize("<green>The held ticket's purchase message was removed.");
        player.sendMessage(component);

        return Command.SINGLE_SUCCESS;
    }
}
