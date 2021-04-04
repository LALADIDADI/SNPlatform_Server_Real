package com.fan.boot.param;

import java.util.Map;

public class ClusterMIParam {

    //算法运行所必须的参数
    String typeOfSearch;
    String alpha;
    String sigThreshold;
    String topK;
    String rou;
    String level;
    String iAntCount;
    String iterCount;
    String kLociSet;
    String kEpiModel;
    String kTopModel;
    String kCluster;

    // 算法位置相关参数
    String queryId;
    String inputDataName;
    String inputDataPath;
    String inputDataPath_i; // inputFile不带文件名版本，即青春版

    public ClusterMIParam() {
    }

    public static void main(String[] args) {

    }
    // 传入所有算法相关参数
    public void setAllParams(Map<String, String> params){
        this.typeOfSearch = params.get("typeOfSearch");
        this.alpha = params.get("alpha");
        this.sigThreshold = params.get("sigThreshold");
        this.topK = params.get("topK");
        this.rou = params.get("rou");
        this.level = params.get("phe");
        this.iAntCount = params.get("iAntCount");
        this.iterCount = params.get("iterCount");
        this.kLociSet = params.get("kLociSet");
        this.kEpiModel = params.get("kEpiModel");
        this.kTopModel = params.get("kTopModel");
        this.kCluster = params.get("kCluster");
    }

    public String getTypeOfSearch() {
        return typeOfSearch;
    }

    public void setTypeOfSearch(String typeOfSearch) {
        this.typeOfSearch = typeOfSearch;
    }

    public String getAlpha() {
        return alpha;
    }

    public void setAlpha(String alpha) {
        this.alpha = alpha;
    }

    public String getSigThreshold() {
        return sigThreshold;
    }

    public void setSigThreshold(String sigThreshold) {
        this.sigThreshold = sigThreshold;
    }

    public String getTopK() {
        return topK;
    }

    public void setTopK(String topK) {
        this.topK = topK;
    }

    public String getRou() {
        return rou;
    }

    public void setRou(String rou) {
        this.rou = rou;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getiAntCount() {
        return iAntCount;
    }

    public void setiAntCount(String iAntCount) {
        this.iAntCount = iAntCount;
    }

    public String getIterCount() {
        return iterCount;
    }

    public void setIterCount(String iterCount) {
        this.iterCount = iterCount;
    }

    public String getkLociSet() {
        return kLociSet;
    }

    public void setkLociSet(String kLociSet) {
        this.kLociSet = kLociSet;
    }

    public String getkEpiModel() {
        return kEpiModel;
    }

    public void setkEpiModel(String kEpiModel) {
        this.kEpiModel = kEpiModel;
    }

    public String getkTopModel() {
        return kTopModel;
    }

    public void setkTopModel(String kTopModel) {
        this.kTopModel = kTopModel;
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

    public String getkCluster() {
        return kCluster;
    }

    public void setkCluster(String kCluster) {
        this.kCluster = kCluster;
    }

    public String getInputDataPath_i() {
        return inputDataPath_i;
    }

    public void setInputDataPath_i(String inputDataPath_i) {
        this.inputDataPath_i = inputDataPath_i;
    }
}
