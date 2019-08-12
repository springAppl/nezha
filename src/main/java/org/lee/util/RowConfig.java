package org.lee.util;

import java.util.List;

public class RowConfig {

    List<CellConfig> cellConfigs;

    // 选填
    private Integer rowNum;

    private boolean ignore = false;


    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }


    public List<CellConfig> getCellConfigs() {
        return cellConfigs;
    }

    public void setCellConfigs(List<CellConfig> cellConfigs) {
        this.cellConfigs = cellConfigs;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    @Override
    public String toString() {
        return "RowConfig{" +
                "cellConfigs=" + cellConfigs +
                ", rowNum=" + rowNum +
                ", ignore=" + ignore +
                '}';
    }
}
