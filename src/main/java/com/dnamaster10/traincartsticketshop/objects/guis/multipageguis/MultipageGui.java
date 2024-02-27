package com.dnamaster10.traincartsticketshop.objects.guis.multipageguis;

import com.dnamaster10.traincartsticketshop.objects.guis.Gui;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public abstract class MultipageGui extends Gui {
    private int currentPage;
    private int totalPages;
    private static final int maxPages = getPlugin().getConfig().getInt("MaxPagesPerGui");
    protected abstract void nextPage();
    protected abstract void prevPage();

    protected int getPageNumber() {return this.currentPage;}
    protected void setPageNumber(int pageNumber) {this.currentPage = pageNumber;}
    protected int getTotalPages() {return this.totalPages;}
    protected void setTotalPages(int totalPages) {this.totalPages = totalPages;}
    protected int getMaxPages() {return maxPages;}
}
