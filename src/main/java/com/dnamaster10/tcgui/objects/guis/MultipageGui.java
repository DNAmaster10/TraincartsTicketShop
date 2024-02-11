package com.dnamaster10.tcgui.objects.guis;

import com.dnamaster10.tcgui.objects.buttons.Button;

import java.util.HashMap;

public abstract class MultipageGui extends Gui {
    //Multipage guis store their inventory contents in an array to save on database queries
    //This hashmap isn't populated with a page until a page is accessed. A HashMap is used instead of a list
    //as it's possible to open guis from signs at a specific page rather than the first.
    //The hashmap holds the page number as the key.
    private final HashMap<Integer, Button[]> pages = new HashMap<>();
    private int currentPage;
    protected int getPageNumber() {
        return this.currentPage;
    }
    protected void setPageNumber(int pageNumber) {
        this.currentPage = pageNumber;
    }
    protected Button[] getPage(int pageNumber) {
        if (!pages.containsKey(pageNumber)) {
            //Return an empty page if no page exists
            return new Button[54];
        }
        return pages.get(pageNumber);
    }
    protected void setPage(int pageNumber, Button[] pageContents) {
        pages.put(pageNumber, pageContents);
    }
    protected abstract void nextPage();
    protected abstract void prevPage();
    @Override
    public void open() {
        //Opens a new inventory with the current page

    }
}
