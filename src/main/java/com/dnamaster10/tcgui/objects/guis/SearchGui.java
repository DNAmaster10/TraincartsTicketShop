package com.dnamaster10.tcgui.objects.guis;

public abstract class SearchGui extends MultipageGui {
    private String searchTerm;
    private String searchGuiName;
    private int searchGuiId;
    protected void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
    protected String getSearchTerm() {
        return this.searchTerm;
    }
    protected void setSearchGuiName(String searchGuiName) {
        this.searchGuiName = searchGuiName;
    }
    protected String getSearchGuiName() {
        return this.searchGuiName;
    }
    protected void setSearchGuiId(int id) {
        this.searchGuiId = id;
    }
    protected int getSearchGuiId() {
        return this.searchGuiId;
    }
}
