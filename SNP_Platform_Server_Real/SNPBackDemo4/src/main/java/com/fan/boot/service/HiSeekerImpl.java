package com.fan.boot.service;

import com.fan.boot.param.HiSeekerParam;

import java.io.File;
import java.io.IOException;

public class HiSeekerImpl {


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
    public static void runHiSeekerExe(HiSeekerParam HiParams) throws IOException {
        System.out.println("我RUN啦");

        // 获取exe文件在程序中的位置
        File directory = new File("");// 参数要为空
        String AbsolutePath = directory.getCanonicalPath();
        String exePath = "\\src\\main\\resources\\static\\HiSeekerExe\\HiSeekerRun.exe";
        AbsolutePath = AbsolutePath + exePath;
        System.out.println(AbsolutePath);

        String threshold = HiParams.getThreshold();
        String scaleFactor = HiParams.getScaleFactor();
        String rou = HiParams.getRou();
        String phe = HiParams.getPhe();
        String alpha = HiParams.getAlpha();
        String iAntCount = HiParams.getiAntCount();
        String iterCount = HiParams.getIterCount();
        String kLociSet = HiParams.getkLociSet();
        String kEpiModel = HiParams.getkEpiModel();
        String kTopModel = HiParams.getkTopModel();
        String topK = HiParams.getTopK();
        String typeOfSearch = HiParams.getTypeOfSearch();

        String inputDataPath = HiParams.getInputDataPath();
        String queryId = HiParams.getQueryId();

        String[] cmd = {AbsolutePath, threshold, scaleFactor, rou, phe, alpha, iAntCount, iterCount, kLociSet,
                        kEpiModel, kTopModel, topK, typeOfSearch, inputDataPath, queryId};
        openExe(cmd);
        System.out.println(inputDataPath);
        System.out.println("我结束啦");
    }



    public static void main(String[] args) throws IOException {

    }
}
