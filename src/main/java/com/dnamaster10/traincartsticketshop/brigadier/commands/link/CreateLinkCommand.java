package com.dnamaster10.traincartsticketshop.brigadier.commands.link;

import com.dnamaster10.traincartsticketshop.brigadier.argumenttypes.GuiNameArgumentType;
import com.dnamaster10.traincartsticketshop.brigadier.commands.TicketShopCommand;
import com.dnamaster10.traincartsticketshop.brigadier.suggestions.GuiNameSuggestionProvider;
import com.dnamaster10.traincartsticketshop.objects.buttons.Link;
import com.dnamaster10.traincartsticketshop.util.Utilities;
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
import org.bukkit.inventory.ItemStack;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class CreateLinkCommand implements TicketShopCommand {

    @Override
    public LiteralCommandNode<CommandSourceStack> getRootNode() {
        return Commands.literal("create")
                .requires(ctx -> ctx.getExecutor() instanceof Player player &&
                        player.hasPermission("traincartsticketshop.link.create"))
                .then(Commands.argument("gui id", new GuiNameArgumentType()).suggests(GuiNameSuggestionProvider::getAllSuggestions)
                        .executes(this::execute)).build();
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx) {
        String guiName = StringArgumentType.getString(ctx, "gui id");
        Player executor = (Player) ctx.getSource().getExecutor();
        assert executor != null;

        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            GuiDataAccessor guiAccessor = new GuiDataAccessor();

            if (!guiAccessor.checkGuiByName(guiName)) {
                Component component = MiniMessage.miniMessage().deserialize("<red>Gui \"" + guiName + "\" does not exist!");
                executor.sendMessage(component);
                return;
            }

            GuiDatabaseObject gui = guiAccessor.getGuiByName(guiName);

            Link link = new Link(gui.id(), 0, Utilities.parseColour(guiName));
            ItemStack linkItem = link.getItemStack();
            executor.getInventory().addItem(linkItem);
        });

        return Command.SINGLE_SUCCESS;
    }
}
