package com.fan.boot.service;

import com.fan.boot.param.HiSeekerParam;
import com.fan.boot.utils.ReadFileListUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class HiSeekerImpl {

    // 控制进程销毁的容器全局变量
    static ArrayList<Process> proList = new ArrayList<Process>();

    // 销毁全部进程的方法
    public static void destroyOb() {
        for(int i = 0; i < proList.size(); i++) {
            Process p = proList.get(i);
            p.destroy();
        }
    }

    // 打开并运行exe的方法
    public static void openExe(String[] AbsolutePath) {
        Runtime rn = Runtime.getRuntime();
        Process p = null;
        try {
            p = rn.exec(AbsolutePath);
            proList.add(p);
        } catch (Exception e) {
            System.out.println("Error exec!");
        }
    }

    // 调用exe并传入参数，包括输入文件在哪里
    public static void runHiSeekerExe(HiSeekerParam HiParams, String fileName) throws IOException {
        System.out.println("我RUN啦");

        // 获取exe文件在程序中的位置
        File directory = new File("");// 参数要为空
        String AbsolutePath = directory.getCanonicalPath();
        String exePath = "\\model\\HiSeekerExe\\HiSeekerRun.exe";
        String exePath2 = "\\src\\main\\resources\\static\\HiSeekerExe\\HiSeekerRun.exe";
        AbsolutePath = AbsolutePath + exePath2;
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

        String inputDataPath = HiParams.getInputDataPath_i()+fileName;
        String queryId = HiParams.getQueryId();

        String dataName = fileName;

        String[] cmd = {AbsolutePath, threshold, scaleFactor, rou, phe, alpha, iAntCount, iterCount, kLociSet,
                        kEpiModel, kTopModel, topK, typeOfSearch, inputDataPath, queryId, dataName};
        openExe(cmd);
        System.out.println("完整文件输入路径为：" + inputDataPath);
        System.out.println("我结束啦");
    }

    // 实现批量调用exe，并提供一个接口，包含文件路径，但不包含文件名
    public static void batchRun(HiSeekerParam HiParams) throws IOException, InterruptedException {
        String inputDataPath = HiParams.getInputDataPath_i();
        String[] inputFiles = ReadFileListUtils.getFileName(inputDataPath);

        for(int i = 0; i < inputFiles.length; i++){
            runHiSeekerExe(HiParams, inputFiles[i]);
            System.out.println("inputFiles["+ i +"]:"+inputFiles[i]);
            Thread.sleep(1000);
        }
    }



    public static void main(String[] args) throws IOException {
        File directory = new File("");// 参数要为空
        String AbsolutePath = directory.getCanonicalPath();
        String exePath = "\\src\\main\\resources\\static\\HiSeekerExe\\HiSeekerRun.exe";
        AbsolutePath = AbsolutePath + exePath;
        String[] testCmd = {AbsolutePath, "0.05", "10000", "0.05", "100", "1", "500", "200", "2", "3", "800", "100", "0", "C:\\Users\\Administrator\\Desktop\\毕业设计\\代码\\testInputFile\\testdata_1.txt", "110000", "demo.txt"};
        openExe(testCmd);
    }
}
