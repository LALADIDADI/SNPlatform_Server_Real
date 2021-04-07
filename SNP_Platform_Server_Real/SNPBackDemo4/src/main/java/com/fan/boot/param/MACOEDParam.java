package com.fan.boot.param;

import java.util.Map;

public class MACOEDParam {

    // 基本属性
    String maxIter;
    String numAnt;
    String dimEpi;
    String alpha;
    String lambda;
    String threshold;
    String tau;
    String rou;

    // 路径属性
    String queryId;
    String inputDataName;
    String inputDataPath;
    String inputDataPath_i; // inputFile不带文件名版本，即青春版

    public void setBasicParams(Map<String, String> params) {
        this.maxIter = params.get("maxIter");
        this.numAnt = params.get("numAnt");
        this.dimEpi = params.get("dimEpi");
        this.alpha = params.get("alpha");
        this.lambda = params.get("lambda");
        this.threshold = params.get("threshold");
        this.tau = params.get("tau");
        this.rou = params.get("rou");
    }

    public String getMaxIter() {
        return maxIter;
    }

    public void setMaxIter(String maxIter) {
        this.maxIter = maxIter;
    }

    public String getNumAnt() {
        return numAnt;
    }

    public void setNumAnt(String numAnt) {
        this.numAnt = numAnt;
    }

    public String getDimEpi() {
        return dimEpi;
    }

    public void setDimEpi(String dimEpi) {
        this.dimEpi = dimEpi;
    }

    public String getAlpha() {
        return alpha;
    }

    public void setAlpha(String alpha) {
        this.alpha = alpha;
    }

    public String getLambda() {
        return lambda;
    }

    public void setLambda(String lambda) {
        this.lambda = lambda;
    }

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public String getTau() {
        return tau;
    }

    public void setTau(String tau) {
        this.tau = tau;
    }

    public String getRou() {
        return rou;
    }

    public void setRou(String rou) {
        this.rou = rou;
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
}
