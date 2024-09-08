package com.dnamaster10.traincartsticketshop.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class Utilities {
    public static boolean isInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    public static int getPageCount(int elementCount, int pageSize) {
        //Returns the minimum amount of pages that would be required to hold given elements
        return (int) Math.ceil((double) elementCount / pageSize);
    }

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
    public static boolean checkSpecialCharacters(String input) {
        //Returns true if string contains characters other than Aa-Zz, dashes, numbers, and underscores.
        return !STRING_PATTERN.matcher(input).matches();
    }
}
