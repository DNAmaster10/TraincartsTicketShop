package com.dnamaster10.traincartsticketshop.brigadier.commands.ticket;

import com.dnamaster10.traincartsticketshop.brigadier.argumenttypes.DisplayNameArgumentType;
import com.dnamaster10.traincartsticketshop.brigadier.commands.TicketShopCommand;
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

import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;

public class RenameTicketCommand implements TicketShopCommand {
    @Override
    public LiteralCommandNode<CommandSourceStack> getRootNode() {
        return Commands.literal("rename")
                .requires(ctx -> ctx.getExecutor() instanceof Player player &&
                        player.hasPermission("traincartsticketshop.ticket.rename"))
                .then(Commands.argument("new name", new DisplayNameArgumentType())
                        .executes(this::execute)).build();
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx) {
        String newName = StringArgumentType.getString(ctx, "new name");
        Player executor = (Player) ctx.getSource().getExecutor();
        assert executor != null;

        ItemStack ticket = executor.getInventory().getItemInMainHand();
        String buttonType = getButtonType(ticket);
        if (buttonType == null || !buttonType.equals("ticket")) {
            Component component = MiniMessage.miniMessage().deserialize("<red>You must be holding a ticket to perform that action.");
            executor.sendMessage(component);
            return Command.SINGLE_SUCCESS;
        }

        Component colouredName = Utilities.parseColour(newName);

        ItemMeta meta = ticket.getItemMeta();
        assert meta != null;
        meta.displayName(colouredName);
        ticket.setItemMeta(meta);

        Component component = MiniMessage.miniMessage().deserialize("<green>Successfully renamed ticket!");
        executor.sendMessage(component);

        return Command.SINGLE_SUCCESS;
    }
}
