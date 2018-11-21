package org.lee.util;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Boot {
    public static void main(String[] args) throws IOException, DataInvalidException {
        Boot boot = new Boot();
        ResultColl resultColl = boot.filterNewTable();
        List<Map<String, String>> data = boot.dealInvalidData(resultColl.getLeftData());
        resultColl.getResultData().addAll(data);
        resultColl.getResultData().forEach(System.out::println);
        ExcelWriter writer = new ExcelWriter();
        writer.writeResult("result", resultColl.getResultData());
    }

    public List<Map<String, String>> dealInvalidData(List<Map<String, String>> data) throws IOException, DataInvalidException {
        ResultColl resultColl = filterByResourceType(data);
        return fillData(resultColl.getResultData(), resultColl.getLeftData());
    }

    public ResultColl filterByResourceType(List<Map<String, String>> data){
        List<Map<String, String>> innerData = data.stream()
                .filter(this::isInnerType)
                .collect(Collectors.toList());
        List<Map<String, String>> outData = data.stream()
                .filter(ele -> !isInnerType(ele))
                .collect(Collectors.toList());
        return new ResultColl(outData, innerData);
    }

    public List<Map<String, String>> fillData(List<Map<String, String>> innerData, List<Map<String, String>> outData) throws IOException, DataInvalidException {
        List<Map<String, String>> data = new ArrayList<>();
        List<Map<String, String>> polictTable = readPoliceTable();
        policeMap = polictTable.stream().collect(Collectors.toMap(ele -> ele.get(PoliceTableCloumnName.ADMIN_AREA).trim(), ele -> ele));

        data.addAll(
                innerData.stream().map(this::fillInnerData).collect(Collectors.toList())
        );
        data.addAll(
                outData.stream().map(this::fillOutData).collect(Collectors.toList())
        );
        return data;
    }
    public Map<String, String> fillInnerData(Map<String, String> innerData){
        dealDeviceBrand(innerData);
        dealInnerDataPointType(innerData);
        dealInstallLocation(innerData);
        dealInnerDataLocationType(innerData);
        dealConnectInternet(innerData);
        dealPoliceCode(innerData);
        dealInnerInstallTime(innerData);
        dealManageUnit(innerData);
        dealPhone(innerData);
        dealSaveDay(innerData);
        dealDeviceState(innerData);
        return innerData;
    }

    public Map<String, String> fillOutData(Map<String, String> outData){
        dealDeviceBrand(outData);
        dealOutDataPointType(outData);
        dealInstallLocation(outData);
        dealOutDataLocationType(outData);
        dealConnectInternet(outData);
        dealPoliceCode(outData);
        dealOutInstallTime(outData);
        dealManageUnit(outData);
        dealPhone(outData);
        dealSaveDay(outData);
        dealDeviceState(outData);
        return outData;
    }

    public static boolean stringIsEmpty(String value){
        return Objects.isNull(value) || value.trim().length() == 0;
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

    private Map<String, Map<String, String>> policeMap;

    // 安装地址
    public void dealInstallLocation(Map<String, String> data){
        String originAdminArea = data.get(ExcelCloumnName.INSTALL_LOCATION);
        if (stringIsEmpty(originAdminArea)) {
            String adminArea = data.get(ExcelCloumnName.ADMIN_AREA);
            try {
                Map<String, String> value = policeMap.get(adminArea.substring(0,6));
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
    public void dealManageUnit(Map<String, String> data) {
        String manageUnit = data.get(ExcelCloumnName.MANAGE_UNIT);
        if (stringIsEmpty(manageUnit)) {
            String adminArea = data.get(ExcelCloumnName.ADMIN_AREA);
            Map<String, String> value = policeMap.get(adminArea.substring(0,6));
            if (Objects.isNull(value)){
                return;
            }
            data.replace(ExcelCloumnName.MANAGE_UNIT, value.get(PoliceTableCloumnName.NAME));
        }
    }

    // 联系方式
    public void dealPhone(Map<String, String> data) {
        String phone = data.get(ExcelCloumnName.PHONE);
        if (stringIsEmpty(phone)) {
            String adminArea = data.get(ExcelCloumnName.ADMIN_AREA);
            Map<String, String> value = policeMap.get(adminArea.substring(0,6));
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


    public boolean isInnerType(Map<String, String>  data){
        String code = data.get(ExcelCloumnName.DEVICE_CODE);
        if (Objects.isNull(code)) {
            throw new RuntimeException("数据异常：" + data.toString());
        }
        if (code.length() < 14) {
            return false;
        }
        if (!Objects.equals("02", code.substring(8,10))){
            return false;
        }
        if (!Objects.equals('5', code.indexOf(13))){
            return false;
        }
        return true;
    }

    public ResultColl filterNewTable() throws IOException, DataInvalidException {
        List<Map<String, String>> matchData = new ArrayList<>();
        List<Map<String, String>> leftData = new ArrayList<>();

        ResultColl cmpOldTableData = compareOldTable();
        matchData.addAll(cmpOldTableData.getResultData());
        leftData.addAll(cmpOldTableData.getLeftData());

        compareBakTable(cmpOldTableData.getLeftData())
        .ifPresent(data -> {
            matchData.addAll(data.getResultData());
            leftData.clear();
            leftData.addAll(data.getLeftData());
        });
        return new ResultColl(leftData, matchData);
    }


    // 新表与旧表过滤
    public ResultColl compareOldTable() throws IOException, DataInvalidException {
        String newTable = readFileName("输入新表：");
        String oldTable = readFileName("输入旧表：");

        ExcelReader excelReader = new ExcelReader();
        return excelReader.compareNewAndOldTable(
                newTable,
                oldTable
        );
    }

    // 新表与备份表过滤
    public Optional<ResultColl> compareBakTable(List<Map<String, String>> leftData) throws IOException, DataInvalidException {
        Boolean hasBak = readBoolean("是否有备份表（yes/no）:");
        if (hasBak) {
            ExcelReader excelReader = new ExcelReader();
            String bakTable = readFileName("输入备份表：");
            return Optional.of(excelReader.compareNewAndBakTable(leftData, bakTable));
        }
        return Optional.empty();
    }

    public List<Map<String, String>> readPoliceTable() throws IOException, DataInvalidException {
        String newTable = readFileName("输入公安表：");
        ExcelReader excelReader = new ExcelReader();
        return excelReader.readPoliceTable(newTable);
    }


    public static String readFileName(String message){
        Scanner scan = new Scanner(System.in);
        String tableName;
        while (true) {
            System.out.println(message);
            if (scan.hasNext()) {
                tableName = scan.next();
                File file = new File(tableName);
                if (file.exists()) {
                    return tableName;
                }
            }
        }
    }

    public static Boolean readBoolean(String message){
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.println(message);
            String judgement = null;
            if(scan.hasNext()) {
                judgement = scan.next();
            }
            if (Objects.equals("yes", judgement)) {
                return true;
            }
            if (Objects.equals("no", judgement)) {
                return false;
            }
        }
    }
}
