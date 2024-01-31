package com.dnamaster10.tcgui.objects.guis;

public abstract class MultipageGui extends Gui {
    private int currentPage;
    protected int getPage() {
        return currentPage;
    }
    protected void setPage(int page) {
        this.currentPage = page;
    }
    protected abstract void nextPage();
    protected abstract void prevPage();
}
