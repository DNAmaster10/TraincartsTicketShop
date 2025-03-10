package com.dnamaster10.traincartsticketshop.brigadiertest.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public interface TicketShopCommand {
    LiteralCommandNode<CommandSourceStack> getRootNode();
    int execute(CommandContext<CommandSourceStack> ctx);
}
