package com.dnamaster10.tcgui.commands.commandhandlers.company;

import com.dnamaster10.tcgui.commands.commandhandlers.CommandHandler;
import org.bukkit.command.CommandSender;

public class CompanyAddMemberCommandHandler extends CommandHandler {

    @Override
    protected boolean checkSync(CommandSender sender, String[] args) {
        return false;
    }

    @Override
    protected boolean checkAsync(CommandSender sender, String[] args) throws Exception {
        return false;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) throws Exception {

    }

}
