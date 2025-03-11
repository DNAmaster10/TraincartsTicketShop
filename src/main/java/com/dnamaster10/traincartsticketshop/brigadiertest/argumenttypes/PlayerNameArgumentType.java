package com.dnamaster10.traincartsticketshop.brigadiertest.argumenttypes;

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

public class PlayerNameArgumentType implements CustomArgumentType.Converted<String, String> {

    @Override
    public @NotNull String convert(@NotNull String nativeType) throws CommandSyntaxException {
        if (nativeType.length() > 16) {
            Message message = MessageComponentSerializer.message().serialize(Component.text("Username too long!", NamedTextColor.RED));
            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        } else if (nativeType.length() < 3) {
            Message message = MessageComponentSerializer.message().serialize(Component.text("Username too short!", NamedTextColor.RED));
            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        } else if (!Utilities.checkNumbersLettersUnderscores(nativeType)) {
            Message message = MessageComponentSerializer.message().serialize(Component.text("Invalid username!", NamedTextColor.RED));
            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        }
        return nativeType;
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }
}
