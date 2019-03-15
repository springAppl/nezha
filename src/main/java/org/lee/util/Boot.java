package org.lee.util;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Boot {
    public static void main(String[] args) throws IOException, DataInvalidException, DuplicateAdminAreaException {
        Boot boot = new Boot();
        ResultColl resultColl = boot.filterNewTable();

        List<Map<String, String>> data = boot.dealInvalidData(resultColl.getLeftData());
        resultColl.getResultData().addAll(data);
        ExcelWriter writer = new ExcelWriter();
        writer.writeResult("result", resultColl.getResultData());

        boot.calIncrement(resultColl.getResultData());
    }

    public List<Map<String, String>> dealInvalidData(List<Map<String, String>> data) throws IOException, DataInvalidException, DuplicateAdminAreaException {
        ResultColl resultColl = filterByResourceType(data);
        DataWasher dataWasher = new DataWasher();
        return dataWasher.fillData(resultColl.getResultData(), resultColl.getLeftData());
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

    public void calIncrement(List<Map<String, String>> result) throws IOException, DataInvalidException {
        // 读取上一次比对的结果
        LastResultReader lastResultReader = new LastResultReader();
        Optional<List<Map<String, String>>> increment = lastResultReader.compareTable(result);
        if (!increment.isPresent()){
            System.out.println("没有增量");
            return;
        }
        ExcelWriter writer = new ExcelWriter();
        writer.writeResult("increment", increment.get());
    }

    public ResultColl filterNewTable() throws IOException, DataInvalidException {
        List<Map<String, String>> matchData = new ArrayList<>();
        List<Map<String, String>> leftData = new ArrayList<>();

        BakReader bakReader = new BakReader();

        ResultColl cmpOldTableData = bakReader.compareOldTable();
        matchData.addAll(cmpOldTableData.getResultData());
        leftData.addAll(cmpOldTableData.getLeftData());

        bakReader.compareBakTable(cmpOldTableData.getLeftData())
        .ifPresent(data -> {
            matchData.addAll(data.getResultData());
            leftData.clear();
            leftData.addAll(data.getLeftData());
        });
        return new ResultColl(leftData, matchData);
    }
}