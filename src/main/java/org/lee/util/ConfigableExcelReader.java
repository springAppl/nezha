package org.lee.util;


import com.alibaba.fastjson.JSON;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigableExcelReader extends AbstractExcelReader {

    private String file;

    private List<MetaCube> metaCubes;


    public ConfigableExcelReader(String file) {
        this.file = file;
        init();
    }

    private void init(){
        metaCubes = readConfig();
    }

    private List<MetaCube> readConfig(){
        StringBuilder config = new StringBuilder();
        byte[] content = new byte[1024];
        try (
                FileInputStream fin = new FileInputStream(file)
        ) {
            int sign = -1;
            while ((sign = fin.read(content)) != -1) {
                config.append(new String(content, 0, sign));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<MetaCube> metaCubes = JSON.parseArray(config.toString(), MetaCube.class);
        validate(metaCubes);
        return metaCubes;
    }

    private void validate(List<MetaCube> metaCubes){
        validateType(metaCubes);
        insertID(metaCubes);
        validateIndex(metaCubes);
        validateName(metaCubes);
    }

    private void validateType(List<MetaCube> metaCubes){
        Set<String> types = Arrays.stream(CellType.values())
                .map(Enum::name)
                .collect(Collectors.toSet());

        for (MetaCube metaCube : metaCubes) {
            if (!types.contains(metaCube.getType().toUpperCase())) {
                throw new DataInvalidException(String.format("%s文件%s类型错误", file,
                        metaCube.getName()));
            }
        }
    }

    private void validateName(List<MetaCube> metaCubes){
        Map<String, Long> map =
                metaCubes.stream().collect(Collectors.groupingBy(MetaCube::getName,
                Collectors.counting()));
        map.forEach((key, value) -> {
            if (value > 1) {
                throw new DataInvalidException(String.format("%s配置文件中name为%s重复", file,
                        key));
            }
        });
    }

    private void validateIndex(List<MetaCube> metaCubes){
        Map<Integer, Long> map =
                metaCubes.stream().collect(Collectors.groupingBy(MetaCube::getIndex,
                        Collectors.counting()));
        map.forEach((key, value) -> {
            if (value > 1) {
                throw new DataInvalidException(String.format("%s配置文件中index为%d重复", file,
                        key));
            }
        });
    }

    private void insertID(List<MetaCube> metaCubes){
        metaCubes.forEach(metaCube -> metaCube.setId(UUID.randomUUID().toString()));
    }



    @Override
    protected Map<String, String> read(Row row) throws DataInvalidException {
        return metaCubes.stream().collect(Collectors.toMap(MetaCube::getName,
                ele -> value(row, ele.getIndex())));
    }

    private String value(Row row, int index) throws DataInvalidException {
        Cell cell = row.getCell(index - 1);

        if (Objects.equals(cell.getCellType(), CellType.STRING)) {
            return cell.getStringCellValue();
        } else if (Objects.equals(cell.getCellType(), CellType.NUMERIC)) {
            return format(cell.getNumericCellValue());
        } else {
            throw new DataInvalidException(String.format("第%d列 ", index) + "格式错误");
        }
    }

    private String format(double value) {
        // 保留两位小数

        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return decimalFormat.format(value);
    }

}
