package com.dnamaster10.traincartsticketshop.brigadiertest.commands.gui;

import com.dnamaster10.traincartsticketshop.brigadiertest.argumenttypes.GuiNameArgumentType;
import com.dnamaster10.traincartsticketshop.brigadiertest.commands.TicketShopCommand;
import com.dnamaster10.traincartsticketshop.brigadiertest.suggestions.GuiNameSuggestionProvider;
import com.dnamaster10.traincartsticketshop.objects.guis.LinkSearchGui;
import com.dnamaster10.traincartsticketshop.util.database.accessors.GuiDataAccessor;
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

public class SearchLinksCommand implements TicketShopCommand {

    @Override
    public LiteralCommandNode<CommandSourceStack> getRootNode() {
        return Commands.literal("searchLinks")
                .requires(ctx -> ctx.getExecutor() instanceof Player player && player.hasPermission("traincartsticketshop.gui.search.links"))
                .then(Commands.argument("id", new GuiNameArgumentType()).suggests(GuiNameSuggestionProvider::getAllSuggestions)
                        .then(Commands.argument("search term", StringArgumentType.string())
                                .executes(this::execute))).build();
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            String guiName = StringArgumentType.getString(ctx, "id");
            String searchTerm = StringArgumentType.getString(ctx, "search term");
            Player player = (Player) ctx.getSource().getExecutor();

            GuiDataAccessor guiAccessor = new GuiDataAccessor();
            if (!guiAccessor.checkGuiByName(guiName)) {
                Component component = MiniMessage.miniMessage().deserialize("<red>Gui \"" + guiName + "\" does not exist.");
                player.sendMessage(component);
                return;
            }

            if (searchTerm.length() > 25) {
                Component component = MiniMessage.miniMessage().deserialize("<red>Search term cannot be longer than 25 characters in length");
                player.sendMessage(component);
                return;
            }

            getPlugin().getGuiManager().openNewSession(player);
            int guiId = guiAccessor.getGuiByName(guiName).id();
            LinkSearchGui gui = new LinkSearchGui(player, guiId, searchTerm);
            gui.open();
        });
        return Command.SINGLE_SUCCESS;
    }
}
