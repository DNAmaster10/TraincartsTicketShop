package com.dnamaster10.traincartsticketshop.brigadiertest;

import com.dnamaster10.traincartsticketshop.brigadiertest.commands.*;
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

        //Register link commands
        LiteralArgumentBuilder<CommandSourceStack> link = Commands.literal("link");

        //Bind sub-commands to root command node
        command.then(gui);
        command.then(ticket);
        command.then(link);

        return command.build();
    }
}
