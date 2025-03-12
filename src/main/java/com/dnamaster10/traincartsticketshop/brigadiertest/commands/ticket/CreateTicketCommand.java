package com.dnamaster10.traincartsticketshop.brigadiertest.commands.ticket;

import com.dnamaster10.traincartsticketshop.brigadiertest.commands.TicketShopCommand;
import com.dnamaster10.traincartsticketshop.brigadiertest.suggestions.TraincartsTicketSuggestionProvider;
import com.dnamaster10.traincartsticketshop.objects.buttons.Ticket;
import com.dnamaster10.traincartsticketshop.util.Traincarts;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class CreateTicketCommand implements TicketShopCommand {
    @Override
    public LiteralCommandNode<CommandSourceStack> getRootNode() {
        return Commands.literal("create")
                .requires(ctx -> ctx.getExecutor() instanceof Player player && player.hasPermission("traincartsticketshop.ticket.create"))
                .then(Commands.argument("traincarts ticket", StringArgumentType.string()).suggests(TraincartsTicketSuggestionProvider::filterTraincartsTicketSuggestions)
                        .executes(this::execute)).build();
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx) {
        String ticketName = StringArgumentType.getString(ctx, "traincarts ticket");
        Player executor = (Player) ctx.getSource().getExecutor();
        assert executor != null;

        if (!Traincarts.checkTicket(ticketName)) {
            Component component = MiniMessage.miniMessage().deserialize("<red>Traincarts ticket \"" + ticketName + "\" does not exist.");
            executor.sendMessage(component);
            return Command.SINGLE_SUCCESS;
        }

        double defaultPrice = getPlugin().getConfig().getDouble("DefaultTicketPrice");

        Ticket ticket = new Ticket(ticketName, ticketName, "", defaultPrice);

        ItemStack ticketItem = ticket.getItemStack();
        executor.getInventory().addItem(ticketItem);
        Component component = MiniMessage.miniMessage().deserialize("<green>Ticket created!");
        executor.sendMessage(component);
        return Command.SINGLE_SUCCESS;
    }
}
