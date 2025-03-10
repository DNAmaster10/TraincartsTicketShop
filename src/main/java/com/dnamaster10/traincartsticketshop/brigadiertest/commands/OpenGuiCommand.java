package com.dnamaster10.traincartsticketshop.brigadiertest.commands;

import com.dnamaster10.traincartsticketshop.brigadiertest.argumenttypes.GuiNameArgumentType;
import com.dnamaster10.traincartsticketshop.brigadiertest.suggestions.GuiNameSuggestionProvider;
import com.dnamaster10.traincartsticketshop.objects.guis.ShopGui;
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

public class OpenGuiCommand implements TicketShopCommand {

    @Override
    public LiteralCommandNode<CommandSourceStack> getRootNode() {
        return Commands.literal("open")
            .requires(ctx ->
                ctx.getExecutor() instanceof Player &&
                ctx.getExecutor().hasPermission("traincartsticketshop.gui.open"))
            .then(Commands.argument("id", new GuiNameArgumentType())
            .suggests(GuiNameSuggestionProvider::getAllSuggestions)
            .executes(this::execute)
        ).build();
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx) {
        GuiDataAccessor guiAccessor = new GuiDataAccessor();
        String guiName = StringArgumentType.getString(ctx, "id");
        Player player = (Player) ctx.getSource().getExecutor();
        assert player != null;

        if (!guiAccessor.checkGuiByName(guiName)) {
            Component component = MiniMessage.miniMessage().deserialize("<red>Gui \"" + guiName + "\" does not exist.");
            player.sendMessage(component);
            return Command.SINGLE_SUCCESS;
        }

        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            getPlugin().getGuiManager().openNewSession(player);
            int guiId = guiAccessor.getGuiIdByName(guiName);
            ShopGui gui = new ShopGui(player, guiId);
            gui.open();
        });
        return Command.SINGLE_SUCCESS;
    }
}
