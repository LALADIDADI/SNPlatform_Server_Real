package com.fan.boot.service;

import com.fan.boot.param.ClusterMIParam;
import com.fan.boot.param.HiSeekerParam;

import java.io.File;
import java.io.IOException;

public class ClusterMIImpl {


    // 打开并运行exe的方法
    public static void openExe(String[] AbsolutePath) {
        Runtime rn = Runtime.getRuntime();
        Process p = null;
        try {
            p = rn.exec(AbsolutePath);
        } catch (Exception e) {
            System.out.println("Error exec!");
        }
    }

    // 调用exe并传入参数，包括输入文件在哪里
    public static void runClusterMIExe(ClusterMIParam CmiParams) throws IOException {
        System.out.println("我RUN啦");

        // 获取exe文件在程序中的位置
        File directory = new File("");// 参数要为空
        String AbsolutePath = directory.getCanonicalPath();
        String exePath = "\\src\\main\\resources\\static\\ClusterMIExe\\ClusterMIRun.exe";
        AbsolutePath = AbsolutePath + exePath;
        System.out.println(AbsolutePath);

        String typeOfSearch = CmiParams.getTypeOfSearch();
        String alpha = CmiParams.getAlpha();
        String sigThreshold = CmiParams.getSigThreshold();
        String topK = CmiParams.getTopK();
        String rou = CmiParams.getRou();
        String level = CmiParams.getLevel();
        String iAntCount = CmiParams.getiAntCount();
        String iterCount = CmiParams.getIterCount();
        String kLociSet = CmiParams.getkLociSet();
        String kEpiModel = CmiParams.getkEpiModel();
        String kTopModel = CmiParams.getkTopModel();
        String kCluster = CmiParams.getkCluster();

        String inputDataPath = CmiParams.getInputDataPath();
        String queryId = CmiParams.getQueryId();

        String[] cmd = {AbsolutePath, typeOfSearch, alpha, sigThreshold,topK, rou, level, iAntCount, iterCount, kLociSet, kEpiModel, kTopModel, kCluster, inputDataPath, queryId};
        openExe(cmd);
        System.out.println(typeOfSearch+" "+alpha+" "+sigThreshold+" "+topK+" "+rou+" "+level+" "+iAntCount+" "+iterCount+" "+kLociSet+" "+kEpiModel+" "+kTopModel+" "+kCluster+" "+inputDataPath+" "+queryId+" ");
        System.out.println(inputDataPath);
        System.out.println("我结束啦");
    }



    public static void main(String[] args) throws IOException {
        File directory = new File("");// 参数要为空
        String AbsolutePath = directory.getCanonicalPath();
        String exePath = "\\src\\main\\resources\\static\\ClusterMIExe\\ClusterMIRun.exe";
        AbsolutePath = AbsolutePath + exePath;
        String[] testCmd = {AbsolutePath, "0", "1", "0.05", "100", "0.01", "500", "1000", "100", "2", "3", "1000", "3", "G:\\SNPalgorithm\\ClusterMI\\inputData\\data.txt", "110000"};
        openExe(testCmd);
    }
}
