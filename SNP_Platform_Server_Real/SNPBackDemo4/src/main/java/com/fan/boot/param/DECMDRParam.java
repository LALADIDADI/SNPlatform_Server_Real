package com.fan.boot.param;

import java.util.Map;

public class DECMDRParam {
    // 算法运行所需参数，不需计算
    int seed;
    int populationSize;
    int maxGeneration;
    int order;
    double mutationFactor;
    double CRFactor;

    // 算法位置相关参数
    String queryId;
    String inputDataName;
    String inputDataPath;
    String inputDataPath_i; // 没有文件名的文件路径
    String resDataPath_i;


    // 判断算法是否完成相关参数
    int filesCount = 0;
    int finishedCount = 0;

    public void setBasicParams(Map<String, String> params) {
        this.seed = Integer.parseInt(params.get("seed"));
        this.populationSize = Integer.parseInt(params.get("populationSize"));
        this.maxGeneration = Integer.parseInt(params.get("maxGeneration"));
        this.order = Integer.parseInt(params.get("order"));
        this.mutationFactor = Double.parseDouble(params.get("mutationFactor"));
        this.CRFactor = Double.parseDouble(params.get("CRFactor"));
    }


    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public int getMaxGeneration() {
        return maxGeneration;
    }

    public void setMaxGeneration(int maxGeneration) {
        this.maxGeneration = maxGeneration;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public double getMutationFactor() {
        return mutationFactor;
    }

    public void setMutationFactor(double mutationFactor) {
        this.mutationFactor = mutationFactor;
    }

    public double getCRFactor() {
        return CRFactor;
    }

    public void setCRFactor(double CRFactor) {
        this.CRFactor = CRFactor;
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
