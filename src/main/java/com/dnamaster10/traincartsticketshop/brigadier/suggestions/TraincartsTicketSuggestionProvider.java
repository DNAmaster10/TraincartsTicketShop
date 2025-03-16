package com.dnamaster10.traincartsticketshop.brigadier.suggestions;

import com.dnamaster10.traincartsticketshop.util.Traincarts;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.concurrent.CompletableFuture;

public class TraincartsTicketSuggestionProvider {

    public static CompletableFuture<Suggestions> filterTraincartsTicketSuggestions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        String remainingLowercase = builder.getRemainingLowerCase();
        Traincarts.getTicketNames().stream()
                .filter(entry -> entry.toLowerCase().startsWith(remainingLowercase))
                .forEach(entry -> {
                    if (entry.contains(" ")) builder.suggest("\"" + entry + "\"");
                    builder.suggest(entry);
                });
        return builder.buildFuture();
    }

}
