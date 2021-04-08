package com.fan.boot.param;

import java.util.Map;

public class DualWMDRParam {

    // 基本属性
    String percent;
    String topT;
    String topK;
    String threshold;
    String fold;
    String constant;
    String alpha;
    int order;

    // 路径属性
    String queryId;
    String inputDataName;
    String inputDataPath;
    String inputDataPath_i;

    public void setBasicParams (Map<String, String> params) {
        this.percent = params.get("percent");
        this.topT = params.get("topT");
        this.topK = params.get("topK");
        this.threshold = params.get("threshold");
        this.fold = params.get("fold");
        this.constant = params.get("constant");
        this.alpha = params.get("alpha");
        this.order = Integer.parseInt(params.get("order"));
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
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

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public String getFold() {
        return fold;
    }

    public void setFold(String fold) {
        this.fold = fold;
    }

    public String getConstant() {
        return constant;
    }

    public void setConstant(String constant) {
        this.constant = constant;
    }

    public String getAlpha() {
        return alpha;
    }

    public void setAlpha(String alpha) {
        this.alpha = alpha;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
