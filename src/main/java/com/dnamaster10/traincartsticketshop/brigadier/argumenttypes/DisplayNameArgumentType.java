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
import org.jetbrains.annotations.NotNull;

public class DisplayNameArgumentType implements CustomArgumentType.Converted<String, String> {

    @Override
    public @NotNull String convert(@NotNull String nativeType) throws CommandSyntaxException {
        Component colouredComponent = Utilities.parseColour(nativeType);
        String rawText = Utilities.stripColour(colouredComponent);

        //TODO Check why only the first error gets thrown. i.e. try printing the raw display name to compare it.
        if (rawText.length() > 50) {
            Message message = MessageComponentSerializer.message().serialize(Component.text("Display names cannot contain more than 50 characters!"));
            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        } else if (Utilities.componentToString(colouredComponent).length() > 200) {
            Message message = MessageComponentSerializer.message().serialize(Component.text("Display names cannot contain that many colours!"));
            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        }

        return nativeType;
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }
}
