package org.lee.util;

import java.util.List;

public class CounterDetail {
    private int count;
    private List<String> deviceCodes;

    public CounterDetail(int count, List<String> deviceCodes) {
        this.count = count;
        this.deviceCodes = deviceCodes;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getDeviceCodes() {
        return deviceCodes;
    }

    public void setDeviceCodes(List<String> deviceCodes) {
        this.deviceCodes = deviceCodes;
    }
}
