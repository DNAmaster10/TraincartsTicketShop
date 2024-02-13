package com.dnamaster10.traincartsticketshop.util;

public class Utilities {
    public static boolean isInt(String value) {
        //Returns true if string is an integer
        try {
            Integer.parseInt(value);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
}
