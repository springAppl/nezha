package org.lee.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class BookConfig {
    Map<Integer, SheetConfig> configMap;
    private SheetConfig defaultConfig;

    public BookConfig(InputStream inputStream){
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(inputStream));

        String content = bufferedReader.lines().collect(Collectors.joining());

    }


    public BookConfig(List<SheetConfig> sheetConfigList) {
        this.configMap = sheetConfigList.stream()
                        .filter(sheetConfig -> Objects.nonNull(sheetConfig.getSheetIndex()))
                        .collect(Collectors.toMap(SheetConfig::getSheetIndex, sheetConfig -> sheetConfig));
        this.defaultConfig = sheetConfigList.stream()
                .filter(sheetConfig -> Objects.isNull(sheetConfig.getSheetIndex()))
                .findAny()
                .get();
    }

    public SheetConfig getIndex(int index){
        SheetConfig sheetConfig = configMap.get(index);
        return Objects.nonNull(sheetConfig) ? sheetConfig : defaultConfig;
    }
}
