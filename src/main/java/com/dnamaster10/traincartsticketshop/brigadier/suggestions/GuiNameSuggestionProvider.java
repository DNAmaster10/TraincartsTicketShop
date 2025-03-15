package com.dnamaster10.traincartsticketshop.brigadier.suggestions;

import com.dnamaster10.traincartsticketshop.util.database.accessors.GuiDataAccessor;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class GuiNameSuggestionProvider {

    private static CompletableFuture<Suggestions> filterAllGuis(SuggestionsBuilder builder) {
        GuiDataAccessor guiDataAccessor = new GuiDataAccessor();
        String remainingLowercase = builder.getRemainingLowerCase();
        guiDataAccessor.getGuiNames().stream()
                .filter(entry -> entry.toLowerCase().startsWith(remainingLowercase))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> filterGuisOwnedBy(String uuid, SuggestionsBuilder builder) {
        GuiDataAccessor guiDataAccessor = new GuiDataAccessor();
        String remainingLowercase = builder.getRemainingLowerCase();
        guiDataAccessor.getGuisOwnedBy(uuid).stream()
                .filter(entry -> entry.name().toLowerCase().startsWith(remainingLowercase))
                .forEach(entry -> builder.suggest(entry.name()));
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> filterGuisEditableBy(String uuid, SuggestionsBuilder builder) {
        GuiDataAccessor guiDataAccessor = new GuiDataAccessor();
        String remainingLowercase = builder.getRemainingLowerCase();
        guiDataAccessor.getGuisEditableBy(uuid).stream()
                .filter(entry -> entry.name().toLowerCase().startsWith(remainingLowercase))
                .forEach(entry -> builder.suggest(entry.name()));
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> getAllSuggestions(CommandContext<CommandSourceStack> commandContext, SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
        return filterAllGuis(suggestionsBuilder);
    }

    public static CompletableFuture<Suggestions> getEditSuggestions(CommandContext<CommandSourceStack> commandContext, SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
        if (!(commandContext.getSource().getExecutor() instanceof Player player)) return null;
        if (player.hasPermission("traincartsticketshop.admin.gui.edit")) return filterAllGuis(suggestionsBuilder);
        if (!player.hasPermission("traincartsticketshop.gui.edit")) return null;

        return filterGuisEditableBy(player.getUniqueId().toString(), suggestionsBuilder);
    }

    public static CompletableFuture<Suggestions> getRenameSuggestions(CommandContext<CommandSourceStack> commandContext, SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
        if (!((commandContext.getSource().getExecutor()) instanceof Player player)) return filterAllGuis(suggestionsBuilder);
        if (player.hasPermission("traincartsticketshop.admin.gui.rename")) return filterAllGuis(suggestionsBuilder);
        if (!player.hasPermission("traincartsticketshop.gui.rename")) return null;

        //TODO Should this filter by guis editable by instead?
        return filterGuisOwnedBy(player.getUniqueId().toString(), suggestionsBuilder);
    }

    public static CompletableFuture<Suggestions> getSetIdSuggestions(CommandContext<CommandSourceStack> commandContext, SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
        if (!(commandContext.getSource().getExecutor() instanceof Player player)) return filterAllGuis(suggestionsBuilder);
        if (player.hasPermission("traincartsticketshop.admin.gui.setid")) return filterAllGuis(suggestionsBuilder);
        if (!player.hasPermission("traincartsticketshop.gui.setid")) return null;

        return filterGuisOwnedBy(player.getUniqueId().toString(), suggestionsBuilder);
    }

    public static CompletableFuture<Suggestions> getAddEditorSuggestions(CommandContext<CommandSourceStack> commandContext, SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
        if (!(commandContext.getSource().getExecutor() instanceof Player player)) return filterAllGuis(suggestionsBuilder);
        if (player.hasPermission("traincartsticketshop.admin.gui.addeditor")) return filterAllGuis(suggestionsBuilder);
        if (!player.hasPermission("traincartsticketshop.gui.addeditor")) return null;

        return filterGuisOwnedBy(player.getUniqueId().toString(), suggestionsBuilder);
    }

    public static CompletableFuture<Suggestions> getDeleteSuggestions(CommandContext<CommandSourceStack> commandContext, SuggestionsBuilder suggestionsBuilder) {
        if (!(commandContext.getSource().getExecutor() instanceof Player player)) return filterAllGuis(suggestionsBuilder);
        if (player.hasPermission("traincartsticketshop.admin.delete")) return filterAllGuis(suggestionsBuilder);
        if (!player.hasPermission("traincartsticketshop.gui.delete")) return null;

        return filterGuisOwnedBy(player.getUniqueId().toString(), suggestionsBuilder);
    }

    public static CompletableFuture<Suggestions> getRemoveEditorSuggestions(CommandContext<CommandSourceStack> commandContext, SuggestionsBuilder suggestionsBuilder) {
        if (!(commandContext.getSource().getExecutor() instanceof Player player)) return filterAllGuis(suggestionsBuilder);
        if (player.hasPermission("traincartsticketshop.admin.gui.removeeditor")) return filterAllGuis(suggestionsBuilder);
        if (!player.hasPermission("traincartsticketshop.gui.removeeditor")) return null;

        return filterGuisOwnedBy(player.getUniqueId().toString(), suggestionsBuilder);
    }

    public static CompletableFuture<Suggestions> getTransferSuggestions(CommandContext<CommandSourceStack> commandContext, SuggestionsBuilder suggestionsBuilder) {
        if (!(commandContext.getSource().getExecutor() instanceof Player player)) return filterAllGuis(suggestionsBuilder);
        if (player.hasPermission("traincartsticketshop.admin.gui.transfer")) return filterAllGuis(suggestionsBuilder);
        if (!player.hasPermission("traincartsticketshop.gui.transfer")) return null;

        return filterGuisOwnedBy(player.getUniqueId().toString(), suggestionsBuilder);
    }
}
