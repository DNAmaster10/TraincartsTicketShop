package com.dnamaster10.traincartsticketshop.brigadier.commands.link;

import com.dnamaster10.traincartsticketshop.brigadier.commands.TicketShopCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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

import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.DEST_GUI_PAGE;
import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;

public class SetLinkDestinationPage implements TicketShopCommand {
    @Override
    public LiteralCommandNode<CommandSourceStack> getRootNode() {
        return Commands.literal("setDestinationPage")
                .requires(ctx -> ctx.getExecutor() instanceof Player player &&
                        player.hasPermission("traincartsticketshop.link.setdestinationpage"))
                .then(Commands.argument("page number", IntegerArgumentType.integer(0))
                        .executes(this::execute)).build();
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx) {
        int pageNumber = IntegerArgumentType.getInteger(ctx, "page number") - 1;
        Player executor = (Player) ctx.getSource().getExecutor();
        assert executor != null;

        ItemStack link = executor.getInventory().getItemInMainHand();
        String buttonType = getButtonType(link);
        if (buttonType == null || !buttonType.equals("link")) {
            Component component = MiniMessage.miniMessage().deserialize("<red>You must be holding a link to perform that action.");
            executor.sendMessage(component);
            return Command.SINGLE_SUCCESS;
        }

        ItemMeta meta = link.getItemMeta();
        assert meta != null;
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(DEST_GUI_PAGE, PersistentDataType.INTEGER, pageNumber);
        link.setItemMeta(meta);

        Component component = MiniMessage.miniMessage().deserialize("<green>Link page set successfully.");
        executor.sendMessage(component);
        return Command.SINGLE_SUCCESS;

    }
}
