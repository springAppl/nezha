package org.lee.util;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataWasher {

    private Map<String, Map<String, String>> policeMap;

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
        dealLonLat(data);
        return data;
    }

    public Map<String, String> fillInnerData(Map<String, String> innerData){
        dealDeviceBrand(innerData);
        dealInnerDataPointType(innerData);
        dealInstallLocation(innerData, this::subSix);
        dealInnerDataLocationType(innerData);
        dealConnectInternet(innerData);
        dealPoliceCode(innerData);
        dealInnerInstallTime(innerData);
        dealManageUnit(innerData, this::subSix);
        dealPhone(innerData, this::subSix);
        dealSaveDay(innerData);
        dealDeviceState(innerData);
        return innerData;
    }

    public String subSix(String data){
        return data.substring(0, 6);
    }

    public String subEight(String data){
        return data.substring(0, 8);
    }

    public Map<String, String> fillOutData(Map<String, String> outData){
        dealDeviceBrand(outData);
        dealOutDataPointType(outData);
        dealInstallLocation(outData, this::subEight);
        dealOutDataLocationType(outData);
        dealConnectInternet(outData);
        dealPoliceCode(outData);
        dealOutInstallTime(outData);
        dealManageUnit(outData, this::subEight);
        dealPhone(outData, this::subEight);
        dealSaveDay(outData);
        dealDeviceState(outData);
        return outData;
    }

    // 摄像机品牌
    public void dealDeviceBrand(Map<String, String> data){
        String value = data.get(ExcelCloumnName.DEVICE_BRAND);
        if (stringIsEmpty(value)) {
            data.replace(ExcelCloumnName.DEVICE_BRAND, "1");
        }
    }

    // 监控点位类型
    public void dealOutDataPointType(Map<String, String> data){
        String value = data.get(ExcelCloumnName.POINT_TYPE);
        if (stringIsEmpty(value)) {
            data.replace(ExcelCloumnName.POINT_TYPE, "2");
        }
    }

    // 监控点位类型
    public void dealInnerDataPointType(Map<String, String> data){
        String value = data.get(ExcelCloumnName.POINT_TYPE);
        if (stringIsEmpty(value)) {
            data.replace(ExcelCloumnName.POINT_TYPE, "4");
        }
    }

    // 安装地址
    public void dealInstallLocation(Map<String, String> data, Function<String, String> mapper){
        String originAdminArea = data.get(ExcelCloumnName.INSTALL_LOCATION);
        if (stringIsEmpty(originAdminArea)) {
            String adminArea = data.get(ExcelCloumnName.ADMIN_AREA);
            try {
                Map<String, String> value = policeMap.get(mapper.apply(adminArea));
                if (Objects.isNull(value)){
                    return;
                }
                data.replace(ExcelCloumnName.INSTALL_LOCATION, value.get(PoliceTableCloumnName.NAME));
            } catch (Exception e) {
                System.out.println("error");
            }
        }
    }

    // 摄像机位置类型
    public void dealOutDataLocationType(Map<String, String> data){
        String value = data.get(ExcelCloumnName.LOCATION_TYPE);
        if (stringIsEmpty(value)) {
            data.replace(ExcelCloumnName.LOCATION_TYPE, "9");
        }
    }
    // 摄像机位置类型
    public void dealInnerDataLocationType(Map<String, String> data){
        String value = data.get(ExcelCloumnName.LOCATION_TYPE);
        if (stringIsEmpty(value)) {
            data.replace(ExcelCloumnName.LOCATION_TYPE, "2");
        }
    }
    // 连网属性
    public void dealConnectInternet(Map<String, String> data){
        String value = data.get(ExcelCloumnName.CONNECT_INTERNET);
        if (stringIsEmpty(value)) {
            data.replace(ExcelCloumnName.CONNECT_INTERNET, "0");
        }
    }

    // 所属辖区公安机关
    public void dealPoliceCode(Map<String, String> data){
        String policeCode = data.get(ExcelCloumnName.POLICE_CODE);
        if (stringIsEmpty(policeCode)){
            String adminArea = data.get(ExcelCloumnName.ADMIN_AREA);
            if (Objects.equals(adminArea.length(), 6)){
                data.replace(ExcelCloumnName.POLICE_CODE, adminArea + "000000");
            } else if (Objects.equals(adminArea.length(), 8)) {
                data.replace(ExcelCloumnName.POLICE_CODE, adminArea + "0000");
            }
        }
    }

    // 安装时间
    public void dealInnerInstallTime(Map<String, String> data){
        String value = data.get(ExcelCloumnName.INSTALL_TIME);
        if (stringIsEmpty(value)) {
            data.replace(ExcelCloumnName.INSTALL_TIME, "2017-10-01 10:30:59");
        }
    }

    // 安装时间
    public void dealOutInstallTime(Map<String, String> data){
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
            if (Objects.isNull(value)){
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
            if (Objects.isNull(value)){
                return;
            }
            data.replace(ExcelCloumnName.PHONE, value.get(PoliceTableCloumnName.PHONE));
        }
    }

    // 保存天数
    public void dealSaveDay(Map<String, String> data){
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

    public static boolean stringIsEmpty(String value){
        return Objects.isNull(value) || value.trim().length() == 0;
    }

    public void dealLonLat(List<Map<String, String>> data){
        LonLatVilator vilator = new LonLatVilator();
        vilator.count(data);
        vilator.alarm();
    }
}
