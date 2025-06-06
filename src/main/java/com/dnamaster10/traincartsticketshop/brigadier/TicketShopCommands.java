package com.dnamaster10.traincartsticketshop.brigadier;

import com.dnamaster10.traincartsticketshop.brigadier.commands.gui.*;
import com.dnamaster10.traincartsticketshop.brigadier.commands.link.CreateLinkCommand;
import com.dnamaster10.traincartsticketshop.brigadier.commands.link.RenameLinkCommand;
import com.dnamaster10.traincartsticketshop.brigadier.commands.link.SetLinkDestinationPage;
import com.dnamaster10.traincartsticketshop.brigadier.commands.ticket.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class TicketShopCommands {
    public static LiteralCommandNode<CommandSourceStack> getRootNode() {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("traincartsticketshop");

        //Register gui commands
        LiteralArgumentBuilder<CommandSourceStack> gui = Commands.literal("gui");
        gui.then(new AddEditorCommand().getRootNode());
        gui.then(new CreateGuiCommand().getRootNode());
        gui.then(new DeleteGuiCommand().getRootNode());
        gui.then(new EditGuiCommand().getRootNode());
        gui.then(new GuiInfoCommand().getRootNode());
        gui.then(new OpenGuiCommand().getRootNode());
        gui.then(new RemoveGuiEditorCommand().getRootNode());
        gui.then(new RenameGuiCommand().getRootNode());
        gui.then(new SearchLinksCommand().getRootNode());
        gui.then(new SearchTicketsCommand().getRootNode());
        gui.then(new SetGuiIdCommand().getRootNode());
        gui.then(new TransferGuiCommand().getRootNode());

        //Register ticket commands
        LiteralArgumentBuilder<CommandSourceStack> ticket = Commands.literal("ticket");
        ticket.then(new CreateTicketCommand().getRootNode());
        ticket.then(new RemoveTicketPurchaseMessageCommand().getRootNode());
        ticket.then(new RenameTicketCommand().getRootNode());
        ticket.then(new SetTicketPriceCommand().getRootNode());
        ticket.then(new SetTicketPurchaseMessageCommand().getRootNode());
        ticket.then(new SetTraincartsTicketCommand().getRootNode());

        //Register link commands
        LiteralArgumentBuilder<CommandSourceStack> link = Commands.literal("link");
        link.then(new CreateLinkCommand().getRootNode());
        link.then(new RenameLinkCommand().getRootNode());
        link.then(new SetLinkDestinationPage().getRootNode());

        //Bind sub-commands to root command node
        command.then(gui);
        command.then(ticket);
        command.then(link);

        return command.build();
    }
}
