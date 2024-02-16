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
    public static int getPageCount(int elementCount, int pageSize) {
        //Returns the minimum amount of pages that would be required to hold given elements
        return (int) Math.ceil((double) elementCount / pageSize);
    }
}
