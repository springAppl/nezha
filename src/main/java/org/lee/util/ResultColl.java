package org.lee.util;

import java.util.List;
import java.util.Map;

public class ResultColl {
    private List<Map<String, String>> leftData;
    private List<Map<String, String>> resultData;

    public ResultColl(List<Map<String, String>> leftData, List<Map<String, String>> resultData){
        this.leftData = leftData;
        this.resultData = resultData;
    }

    public List<Map<String, String>> getLeftData() {
        return leftData;
    }

    public void setLeftData(List<Map<String, String>> leftData) {
        this.leftData = leftData;
    }

    public List<Map<String, String>> getResultData() {
        return resultData;
    }

    public void setResultData(List<Map<String, String>> resultData) {
        this.resultData = resultData;
    }
}
