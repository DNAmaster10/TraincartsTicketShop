package com.dnamaster10.tcgui.util.gui;

public class LastGui {
    String lastGuiName;
    int lastPageNum;
    public String getLastGuiName() {
        return lastGuiName;
    }
    public int getLastPageNum() {
        return lastPageNum;
    }
    public LastGui(String lastGuiName, int lastPageNum) {
        this.lastGuiName = lastGuiName;
        this.lastPageNum = lastPageNum;
    }
}
