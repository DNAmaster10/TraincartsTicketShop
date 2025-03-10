package com.dnamaster10.traincartsticketshop.brigadiertest.commands;

import com.dnamaster10.traincartsticketshop.brigadiertest.argumenttypes.GuiNameArgumentType;
import com.dnamaster10.traincartsticketshop.brigadiertest.suggestions.GuiNameSuggestionProvider;
import com.dnamaster10.traincartsticketshop.util.database.accessors.GuiDataAccessor;
import com.dnamaster10.traincartsticketshop.util.database.accessors.GuiEditorsDataAccessor;
import com.dnamaster10.traincartsticketshop.util.database.accessors.LinkDataAccessor;
import com.dnamaster10.traincartsticketshop.util.database.accessors.TicketDataAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.GuiDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class GuiInfoCommand implements TicketShopCommand {
    @Override
    public LiteralCommandNode<CommandSourceStack> getRootNode() {
        return Commands.literal("info")
                .requires(ctx -> {
                    if (ctx.getExecutor() instanceof Player player) {
                        return player.hasPermission("traincartsticketshop.gui.info");
                    }
                    return true;
                }).then(Commands.argument("id", new GuiNameArgumentType()).suggests(GuiNameSuggestionProvider::getAllSuggestions)
                        .executes(this::execute)).build();
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            String guiName = StringArgumentType.getString(ctx, "id");

            GuiDataAccessor guiAccessor = new GuiDataAccessor();
            if (!guiAccessor.checkGuiByName(guiName)) {
                Component component = MiniMessage.miniMessage().deserialize("<red>Gui \"" + guiName + "\" does not exist.");
                ctx.getSource().getSender().sendMessage(component);
                return;
            }

            GuiDatabaseObject gui = guiAccessor.getGuiByName(guiName);

            TicketDataAccessor ticketDataAccessor = new TicketDataAccessor();
            LinkDataAccessor linkDataAccessor = new LinkDataAccessor();
            GuiEditorsDataAccessor guiEditorsAccessor = new GuiEditorsDataAccessor();

            int totalPages;
            int totalTickets;
            int totalLinks;
            List<String> guiEditors;
            String ownerUsername;

            try {
                totalPages = guiAccessor.getHighestPageNumber(gui.id()) + 1;
                totalTickets = ticketDataAccessor.getTotalTickets(gui.id());
                totalLinks = linkDataAccessor.getTotalLinks(gui.id());
                guiEditors = guiEditorsAccessor.getEditorUsernames(gui.id());
                ownerUsername = guiAccessor.getOwnerUsername(gui.id());
            } catch (QueryException e) {
                getPlugin().handleSqlException(e);
                return;
            }

            CommandSender sender = ctx.getSource().getSender();

            sender.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>Info for gui \"" + gui.name() + "\":"));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("| Owner: " + ownerUsername));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("| Pages: " + totalPages));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("| Tickets: " + totalTickets));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("| Links: " + totalLinks));
            sender.sendMessage(MiniMessage.miniMessage().deserialize("| Editors: " + String.join(",", guiEditors)));
        });
        return Command.SINGLE_SUCCESS;
    }
}
