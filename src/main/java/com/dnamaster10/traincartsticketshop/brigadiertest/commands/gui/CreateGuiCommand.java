package com.dnamaster10.traincartsticketshop.brigadiertest.commands.gui;

import com.dnamaster10.traincartsticketshop.brigadiertest.argumenttypes.GuiNameArgumentType;
import com.dnamaster10.traincartsticketshop.brigadiertest.commands.TicketShopCommand;
import com.dnamaster10.traincartsticketshop.util.database.accessors.GuiDataAccessor;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class CreateGuiCommand implements TicketShopCommand {
    @Override
    public LiteralCommandNode<CommandSourceStack> getRootNode() {
        return Commands.literal("create")
                .requires(ctx -> ctx.getExecutor() instanceof Player player && player.hasPermission("traincartsticketshop.gui.create"))
                .then(Commands.argument("id", new GuiNameArgumentType())
                        .executes(this::execute)).build();
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            GuiDataAccessor guiAccessor = new GuiDataAccessor();
            String guiName = StringArgumentType.getString(ctx, "id");
            Player player = (Player) ctx.getSource().getSender();

            if (guiAccessor.checkGuiByName(guiName)) {
                Component component = MiniMessage.miniMessage().deserialize("<red>A Gui with the ID \"" + guiName + "\" already exists!");
                player.sendMessage(component);
                return;
            }

            try {
                guiAccessor.addGui(guiName, player.getUniqueId().toString());
            } catch (ModificationException e) {
                getPlugin().handleSqlException(e);
                return;
            }

            Component component = MiniMessage.miniMessage().deserialize("<green>Successfully created Gui with ID \"" + guiName + "\".");
            player.sendMessage(component);
            if (guiName.length() >= 15) {
                component = MiniMessage.miniMessage().deserialize("<yellow>Guis with IDs longer than 15 characters in length may not fit on signs.");
                player.sendMessage(component);
            }

        });
        return Command.SINGLE_SUCCESS;
    }
}
