package com.dnamaster10.traincartsticketshop.brigadiertest.suggestions;

import com.dnamaster10.traincartsticketshop.util.database.accessors.GuiDataAccessor;
import com.dnamaster10.traincartsticketshop.util.database.accessors.GuiEditorsDataAccessor;
import com.dnamaster10.traincartsticketshop.util.database.accessors.PlayerDataAccessor;
import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.GuiDatabaseObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.concurrent.CompletableFuture;

public class PlayerNameSuggestionProvider {

    public static CompletableFuture<Suggestions> filterGuiEditorSuggestions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        String guiName = StringArgumentType.getString(ctx, "id");

        GuiDataAccessor guiDataAccessor = new GuiDataAccessor();
        if (!guiDataAccessor.checkGuiByName(guiName)) return null;
        GuiDatabaseObject gui = guiDataAccessor.getGuiByName(guiName);

        GuiEditorsDataAccessor editorsAccessor = new GuiEditorsDataAccessor();
        String remainingLowercase = builder.getRemainingLowerCase();
        editorsAccessor.getEditorUsernames(gui.id()).stream()
                .filter(entry -> entry.toLowerCase().startsWith(remainingLowercase))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> filterAllNameSuggestions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        PlayerDataAccessor playerDataAccessor = new PlayerDataAccessor();
        String remainingLowercase = builder.getRemainingLowerCase();

        playerDataAccessor.getUsernames().stream()
                .filter(entry -> entry.toLowerCase().startsWith(remainingLowercase))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

}
