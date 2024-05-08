package com.dnamaster10.traincartsticketshop.util;

public class Utilities {
    public static boolean isInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    /**
     * Returns the minimum amount of pages that would be needed to hold the given number of elements.
     *
     * @param elementCount the amount of elements needed
     * @param pageSize the maximum number of elements a singe page can hold
     * @return page count
     * */
    public static int getPageCount(int elementCount, int pageSize) {
        //Returns the minimum amount of pages that would be required to hold given elements
        return (int) Math.ceil((double) elementCount / pageSize);
    }
}
