package org.lee.util;

import java.util.HashMap;
import java.util.Map;

public class ExcelCloumnName {
    public static final String DEVICE_CODE = "设备编码";

    public static final String DEVICE_NAME = "设备名称";

    public static final String DEVICE_BRAND = "摄像机品牌";

    public static final String ADMIN_AREA = "行政区域";

    public static final String POINT_TYPE = "监控点位类型";

    public static final String INSTALL_LOCATION = "安装地址";

    public static final String DEVICE_LONGITUDE = "设备经度";

    public static final String DEVICE_LATITUDE = "设备维度";

    public static final String LOCATION_TYPE = "摄像机位置类型";

    public static final String CONNECT_INTERNET = "联网属性";

    public static final String POLICE_CODE = "所属辖区公安机关";

    public static final String INSTALL_TIME = "安装时间";

    public static final String MANAGE_UNIT = "管理单位";

    public static final String PHONE = "管理单位联系方式";

    public static final String SAVE_DAY = "保存天数";

    public static final String DEVICE_STATE = "设备状态";

    public static final Map<Integer, String> EXCEL_CLOUMN = new HashMap<Integer, String>(){
        {
            put(2, DEVICE_BRAND);
            put(4, POINT_TYPE);
            put(5, INSTALL_LOCATION);
            put(6, DEVICE_LONGITUDE);
            put(7, DEVICE_LATITUDE);
            put(8, LOCATION_TYPE);
            put(9, CONNECT_INTERNET);
            put(10, POLICE_CODE);
            put(11, INSTALL_TIME);
            put(12, MANAGE_UNIT);
            put(13, PHONE);
            put(14, SAVE_DAY);
            put(15, DEVICE_STATE);
        }
    };
}
