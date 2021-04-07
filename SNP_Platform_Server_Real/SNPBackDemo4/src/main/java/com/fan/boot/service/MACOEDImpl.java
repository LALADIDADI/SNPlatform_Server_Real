package com.fan.boot.service;

import com.fan.boot.config.MyConst;
import com.fan.boot.param.MACOEDParam;
import com.fan.boot.utils.ReadFileListUtils;
import com.fan.boot.utils.RunExeUtils;

import java.io.File;
import java.io.IOException;

public class MACOEDImpl {
    public static void runMACOED(MACOEDParam macoedParam, String fileName) throws IOException {
        // 定位exe所在位置
        File directory = new File("");// 参数要为空
        String AbsolutePath = directory.getCanonicalPath();
        String exePath = "\\src\\main\\resources\\static\\MACOEDExe\\MACOED.exe";
        AbsolutePath = AbsolutePath + exePath;
        System.out.println("AbsolutePath: " + AbsolutePath);

        // 基本属性
        String maxIter = macoedParam.getMaxIter();
        String numAnt = macoedParam.getNumAnt();
        String dimEpi = macoedParam.getDimEpi();
        String alpha = macoedParam.getAlpha();
        String lambda = macoedParam.getLambda();
        String threshold = macoedParam.getThreshold();
        String tau = macoedParam.getTau();
        String rou = macoedParam.getRou();

        // 路径属性
        String queryId = macoedParam.getQueryId();
        String inputDataPath_i = macoedParam.getInputDataPath_i();
        String dataName = fileName;

        // 参数路径属性
        String inputFilePath = inputDataPath_i + dataName;
        String resDataPath = MyConst.TEM_DATA_PATH + queryId + "\\resultData\\res_" + dataName;
        String haveFinished = MyConst.TEM_DATA_PATH + queryId + "\\haveFinished";
        String haveFinished2 = MyConst.TEM_DATA_PATH + queryId + "\\haveFinished\\" + dataName;
        // 参数属性
        String cmd[] = {AbsolutePath, maxIter, numAnt, dimEpi, alpha, lambda, threshold, tau, rou, inputFilePath, resDataPath, haveFinished, haveFinished2};
        RunExeUtils.openExe(cmd);
        System.out.println(maxIter+" "+numAnt+" "+dimEpi+" "+alpha+" "+lambda+" "+threshold+" "+tau+" "+rou+" ");
        System.out.println("完整文件输入路径：" + inputFilePath);
        System.out.println("完整文件返回路径：" + resDataPath);
        System.out.println("完整文件夹路径：" + haveFinished);
        System.out.println("具体文件夹路径：" + haveFinished2);
        System.out.println("我结束啦");
    }

    public static void batchRun(MACOEDParam macoedParam) throws IOException, InterruptedException {
        String inputDataPath = macoedParam.getInputDataPath_i();
        String[] inputFiles = ReadFileListUtils.getFileName(inputDataPath);

        for(int i = 0; i < inputFiles.length; i++){
            runMACOED(macoedParam, inputFiles[i]);
            System.out.println("inputFiles["+ i +"]:"+inputFiles[i]);
            Thread.sleep(200);
        }
    }

    public static void main(String[] args) throws IOException {
        File directory = new File("");// 参数要为空
        String inputPath = "G:\\SNPalgorithm\\MACOED\\testInputData\\test.txt";
        String resPath = "G:\\SNPalgorithm\\MACOED\\testResData\\resData.txt";
        String haveFinished = MyConst.TEM_DATA_PATH + "111100" + "\\haveFinished";
        String haveFinished2 = MyConst.TEM_DATA_PATH + "111100" + "\\haveFinished\\" + "resData.txt";
        String AbsolutePath = directory.getCanonicalPath();
        String exePath = "\\src\\main\\resources\\static\\MACOEDExe\\MACOED.exe";
        AbsolutePath = AbsolutePath + exePath;
        String[] testCmd = {AbsolutePath, "50", "100", "2", "0.1", "2", "0.8", "1", "0.9", inputPath, resPath, haveFinished, haveFinished2};
        RunExeUtils.openExe(testCmd);
    }
}
