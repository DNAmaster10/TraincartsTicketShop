package com.dnamaster10.traincartsticketshop.brigadiertest.commands;

import com.dnamaster10.traincartsticketshop.brigadiertest.argumenttypes.GuiNameArgumentType;
import com.dnamaster10.traincartsticketshop.brigadiertest.suggestions.GuiNameSuggestionProvider;
import com.dnamaster10.traincartsticketshop.brigadiertest.suggestions.PlayerNameSuggestionProvider;
import com.dnamaster10.traincartsticketshop.util.Players;
import com.dnamaster10.traincartsticketshop.util.database.accessors.GuiDataAccessor;
import com.dnamaster10.traincartsticketshop.util.database.accessors.GuiEditorsDataAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.GuiDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.PlayerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class RemoveGuiEditorCommand implements TicketShopCommand {
    @Override
    public LiteralCommandNode<CommandSourceStack> getRootNode() {
        return Commands.literal("removeEditor")
                .requires(ctx -> {
                    if (ctx.getExecutor() instanceof Player player) {
                        return player.hasPermission("traincartsticketshop.gui.removeeditor") || player.hasPermission("traincartsticketshop.admin.gui.removeeditor");
                    }
                    return true;
                }).then(Commands.argument("id", new GuiNameArgumentType()).suggests(GuiNameSuggestionProvider::getRemoveEditorSuggestions)
                        .then(Commands.argument("player", ArgumentTypes.player()).suggests(PlayerNameSuggestionProvider::filterGuiEditorSuggestions)
                                .executes(this::execute))).build();
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            String guiName = StringArgumentType.getString(ctx, "id");
            String editorName = StringArgumentType.getString(ctx, "player");

            GuiDataAccessor guiAccessor = new GuiDataAccessor();
            if (!guiAccessor.checkGuiByName(guiName)) {
                Component component = MiniMessage.miniMessage().deserialize("<red>Gui \"" + guiName + "\" does not exist.");
                ctx.getSource().getSender().sendMessage(component);
                return;
            }

            GuiDatabaseObject gui = guiAccessor.getGuiByName(guiName);

            if (ctx.getSource().getExecutor() instanceof Player player) {
                if (!player.hasPermission("traincartsticketshop.admin.gui.removeeditor")) {
                    if (!player.getUniqueId().toString().equalsIgnoreCase(gui.ownerUuid())) {
                        Component component = MiniMessage.miniMessage().deserialize("<red>You do not own that Gui.");
                        player.sendMessage(component);
                        return;
                    }
                }
            }

            GuiEditorsDataAccessor editorsAccessor = new GuiEditorsDataAccessor();
            PlayerDatabaseObject editor;
            try {
                editor = Players.getPlayerByUsername(editorName);
            } catch (ModificationException e) {
                getPlugin().handleSqlException(e);
                return;
            }

            if (editor == null) {
                Component component = MiniMessage.miniMessage().deserialize("<red>Player \"" + editorName + "\" does not exist");
                ctx.getSource().getSender().sendMessage(component);
                return;
            }

            if (!editorsAccessor.checkGuiEditorByUuid(gui.id(), editor.uuid())) {
                Component component = MiniMessage.miniMessage().deserialize("<red>Player \"" + editor.username() + "\" is not an editor of that gui.");
                ctx.getSource().getSender().sendMessage(component);
                return;
            }

            try {
                editorsAccessor.removeGuiEditor(gui.id(), editor.uuid());
            } catch (ModificationException e) {
                getPlugin().handleSqlException(e);
                return;
            }

            ctx.getSource().getSender().sendMessage("<green>Player \"" + editor.username() + "\" was removed as an editor of Gui \"" + gui.name() + "\"");
        });
        return Command.SINGLE_SUCCESS;
    }
}
