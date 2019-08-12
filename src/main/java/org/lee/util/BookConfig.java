package org.lee.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class BookConfig {

    @JsonIgnore
    Map<Integer, SheetConfig> configMap;

    @JsonIgnore
    private SheetConfig defaultConfig;

    private List<SheetConfig> sheetConfigs;

    public BookConfig() {
    }

    public static BookConfig config(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(inputStream));

        String content = bufferedReader.lines().collect(Collectors.joining());
        ObjectMapper objectMapper = new ObjectMapper();
        BookConfig bookConfig = objectMapper.readValue(content, BookConfig.class);
        return new BookConfig(bookConfig.sheetConfigs);
    }


    public BookConfig(List<SheetConfig> sheetConfigList) {
        this.sheetConfigs = sheetConfigList;
        init();
    }

    private void init(){
        this.configMap = this.sheetConfigs.stream()
                .filter(sheetConfig -> Objects.nonNull(sheetConfig.getSheetIndex()))
                .collect(Collectors.toMap(SheetConfig::getSheetIndex, sheetConfig -> sheetConfig));
        if (sheetConfigs.size() == 1){
            this.defaultConfig = sheetConfigs.get(0);
        } else {
            this.defaultConfig = sheetConfigs.stream()
                    .filter(sheetConfig -> Objects.isNull(sheetConfig.getSheetIndex()))
                    .findAny()
                    .get();
        }

    }

    public SheetConfig getIndex(int index){
        if (Objects.isNull(configMap)){
            init();
        }
        SheetConfig sheetConfig = configMap.get(index);
        return Objects.nonNull(sheetConfig) ? sheetConfig : defaultConfig;
    }

    public List<SheetConfig> getSheetConfigs() {
        return sheetConfigs;
    }

    public void setSheetConfigs(List<SheetConfig> sheetConfigs) {
        this.sheetConfigs = sheetConfigs;
    }

    @Override
    public String toString() {
        return "BookConfig{" +
                "sheetConfigs=" + sheetConfigs +
                '}';
    }
}
