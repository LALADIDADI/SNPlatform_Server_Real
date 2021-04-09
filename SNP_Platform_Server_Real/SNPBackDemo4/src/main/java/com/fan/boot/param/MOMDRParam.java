package com.fan.boot.param;

import java.util.Map;

public class MOMDRParam {
    // 基本参数
    int seed;
    int order;
    int foldCV;

    // 算法位置相关参数
    String queryId;
    String inputDataName;
    String inputDataPath;
    String inputDataPath_i;
    String resDataPath_i;

    // 判断算法是否完成相关参数
    int filesCount = 0;
    int finishedCount = 0;

    public void setBasicParams(Map<String, String> params) {
        this.seed = Integer.parseInt(params.get("seed"));
        this.order = Integer.parseInt(params.get("order"));
        this.foldCV = Integer.parseInt(params.get("foldCV"));
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getFoldCV() {
        return foldCV;
    }

    public void setFoldCV(int foldCV) {
        this.foldCV = foldCV;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getInputDataName() {
        return inputDataName;
    }

    public void setInputDataName(String inputDataName) {
        this.inputDataName = inputDataName;
    }

    public String getInputDataPath() {
        return inputDataPath;
    }

    public void setInputDataPath(String inputDataPath) {
        this.inputDataPath = inputDataPath;
    }

    public String getInputDataPath_i() {
        return inputDataPath_i;
    }

    public void setInputDataPath_i(String inputDataPath_i) {
        this.inputDataPath_i = inputDataPath_i;
    }

    public String getResDataPath_i() {
        return resDataPath_i;
    }

    public void setResDataPath_i(String resDataPath_i) {
        this.resDataPath_i = resDataPath_i;
    }

    public int getFilesCount() {
        return filesCount;
    }

    public void setFilesCount(int filesCount) {
        this.filesCount = filesCount;
    }

    public int getFinishedCount() {
        return finishedCount;
    }

    public void setFinishedCount(int finishedCount) {
        this.finishedCount = finishedCount;
    }
}
