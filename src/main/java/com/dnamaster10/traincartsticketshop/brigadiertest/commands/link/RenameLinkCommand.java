package com.dnamaster10.traincartsticketshop.brigadiertest.commands.link;

import com.dnamaster10.traincartsticketshop.brigadiertest.argumenttypes.DisplayNameArgumentType;
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

import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;

public class RenameLinkCommand implements TicketShopCommand {
    @Override
    public LiteralCommandNode<CommandSourceStack> getRootNode() {
        return Commands.literal("rename")
                .requires(ctx -> ctx.getExecutor() instanceof Player player &&
                        player.hasPermission("traincartsticketshop.link.rename"))
                .then(Commands.argument("new name", new DisplayNameArgumentType())
                        .executes(this::execute)).build();
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx) {
        String newName = StringArgumentType.getString(ctx, "new name");
        Player executor = (Player) ctx.getSource().getExecutor();
        assert executor != null;

        ItemStack link = executor.getInventory().getItemInMainHand();
        String buttonType = getButtonType(link);
        if (buttonType == null || !buttonType.equals("link")) {
            Component component = MiniMessage.miniMessage().deserialize("<red>You must be holding a link to perform that action.");
            executor.sendMessage(component);
            return Command.SINGLE_SUCCESS;
        }

        Component colouredName = Utilities.parseColour(newName);

        ItemMeta meta = link.getItemMeta();
        assert meta != null;
        meta.displayName(colouredName);
        link.setItemMeta(meta);

        Component component = MiniMessage.miniMessage().deserialize("<green>Successfully renamed link!");
        executor.sendMessage(component);

        return Command.SINGLE_SUCCESS;
    }
}
