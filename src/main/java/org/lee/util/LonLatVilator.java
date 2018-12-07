package org.lee.util;

import java.util.*;
import java.util.stream.Collectors;

public class LonLatVilator {
    Map<String, CounterDetail> counter = new HashMap<>();
    private final static String SPLITOR = ":";
    public final static int numLimit = 29;
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
                System.out.println(String.format("经纬度超过默认值%d, 总数为d%设备编码: ", numLimit, value.getCount()) + value.getDeviceCodes());
            }
        });
    }

    public Map<String, List<String>> alarmData(){
        return counter.entrySet()
                .stream()
                .filter(ele -> ele.getValue().getCount() > numLimit)
                .collect(Collectors.toMap(Map.Entry::getKey, ele -> ele.getValue().getDeviceCodes()));
    }

    public void dec(String lon, String lat, String deviceCode) {
        CounterDetail counterDetail = counter.get(lon +SPLITOR + lat);
        if (Objects.nonNull(counterDetail)) {
            List<String> deviceCodes = counterDetail.getDeviceCodes();
            deviceCodes.remove(deviceCode);
            counterDetail.setCount(counterDetail.getCount() - 1);
            counter.replace(lon + SPLITOR + lat, counterDetail);
        }
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
