package com.fan.boot.param;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class HiSeekerParam {

    // 运行算法必须的参数
    String threshold;
    String scaleFactor;
    String rou;
    String phe;
    String alpha;
    String iAntCount;
    String iterCount;
    String kLociSet;
    String kEpiModel;
    String kTopModel;
    String topK;
    String typeOfSearch;

    // 算法位置相关参数
    String queryId;
    String inputDataName;
    String inputDataPath;


    public HiSeekerParam() {
    }

    public HiSeekerParam(Map<String, String> params){
        this.threshold = params.get("threshold");
        this.scaleFactor = params.get("scaleFactor");
        this.rou = params.get("rou");
        this.phe = params.get("phe");
        this.alpha = params.get("alpha");
        this.iAntCount = params.get("iAntCount");
        this.iterCount = params.get("iterCount");
        this.kLociSet = params.get("kLociSet");
        this.kEpiModel = params.get("kEpiModel");
        this.kTopModel = params.get("kTopModel");
        this.topK = params.get("topK");
        this.typeOfSearch = params.get("typeOfSearch");
    }

    public static void main(String[] args) {

    }

    // 传入所有算法相关参数
    public void setAllParams(Map<String, String> params){
        this.threshold = params.get("threshold");
        this.scaleFactor = params.get("scaleFactor");
        this.rou = params.get("rou");
        this.phe = params.get("phe");
        this.alpha = params.get("alpha");
        this.iAntCount = params.get("iAntCount");
        this.iterCount = params.get("iterCount");
        this.kLociSet = params.get("kLociSet");
        this.kEpiModel = params.get("kEpiModel");
        this.kTopModel = params.get("kTopModel");
        this.topK = params.get("topK");
        this.typeOfSearch = params.get("typeOfSearch");
    }

    public String getScaleFactor() {
        return scaleFactor;
    }

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public void setScaleFactor(String scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public String getRou() {
        return rou;
    }

    public void setRou(String rou) {
        this.rou = rou;
    }

    public String getPhe() {
        return phe;
    }

    public void setPhe(String phe) {
        this.phe = phe;
    }

    public String getAlpha() {
        return alpha;
    }

    public void setAlpha(String alpha) {
        this.alpha = alpha;
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

    public String getTopK() {
        return topK;
    }

    public void setTopK(String topK) {
        this.topK = topK;
    }

    public String getTypeOfSearch() {
        return typeOfSearch;
    }

    public void setTypeOfSearch(String typeOfSearch) {
        this.typeOfSearch = typeOfSearch;
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
}
