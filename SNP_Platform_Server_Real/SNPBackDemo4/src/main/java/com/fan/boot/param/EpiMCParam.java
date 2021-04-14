package com.fan.boot.param;

import java.util.Map;

public class EpiMCParam {

    // 基本属性
    String alternativeC;
    String kFea;
    String lambda1;
    String lambda2;
    String topT;
    String topK;
    String order;

    // 路径属性
    String queryId;
    String inputDataName;
    String inputDataPath;
    String inputDataPath_i; // inputFile不带文件名版本，即青春版

    public void setBasicParams(Map<String, String> params) {
        this.alternativeC = params.get("alternativeC");
        this.kFea = params.get("kFea");
        this.lambda1 = params.get("lambda1");
        this.lambda2 = params.get("lambda2");
        this.topT = params.get("topT");
        this.topK = params.get("topK");
        this.order = params.get("order");
    }

    public String getAlternativeC() {
        return alternativeC;
    }

    public void setAlternativeC(String alternativeC) {
        this.alternativeC = alternativeC;
    }

    public String getkFea() {
        return kFea;
    }

    public void setkFea(String kFea) {
        this.kFea = kFea;
    }

    public String getLambda1() {
        return lambda1;
    }

    public void setLambda1(String lambda1) {
        this.lambda1 = lambda1;
    }

    public String getLambda2() {
        return lambda2;
    }

    public void setLambda2(String lambda2) {
        this.lambda2 = lambda2;
    }

    public String getTopT() {
        return topT;
    }

    public void setTopT(String topT) {
        this.topT = topT;
    }

    public String getTopK() {
        return topK;
    }

    public void setTopK(String topK) {
        this.topK = topK;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
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
