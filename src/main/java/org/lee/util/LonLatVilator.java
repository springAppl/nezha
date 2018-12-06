package org.lee.util;

import java.util.*;

public class LonLatVilator {
    Map<String, CounterDetail> counter = new HashMap<>();
    private final static String SPLITOR = ":";
    private final static int numLimit = 29;
    public int inc = 0;

    public void count(String lon, String lat, String deviceCode) {
        CounterDetail counterDetail = counter.get(lon +SPLITOR + lat);
        if (Objects.isNull(counterDetail)) {
            List<String> deviceCodes = new ArrayList<>();
            deviceCodes.add(deviceCode);
            counterDetail = new CounterDetail(1, deviceCodes);
            counter.put(lon + SPLITOR + lat, counterDetail);
        } else {
            counterDetail.setCount(counterDetail.getCount() + 1);
            counterDetail.getDeviceCodes().add(deviceCode);
            counter.replace(lon + SPLITOR + lat, counterDetail);
        }
    }

    public void count(List<Map<String, String>> data) {
        data.forEach(ele -> count(ele.get(ExcelCloumnName.DEVICE_LONGITUDE), ele.get(ExcelCloumnName.DEVICE_LATITUDE), ele.get(ExcelCloumnName.DEVICE_CODE)));
    }

    public void alarm(){
        counter.forEach((key, value) -> {
            if (value.getCount() > numLimit) {
                System.out.println(String.format("经纬度超过默认值%d, 设备编码: ", numLimit) + value.getDeviceCodes());
            }
        });
    }

    public int get(String lon, String lat){
        CounterDetail detail = counter.get(lon + SPLITOR + lat);
        if (Objects.isNull(detail)) {
            return 0;
        }
        return detail.getCount();
    }

    public int getInc() {
        return inc;
    }

    public void inc() {
        this.inc = inc + 1;
    }
}
