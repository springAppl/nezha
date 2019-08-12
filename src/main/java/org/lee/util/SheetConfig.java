package org.lee.util;

import java.util.List;

public class SheetConfig {

    private Integer sheetIndex;

    private String stopFlag;

    private List<RowConfig> rowConfigs;


    public String getStopFlag() {
        return stopFlag;
    }

    public void setStopFlag(String stopFlag) {
        this.stopFlag = stopFlag;
    }

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
}
