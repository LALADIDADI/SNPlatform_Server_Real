package com.fan.boot.param;

import java.util.Map;

public class DCHEParam {

    // 算法运行所需参数,需计算
    int noSamples;
    int noCases;
    int noSNPs;
    int noControls;

    // 算法运行所需参数，不需计算
    int order;
    double[] alpha0 = new double[1];
    int[] sizeList = new int[1];

    // 算法位置相关参数
    String queryId;
    String inputDataName;
    String inputDataPath;
    String inputDataPath_i; // 没有文件名的文件路径
    String resDataPath_i;


    // 判断算法是否完成相关参数
    int filesCount = 0;
    int finishedCount = 0;
    // boolean finished = false;

    public DCHEParam() {
    }

    public static void main(String[] args) {
        double alpha = 1.5E-03;
        System.out.println(alpha);
    }

    public void setRemainParams(Map<String, String> params){
        this.order = Integer.parseInt(params.get("order"));
        String sAlpha0 = params.get("alpha0");
        String sSizeList = params.get("sizeList");
        String[] arrAlpha;
        String[] arrSizeList;
        // 将前端发来的字符串转为double数组

        // 去掉前后的空格
        sAlpha0 = sAlpha0.trim();
        sSizeList = sSizeList.trim();

        // 通过逗号进行分割
        arrAlpha = sAlpha0.split(",");
        arrSizeList = sSizeList.split(",");

        //转为double数组
        alpha0 = new double[arrAlpha.length];
        sizeList = new int[arrSizeList.length];

        for(int i =0; i < arrAlpha.length; i++){
            alpha0[i] = Double.parseDouble(arrAlpha[i].trim());
            sizeList[i] = Integer.parseInt(arrSizeList[i].trim());
        }

    }

    public int getNoSamples() {
        return noSamples;
    }

    public void setNoSamples(int noSamples) {
        this.noSamples = noSamples;
    }

    public int getNoCases() {
        return noCases;
    }

    public void setNoCases(int noCases) {
        this.noCases = noCases;
    }

    public int getNoSNPs() {
        return noSNPs;
    }

    public void setNoSNPs(int noSNPs) {
        this.noSNPs = noSNPs;
    }

    public int getNoControls() {
        return noControls;
    }

    public void setNoControls(int noControls) {
        this.noControls = noControls;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
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

    public double[] getAlpha0() {
        return alpha0;
    }

    public void setAlpha0(double[] alpha0) {
        this.alpha0 = alpha0;
    }

    public int[] getSizeList() {
        return sizeList;
    }

    public void setSizeList(int[] sizeList) {
        this.sizeList = sizeList;
    }

    public String getResDataPath_i() {
        return resDataPath_i;
    }

    public void setResDataPath_i(String resDataPath_i) {
        this.resDataPath_i = resDataPath_i;
    }

    public String getInputDataPath_i() {
        return inputDataPath_i;
    }

    public void setInputDataPath_i(String inputDataPath_i) {
        this.inputDataPath_i = inputDataPath_i;
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
