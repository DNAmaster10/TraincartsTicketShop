package com.dnamaster10.traincartsticketshop.brigadier.commands.ticket;

import com.dnamaster10.traincartsticketshop.brigadier.commands.TicketShopCommand;
import com.dnamaster10.traincartsticketshop.brigadier.suggestions.TraincartsTicketSuggestionProvider;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static com.dnamaster10.traincartsticketshop.objects.buttons.DataKeys.TC_TICKET_NAME;
import static com.dnamaster10.traincartsticketshop.util.ButtonUtils.getButtonType;

public class SetTraincartsTicketCommand implements TicketShopCommand {
    @Override
    public LiteralCommandNode<CommandSourceStack> getRootNode() {
        return Commands.literal("setTraincartsTicket")
                .requires(ctx -> ctx.getExecutor() instanceof Player player &&
                        player.hasPermission("traincartsticketshop.ticket.settraincartsticket"))
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

        ItemStack ticket = executor.getInventory().getItemInMainHand();
        String buttonType = getButtonType(ticket);
        if (buttonType == null || !buttonType.equals("ticket")) {
            Component component = MiniMessage.miniMessage().deserialize("<red>You must be holding a ticket to perform that action.");
            executor.sendMessage(component);
            return Command.SINGLE_SUCCESS;
        }

        ItemMeta meta = ticket.getItemMeta();
        assert meta != null;
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(TC_TICKET_NAME, PersistentDataType.STRING, ticketName);
        ticket.setItemMeta(meta);

        Component component = MiniMessage.miniMessage().deserialize("<green>Successfully changed held ticket's Traincarts ticket.");
        executor.sendMessage(component);
        return Command.SINGLE_SUCCESS;
    }
}
