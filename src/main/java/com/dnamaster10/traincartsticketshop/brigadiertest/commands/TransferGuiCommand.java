package com.dnamaster10.traincartsticketshop.brigadiertest.commands;

import com.dnamaster10.traincartsticketshop.brigadiertest.argumenttypes.GuiNameArgumentType;
import com.dnamaster10.traincartsticketshop.brigadiertest.argumenttypes.PlayerNameArgumentType;
import com.dnamaster10.traincartsticketshop.brigadiertest.suggestions.GuiNameSuggestionProvider;
import com.dnamaster10.traincartsticketshop.brigadiertest.suggestions.PlayerNameSuggestionProvider;
import com.dnamaster10.traincartsticketshop.objects.guis.ConfirmGuiTransferGui;
import com.dnamaster10.traincartsticketshop.util.Players;
import com.dnamaster10.traincartsticketshop.util.database.accessors.GuiDataAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.GuiDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.PlayerDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class TransferGuiCommand implements TicketShopCommand {
    @Override
    public LiteralCommandNode<CommandSourceStack> getRootNode() {
        return Commands.literal("transfer")
                .requires(ctx -> {
                    if (ctx.getExecutor() instanceof Player player) {
                        return player.hasPermission("traincartsticketshop.admin.gui.transfer") ||
                                player.hasPermission("traincartsticketshop.gui.transfer");
                    }
                    return true;
                }).then(Commands.argument("id", new GuiNameArgumentType()).suggests(GuiNameSuggestionProvider::getTransferSuggestions)
                        .then(Commands.argument("player", new PlayerNameArgumentType()).suggests(PlayerNameSuggestionProvider::filterAllNameSuggestions)
                                .executes(this::execute))).build();
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx) {
        String guiName = StringArgumentType.getString(ctx, "id");
        String username = StringArgumentType.getString(ctx, "player");

        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            GuiDataAccessor guiAccessor = new GuiDataAccessor();
            if (!guiAccessor.checkGuiByName(guiName)) {
                Component component = MiniMessage.miniMessage().deserialize("<red>Gui \"" + guiName + "\" does not exist.");
                ctx.getSource().getSender().sendMessage(component);
                return;
            }

            PlayerDatabaseObject player;
            try {
                player = Players.getPlayerByUsername(username);
            } catch (ModificationException e) {
                getPlugin().handleSqlException(e);
                return;
            }

            if (player == null) {
                Component component = MiniMessage.miniMessage().deserialize("<red>Player \"" + username + "\" could not be found.");
                ctx.getSource().getSender().sendMessage(component);
                return;
            }

            GuiDatabaseObject gui = guiAccessor.getGuiByName(guiName);

            if (gui.ownerUuid().equalsIgnoreCase(player.uuid())) {
                Component component = MiniMessage.miniMessage().deserialize("<red>Player \"" + player.username() + "\" already owns that Gui.");
                ctx.getSource().getSender().sendMessage(component);
                return;
            }

            if (ctx.getSource().getExecutor() instanceof Player p) {
                if (!p.hasPermission("traincartsticketshop.admin.gui.transfer")) {
                    if (!p.getUniqueId().toString().equalsIgnoreCase(gui.ownerUuid())) {
                        Component component = MiniMessage.miniMessage().deserialize("<red>You do not own that Gui.");
                        p.sendMessage(component);
                        return;
                    }
                }
                getPlugin().getGuiManager().openNewSession(p);
                ConfirmGuiTransferGui newGui = new ConfirmGuiTransferGui(p, gui.id(), player.uuid());
                newGui.open();
                return;
            }

            try {
                guiAccessor.updateGuiOwner(gui.id(), player.uuid());
            } catch (ModificationException e) {
                getPlugin().handleSqlException(e);
                return;
            }

            Component component = MiniMessage.miniMessage().deserialize("<green>Gui \"" + guiName + "\" was transferred to " + player.username() + ".");
            ctx.getSource().getSender().sendMessage(component);
        });
        return Command.SINGLE_SUCCESS;
    }
}
