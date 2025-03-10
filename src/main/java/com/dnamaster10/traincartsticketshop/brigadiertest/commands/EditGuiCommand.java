package com.dnamaster10.traincartsticketshop.brigadiertest.commands;

import com.dnamaster10.traincartsticketshop.brigadiertest.argumenttypes.GuiNameArgumentType;
import com.dnamaster10.traincartsticketshop.brigadiertest.suggestions.GuiNameSuggestionProvider;
import com.dnamaster10.traincartsticketshop.objects.guis.EditGui;
import com.dnamaster10.traincartsticketshop.util.database.accessors.GuiDataAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.GuiDatabaseObject;
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

public class EditGuiCommand implements TicketShopCommand {
    @Override
    public LiteralCommandNode<CommandSourceStack> getRootNode() {
        return Commands.literal("edit")
                .requires(ctx ->
                        ctx.getExecutor() instanceof Player player
                                && (player.hasPermission("traincartsticketshop.gui.edit")
                                || player.hasPermission("traincartsticketshop.admin.gui.edit"))
                )
                .then(Commands.argument("id", new GuiNameArgumentType()).suggests(GuiNameSuggestionProvider::getEditSuggestions)
                        .executes(this::execute)).build();
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            String guiName = StringArgumentType.getString(ctx, "id");
            Player player = (Player) ctx.getSource().getExecutor();

            if (player == null) return;

            GuiDataAccessor guiAccessor = new GuiDataAccessor();

            if (!guiAccessor.checkGuiByName(guiName)) {
                Component component = MiniMessage.miniMessage().deserialize("<red>Gui \"" + guiName + "\" does not exist.");
                player.sendMessage(component);
                return;
            }

            GuiDatabaseObject gui = guiAccessor.getGuiByName(guiName);

            if (!player.hasPermission("traincartsticketshop.admin.gui.edit")) {
                if (!guiAccessor.playerCanEdit(gui.id(), player.getUniqueId().toString())) {
                    Component component = MiniMessage.miniMessage().deserialize("<red>You do not have permission to edit that Gui.");
                    player.sendMessage(component);
                    return;
                }
            }

            getPlugin().getGuiManager().openNewSession(player);
            EditGui editGui = new EditGui(player, gui.id());
            editGui.open();
        });
        return Command.SINGLE_SUCCESS;
    }
}
