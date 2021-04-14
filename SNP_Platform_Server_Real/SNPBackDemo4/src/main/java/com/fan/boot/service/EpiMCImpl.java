package com.fan.boot.service;

import com.fan.boot.config.MyConst;
import com.fan.boot.param.EpiMCParam;
import com.fan.boot.utils.ReadFileListUtils;
import com.fan.boot.utils.RunExeUtils;

import java.io.File;
import java.io.IOException;

public class EpiMCImpl {

    public static void runEpiMC(EpiMCParam epiMCParam, String fileName) throws IOException {
        // 定位exe所在位置
        File directory = new File("");// 参数要为空
        String AbsolutePath = directory.getCanonicalPath();
        String exePath = "\\model\\MACOEDExe\\MACOED.exe"; // 打包后使用
        String exePath2 = "\\src\\main\\resources\\static\\EpiMCExe\\EpiMCEntrance.exe";
        AbsolutePath = AbsolutePath + exePath;
        System.out.println("AbsolutePath: " + AbsolutePath);

        // 基本属性
        String alternativeC = epiMCParam.getAlternativeC();
        String kFea = epiMCParam.getkFea();
        String lambda1 = epiMCParam.getLambda1();
        String lambda2 = epiMCParam.getLambda2();
        String topT = epiMCParam.getTopT();
        String topK = epiMCParam.getTopK();
        String order = epiMCParam.getOrder();

        // 路径属性
        String queryId = epiMCParam.getQueryId();
        String inputDataPath_i = epiMCParam.getInputDataPath_i();
        String dataName = fileName;

        // 参数路径属性
        String inputFilePath = inputDataPath_i + dataName;
        String resDataPath = MyConst.TEM_DATA_PATH + queryId + "\\resultData\\res_" + dataName;
        String haveFinished = MyConst.TEM_DATA_PATH + queryId + "\\haveFinished";
        String haveFinished2 = MyConst.TEM_DATA_PATH + queryId + "\\haveFinished\\" + dataName;
        // 参数属性
        String cmd[] = {AbsolutePath, alternativeC, kFea, lambda1, lambda2, topT, topK, order, inputFilePath, resDataPath, haveFinished, haveFinished2};
        RunExeUtils.openExe(cmd);
        System.out.println(alternativeC+" "+kFea+" "+lambda1+" "+lambda2+" "+topT+" "+topK+" "+order+" ");
        System.out.println("完整文件输入路径：" + inputFilePath);
        System.out.println("完整文件返回路径：" + resDataPath);
        System.out.println("完整文件夹路径：" + haveFinished);
        System.out.println("具体文件夹路径：" + haveFinished2);
        System.out.println("我结束啦");
    }

    public static void batchRun(EpiMCParam epiMCParam) throws InterruptedException, IOException {
        String inputDataPath = epiMCParam.getInputDataPath_i();
        String[] inputFiles = ReadFileListUtils.getFileName(inputDataPath);

        for(int i = 0; i < inputFiles.length; i++){
            runEpiMC(epiMCParam, inputFiles[i]);
            System.out.println("inputFiles["+ i +"]:"+inputFiles[i]);
            Thread.sleep(200);
        }
    }

    public static void main(String[] args) throws IOException {
        File directory = new File("");// 参数要为空
        String inputPath = "G:\\SNPalgorithm\\EpiMC\\data.txt";
        String resPath = "G:\\SNPalgorithm\\EpiMC\\result.txt";
        String haveFinished = MyConst.TEM_DATA_PATH + "111100" + "\\haveFinished";
        String haveFinished2 = MyConst.TEM_DATA_PATH + "111100" + "\\haveFinished\\" + "resData.txt";
        String AbsolutePath = directory.getCanonicalPath();
        String exePath = "\\src\\main\\resources\\static\\EpiMCExe\\EpiMCEntrance.exe";
        AbsolutePath = AbsolutePath + exePath;
        String[] testCmd = {AbsolutePath, "4", "3", "10", "10", "10", "200", "2", inputPath, resPath, haveFinished, haveFinished2};
        RunExeUtils.openExe(testCmd);
    }

}
