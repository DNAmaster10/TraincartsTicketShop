package com.dnamaster10.traincartsticketshop.objects.guis;

import java.util.HashMap;

public class PageManager {
    private HashMap<Integer, Page> pages = new HashMap<>();
    private int currentPageNumber;

    public PageManager(int startingPage) {
        currentPageNumber = startingPage;
    }

    public void addPage(int pageNumber, Page page) {
        pages.put(pageNumber, page);
    }

    public boolean hasPage(int pageNumber) {
        return pages.containsKey(pageNumber);
    }

    public Page getPage(int pageNumber) {
        return pages.get(pageNumber);
    }

    public void clearCache() {
        pages.clear();
    }

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    public void setCurrentPageNumber(int pageNumber) {
        currentPageNumber = pageNumber;
    }
}
