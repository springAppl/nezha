package org.lee.util;

import java.util.List;

public class SheetConfig {

    private Integer sheetIndex;

    private List<RowConfig> rowConfigs;


    public List<RowConfig> getRowConfigs() {
        return rowConfigs;
    }

    public void setRowConfigs(List<RowConfig> rowConfigs) {
        this.rowConfigs = rowConfigs;
    }


    public Integer getSheetIndex() {
        return sheetIndex;
    }

    public void setSheetIndex(Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
    }


    @Override
    public String toString() {
        return "SheetConfig{" +
                "sheetIndex=" + sheetIndex +
                ", rowConfigs=" + rowConfigs +
                '}';
    }
}
