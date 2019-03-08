package org.lee.util;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataWasher {

    private Map<String, Map<String, String>> policeMap;

    LonLatVilator vilator = new LonLatVilator();


    public List<Map<String, String>> fillData(List<Map<String, String>> innerData, List<Map<String, String>> outData) throws IOException, DataInvalidException, DuplicateAdminAreaException {
        List<Map<String, String>> data = new ArrayList<>();
        PoliceTableReader policeTableReader = new PoliceTableReader();
        //验证重复
        policeMap = policeTableReader.policeTable();

        data.addAll(
                innerData.stream().map(this::fillInnerData).collect(Collectors.toList())
        );
        data.addAll(
                outData.stream().map(this::fillOutData).collect(Collectors.toList())
        );
        return incLatExist(data);
    }

    public Map<String, String> fillInnerData(Map<String, String> innerData) {
        dealAdminArea(innerData);
        dealDeviceBrand(innerData);
        dealInnerDataPointType(innerData);
        dealInstallLocation(innerData, this::subInner);
        dealInnerDataLocationType(innerData);
        dealConnectInternet(innerData);
        dealPoliceCode(innerData);
        dealInnerInstallTime(innerData);
        dealManageUnit(innerData, this::subInner);
        dealPhone(innerData, this::subInner);
        dealSaveDay(innerData);
        dealDeviceState(innerData);
        dealLonLat(innerData, this::subInner);
        return innerData;
    }

    public String subInner(String data) {
        return data.substring(0, data.length());
    }

    public String subOut(String data) {
        return data.substring(0, 6);
    }

    public Map<String, String> fillOutData(Map<String, String> outData) {
        dealAdminArea(outData);
        dealDeviceBrand(outData);
        dealOutDataPointType(outData);
        dealInstallLocation(outData, this::subOut);
        dealOutDataLocationType(outData);
        dealConnectInternet(outData);
        dealPoliceCode(outData);
        dealOutInstallTime(outData);
        dealManageUnit(outData, this::subOut);
        dealPhone(outData, this::subOut);
        dealSaveDay(outData);
        dealDeviceState(outData);
        dealLonLat(outData, this::subOut);
        return outData;
    }

    // 处理行政区划
    public void  dealAdminArea(Map<String, String> data) {
        String deviceCode = data.get(ExcelCloumnName.DEVICE_CODE);
        String adminArea = data.get(ExcelCloumnName.ADMIN_AREA);
        if (!deviceCode.startsWith(adminArea)) {
            data.replace(ExcelCloumnName.ADMIN_AREA, deviceCode.substring(0, 8));
        }
    }


    // 摄像机品牌
    public void dealDeviceBrand(Map<String, String> data) {
        String value = data.get(ExcelCloumnName.DEVICE_BRAND);
        if (stringIsEmpty(value)) {
            data.replace(ExcelCloumnName.DEVICE_BRAND, "1");
        }
    }

    // 监控点位类型
    public void dealOutDataPointType(Map<String, String> data) {
        String value = data.get(ExcelCloumnName.POINT_TYPE);
        if (stringIsEmpty(value)) {
            data.replace(ExcelCloumnName.POINT_TYPE, "2");
        }
    }

    // 监控点位类型
    public void dealInnerDataPointType(Map<String, String> data) {
        String value = data.get(ExcelCloumnName.POINT_TYPE);
        if (stringIsEmpty(value)) {
            data.replace(ExcelCloumnName.POINT_TYPE, "4");
        }
    }

    // 安装地址
    public void dealInstallLocation(Map<String, String> data, Function<String, String> mapper) {
        String originAdminArea = data.get(ExcelCloumnName.INSTALL_LOCATION);
        if (stringIsEmpty(originAdminArea)) {
            String adminArea = data.get(ExcelCloumnName.ADMIN_AREA);
            try {
                Map<String, String> value = policeMap.get(mapper.apply(adminArea));
                if (Objects.isNull(value)) {
                    return;
                }
                data.replace(ExcelCloumnName.INSTALL_LOCATION, value.get(PoliceTableCloumnName.NAME));
            } catch (Exception e) {
                System.out.println("error");
            }
        }
    }

    // 摄像机位置类型
    public void dealOutDataLocationType(Map<String, String> data) {
        String value = data.get(ExcelCloumnName.LOCATION_TYPE);
        if (stringIsEmpty(value)) {
            data.replace(ExcelCloumnName.LOCATION_TYPE, "9");
        }
    }

    // 摄像机位置类型
    public void dealInnerDataLocationType(Map<String, String> data) {
        String value = data.get(ExcelCloumnName.LOCATION_TYPE);
        if (stringIsEmpty(value)) {
            data.replace(ExcelCloumnName.LOCATION_TYPE, "2");
        }
    }

    // 连网属性
    public void dealConnectInternet(Map<String, String> data) {
        String value = data.get(ExcelCloumnName.CONNECT_INTERNET);
        if (stringIsEmpty(value)) {
            data.replace(ExcelCloumnName.CONNECT_INTERNET, "0");
        }
    }

    // 所属辖区公安机关
    public void dealPoliceCode(Map<String, String> data) {
        String policeCode = data.get(ExcelCloumnName.POLICE_CODE);
        if (stringIsEmpty(policeCode)) {
            String adminArea = data.get(ExcelCloumnName.ADMIN_AREA);
            if (Objects.equals(adminArea.length(), 6)) {
                data.replace(ExcelCloumnName.POLICE_CODE, adminArea + "000000");
            } else if (Objects.equals(adminArea.length(), 8)) {
                data.replace(ExcelCloumnName.POLICE_CODE, adminArea + "0000");
            }
        }
    }

    // 安装时间
    public void dealInnerInstallTime(Map<String, String> data) {
        String value = data.get(ExcelCloumnName.INSTALL_TIME);
        if (stringIsEmpty(value)) {
            data.replace(ExcelCloumnName.INSTALL_TIME, "2017-10-01 10:30:59");
        }
    }

    // 安装时间
    public void dealOutInstallTime(Map<String, String> data) {
        String value = data.get(ExcelCloumnName.INSTALL_TIME);
        if (stringIsEmpty(value)) {
            data.replace(ExcelCloumnName.INSTALL_TIME, "2017-10-01 10:31:00");
        }
    }

    // 管理单位
    public void dealManageUnit(Map<String, String> data, Function<String, String> mapper) {
        String manageUnit = data.get(ExcelCloumnName.MANAGE_UNIT);
        if (stringIsEmpty(manageUnit)) {
            String adminArea = data.get(ExcelCloumnName.ADMIN_AREA);
            Map<String, String> value = policeMap.get(mapper.apply(adminArea));
            if (Objects.isNull(value)) {
                return;
            }
            data.replace(ExcelCloumnName.MANAGE_UNIT, value.get(PoliceTableCloumnName.NAME));
        }
    }

    // 联系方式
    public void dealPhone(Map<String, String> data, Function<String, String> mapper) {
        String phone = data.get(ExcelCloumnName.PHONE);
        if (stringIsEmpty(phone)) {
            String adminArea = data.get(ExcelCloumnName.ADMIN_AREA);
            Map<String, String> value = policeMap.get(mapper.apply(adminArea));
            if (Objects.isNull(value)) {
                return;
            }
            data.replace(ExcelCloumnName.PHONE, value.get(PoliceTableCloumnName.PHONE));
        }
    }

    // 保存天数
    public void dealSaveDay(Map<String, String> data) {
        String saveDay = data.get(ExcelCloumnName.SAVE_DAY);
        if (stringIsEmpty(saveDay)) {
            data.replace(ExcelCloumnName.SAVE_DAY, "30");
        }
    }

    // 设备状态
    public void dealDeviceState(Map<String, String> data) {
        String deviceState = data.get(ExcelCloumnName.DEVICE_STATE);
        if (stringIsEmpty(deviceState)) {
            data.replace(ExcelCloumnName.DEVICE_STATE, "1");
        }
    }

    public static boolean stringIsEmpty(String value) {
        return Objects.isNull(value) || value.trim().length() == 0;
    }

    public void dealLonLat(Map<String, String> data, Function<String, String> mapper) {

        String lon = data.get(ExcelCloumnName.DEVICE_LONGITUDE);
        String lat = data.get(ExcelCloumnName.DEVICE_LATITUDE);

        if (Objects.nonNull(lon)&&lon.trim().length() > 0 && Objects.nonNull(lat)&& lat.trim().length() > 0) {
            vilator.count(lon, lat, data.get(ExcelCloumnName.DEVICE_CODE));
            return;
        }

        Map<String, String> police = policeMap.get(mapper.apply(data.get(ExcelCloumnName.ADMIN_AREA)));
        if (Objects.isNull(police)) {
            return;
        }
        if (Objects.isNull(lon)) {
            data.replace(ExcelCloumnName.DEVICE_LONGITUDE, police.get(PoliceTableCloumnName.LON));
        }
        if (Objects.isNull(lat)) {
            data.replace(ExcelCloumnName.DEVICE_LATITUDE, police.get(PoliceTableCloumnName.LAT));
        }
        //是否大于29
        if (vilator.get(data.get(ExcelCloumnName.DEVICE_LONGITUDE), data.get(ExcelCloumnName.DEVICE_LATITUDE)) > 29) {
            incLat(data);
        }

        vilator.count(
                data.get(ExcelCloumnName.DEVICE_LONGITUDE),
                data.get(ExcelCloumnName.DEVICE_LATITUDE),
                data.get(ExcelCloumnName.DEVICE_CODE)
        );
    }

    private String zero10 = "0000000000";
    private void incLat(Map<String, String> data){
        // 纬度增加
        // 判读小数点后的位数
        String lat = data.get(ExcelCloumnName.DEVICE_LATITUDE);
        String[] strs = lat.split("\\.");
        String smallValue;
        vilator.inc();
        String bigValue;
        if (strs.length < 2 || Objects.isNull(strs[1])) {
            smallValue = zero10 + vilator.getInc();
            bigValue = lat;
        } else if (strs[1].length() < 10){
            bigValue = strs[0];
            smallValue = strs[1] + zero10.substring(0, 10 - strs[1].length());
        } else {
            bigValue = strs[0];
            smallValue = strs[1] + vilator.getInc();
        }
        data.replace(ExcelCloumnName.DEVICE_LATITUDE, bigValue + "." + smallValue);
    }
    private List<Map<String, String>> incLatExist(List<Map<String, String>> data){
        Map<String, List<String>> alarmData = vilator.alarmData();
        Map<String, Map<String, String>> dataMap = data.stream().collect(Collectors.toMap(ele -> ele.get(ExcelCloumnName.DEVICE_CODE), ele -> ele));
        for (Map.Entry<String, List<String>> entry : alarmData.entrySet()) {
            List<String> subDevices = entry.getValue().subList(LonLatVilator.numLimit, entry.getValue().size()).stream().collect(Collectors.toList());
            for (String ele : subDevices) {
                Map<String, String> cell = dataMap.get(ele);
                vilator.dec(cell.get(ExcelCloumnName.DEVICE_LONGITUDE), cell.get(ExcelCloumnName.DEVICE_LATITUDE), ele);
                vilator.inc();
                cell.replace(ExcelCloumnName.DEVICE_LATITUDE, cell.get(ExcelCloumnName.DEVICE_LATITUDE) + vilator.getInc());
                dataMap.replace(ele, cell);
            }
        }
        System.out.println("change: " + vilator.getInc());
        return dataMap.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

}
