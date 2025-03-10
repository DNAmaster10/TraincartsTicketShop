package com.dnamaster10.traincartsticketshop.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Utilities {
    /**
     * Returns true if the passed String can be parsed as an integer.
     *
     * @param value Input String
     * @return Boolean indicating whether the String can be parsed as an integer
     * */
    public static boolean isInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    /**
     * Returns true if the passed string can be parsed as a double.
     *
     * @param value Input String
     * @return Boolean indicating whether the String can be parsed as a double
     */
    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Returns the minimum number of pages required to contain the specified number of elements.
     *
     * @param elementCount The number of elements
     * @param pageSize The maximum number of elements a page can hold
     * @return Minimum number of pages required to hold the specified number of elements
     * */
    public static int getPageCount(int elementCount, int pageSize) {
        //Returns the minimum amount of pages that would be required to hold given elements
        return (int) Math.ceil((double) elementCount / pageSize);
    }

    /**
     * Joins arguments within a String array surrounded by double quotes into a single array element.
     *
     * @param args Input array
     * @return An array of Strings with elements surrounded by double quotes concatenated together
     * */
    public static String[] concatenateQuotedStrings(String[] args) {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder inQuote = new StringBuilder();
        StringBuilder outQuote = new StringBuilder();
        boolean inQuotes = false;

        for (String word : args) {
            if (inQuotes) {
                inQuote.append(' ');
            } else if (!outQuote.isEmpty()){
                result.add(outQuote.toString());
                outQuote.setLength(0);
            }
            for (char character : word.toCharArray()) {
                if (character == '"') {
                    if (inQuotes) {
                        result.add(inQuote.toString());
                        inQuote.setLength(0);
                        inQuotes = false;
                    } else {
                        if (!outQuote.isEmpty()) {
                            result.add(outQuote.toString());
                            outQuote.setLength(0);
                        }
                        inQuotes = true;
                    }
                } else {
                    if (inQuotes) {
                        inQuote.append(character);
                    } else {
                        outQuote.append(character);
                    }
                }
            }
        }
        if (inQuotes && !inQuote.isEmpty()) result.add(inQuote.toString());
        else if (!outQuote.isEmpty()) result.add(outQuote.toString());

        return result.toArray(String[]::new);
    }

    private static final Pattern STRING_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");

    /**
     * Checks if the input String contains any characters other than Aa - Zz, numbers, underscores and dashes.
     *
     * @param input Input String
     * @return True if special characters are present
     */
    public static boolean checkSpecialCharacters(String input) {
        //Returns true if string contains characters other than Aa-Zz, dashes, numbers, and underscores.
        return !STRING_PATTERN.matcher(input).matches();
    }

    /**
     * Takes a list of strings and surrounds any strings in the list which contain white space with double quotes.
     *
     * @param input List to change
     */
    public static void quoteSpacedStrings(List<String> input) {
        for (int i = 0; i < input.size(); i++) {
            String word = input.get(i);
            if (word.contains(" ")) input.set(i, "\"" + word + "\"");
        }
    }

    /**
     * Takes a raw String as input and applies colour formatting.
     *
     * @param input Raw string input
     * @return Chat component with the colours applied
     */
    public static Component parseColour(String input) {
        return MiniMessage.miniMessage().deserialize(input);
    }

    /**
     * Takes a chat component and converts it to a String.
     *
     * @param component Component to convert
     * @return Component converted to a String
     */
    public static String componentToString(Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

    /**
     * Takes a component and returns the raw text without chat formatting.
     *
     * @param component The component to strip
     * @return The raw text contained within the component
     */
    public static String stripColour(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
}
