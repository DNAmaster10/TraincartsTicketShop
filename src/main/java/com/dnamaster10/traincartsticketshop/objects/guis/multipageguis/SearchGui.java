package com.dnamaster10.traincartsticketshop.objects.guis.multipageguis;

import com.dnamaster10.traincartsticketshop.objects.guis.multipageguis.MultipageGui;

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
    protected void setSearchGuiId(int id) {
        this.searchGuiId = id;
    }
    protected int getSearchGuiId() {
        return this.searchGuiId;
    }
}
