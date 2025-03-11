package com.dnamaster10.traincartsticketshop.brigadiertest.commands;

import com.dnamaster10.traincartsticketshop.brigadiertest.argumenttypes.GuiNameArgumentType;
import com.dnamaster10.traincartsticketshop.brigadiertest.argumenttypes.PlayerNameArgumentType;
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

public class AddEditorCommand implements TicketShopCommand {
    @Override
    public LiteralCommandNode<CommandSourceStack> getRootNode() {
        return Commands.literal("addEditor")
                .requires(ctx -> {
                    if (ctx.getExecutor() instanceof Player player) {
                        return player.hasPermission("traincartsitcketshop.gui.addeditor") ||
                                player.hasPermission("traincartsticketshop.admin.gui.addeditor");
                    }
                    return true;
                }).then(Commands.argument("id", new GuiNameArgumentType()).suggests(GuiNameSuggestionProvider::getAddEditorSuggestions)
                        .then(Commands.argument("player", new PlayerNameArgumentType()).suggests(PlayerNameSuggestionProvider::filterAllNameSuggestions)
                                .executes(this::execute))).build();
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            String guiName = StringArgumentType.getString(ctx, "id");
            String username = StringArgumentType.getString(ctx, "player");

            GuiDataAccessor guiAccessor = new GuiDataAccessor();

            if (!guiAccessor.checkGuiByName(guiName)) {
                Component component = MiniMessage.miniMessage().deserialize("<red>Gui \"" + guiName + "\" does not exist.");
                ctx.getSource().getSender().sendMessage(component);
                return;
            }

            GuiDatabaseObject gui = guiAccessor.getGuiByName(guiName);

            if (ctx.getSource().getExecutor() instanceof Player player) {
                if (!player.hasPermission("traincartsticketshop.admin.gui.addeditor")) {
                    if (!gui.ownerUuid().equals(player.getUniqueId().toString())) {
                        Component component = MiniMessage.miniMessage().deserialize("<red>You must be the owner of a Gui to add an editor.");
                        player.sendMessage(component);
                        return;
                    }
                }
            }

            PlayerDatabaseObject editor;
            try {
                editor = Players.getPlayerByUsername(username);
            } catch (ModificationException e) {
                getPlugin().handleSqlException(e);
                return;
            }

            if (editor == null) {
                Component component = MiniMessage.miniMessage().deserialize("<red>Player \"" + username + "\" could not be found.");
                ctx.getSource().getSender().sendMessage(component);
                return;
            }

            if (gui.ownerUuid().equalsIgnoreCase(editor.uuid())) {
                Component component = MiniMessage.miniMessage().deserialize("<red>Player \"" + editor.username() + "\" already owns that Gui.");
                ctx.getSource().getSender().sendMessage(component);
                return;
            }
            GuiEditorsDataAccessor guiEditorsAccessor = new GuiEditorsDataAccessor();
            if (guiEditorsAccessor.checkGuiEditorByUuid(gui.id(), editor.uuid())) {
                Component component = MiniMessage.miniMessage().deserialize("<red>Player \"" + editor.username() + "\" is already an editor of that Gui.");
                ctx.getSource().getSender().sendMessage(component);
                return;
            }

            try {
                guiEditorsAccessor.addGuiEditor(gui.id(), editor.uuid());
            } catch (ModificationException e) {
                getPlugin().handleSqlException(e);
                return;
            }

            Component component = MiniMessage.miniMessage().deserialize("<green>Player \"" + editor.username() + "\" has been added as an editor of \"" + gui.name() + "\"");
            ctx.getSource().getSender().sendMessage(component);
        });
        return Command.SINGLE_SUCCESS;
    }
}
