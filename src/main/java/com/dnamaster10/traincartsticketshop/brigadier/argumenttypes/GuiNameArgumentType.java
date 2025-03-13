package com.dnamaster10.traincartsticketshop.brigadier.argumenttypes;

import com.dnamaster10.traincartsticketshop.util.Utilities;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public class GuiNameArgumentType implements CustomArgumentType.Converted<String, String> {

    @Override
    public @NotNull String convert(String nativeType) throws CommandSyntaxException {
        if (nativeType.length() > 20) {
            Message message = MessageComponentSerializer.message().serialize(Component.text("Gui ID too long!", NamedTextColor.RED));
            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        } else if (Utilities.checkSpecialCharacters(nativeType)) {
            Message message = MessageComponentSerializer.message().serialize(Component.text("Only letters, numbers, '-', and '_' are allowed in Gui IDs!", NamedTextColor.RED));
            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        }

        return nativeType;
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }
}
