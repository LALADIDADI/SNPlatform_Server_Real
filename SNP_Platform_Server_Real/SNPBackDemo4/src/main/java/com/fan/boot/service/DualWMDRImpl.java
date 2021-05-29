package com.fan.boot.service;

import com.fan.boot.config.MyConst;
import com.fan.boot.param.DualWMDRParam;
import com.fan.boot.utils.ReadFileListUtils;
import com.fan.boot.utils.RunExeUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DualWMDRImpl {

    // 控制进程销毁的容器全局变量
    static ArrayList<Process> proList = new ArrayList<Process>();

    // 销毁全部进程的方法
    public static void destroyOb() {
        for(int i = 0; i < proList.size(); i++) {
            Process p = proList.get(i);
            p.destroy();
        }
    }

    public static void runDualWMDR(DualWMDRParam dualParam, String fileName) throws IOException {

        // 定位exe所在位置
        File directory = new File("");// 参数要为空
        String AbsolutePath = directory.getCanonicalPath();
        String exePath = "\\model\\DualWMDRExe\\DualWMDR.exe";
        String exePath_3order = "\\model\\DualWMDRExe\\DualWMDR_3order.exe";
        String exePath2 = "\\src\\main\\resources\\static\\DualWMDRExe\\DualWMDR.exe";
        String exePath_3order_2 = "\\src\\main\\resources\\static\\DualWMDRExe\\DualWMDR_3order.exe";
        int order = dualParam.getOrder();
        if(order == 3){
            AbsolutePath = AbsolutePath + exePath_3order_2;
        }else {
            AbsolutePath = AbsolutePath + exePath2;
        }
        System.out.println("AbsolutePath: " + AbsolutePath);

        // 基本属性
        String percent = dualParam.getPercent();
        String topT = dualParam.getTopT();
        String topK = dualParam.getTopK();
        String threshold = dualParam.getThreshold();
        String fold = dualParam.getFold();
        String constant = dualParam.getConstant();
        String alpha = dualParam.getAlpha();

        // 路径属性
        String queryId = dualParam.getQueryId();
        String inputDataPath_i = dualParam.getInputDataPath_i();
        String dataName = fileName;

        // 参数路径属性
        String inputFilePath = inputDataPath_i + dataName;
        String resDataPath = MyConst.TEM_DATA_PATH + queryId + "\\resultData\\res_" + dataName;
        String haveFinished = MyConst.TEM_DATA_PATH + queryId + "\\haveFinished";
        String haveFinished2 = MyConst.TEM_DATA_PATH + queryId + "\\haveFinished\\" + dataName;

        // 参数属性
        String cmd[] = {AbsolutePath, percent, topT, topK, threshold, fold, constant, alpha, inputFilePath, resDataPath, haveFinished, haveFinished2};
        Process p = RunExeUtils.openExe(cmd);
        proList.add(p);
        System.out.println(percent+" "+topT+" "+topK+" "+threshold+" "+fold+" "+constant+" "+alpha+" ");
        System.out.println("完整文件输入路径：" + inputFilePath);
        System.out.println("完整文件返回路径：" + resDataPath);
        System.out.println("完整文件夹路径：" + haveFinished);
        System.out.println("具体文件夹路径：" + haveFinished2);
        System.out.println("我结束啦");
    }
    public static void batchRun(DualWMDRParam dualParam) throws InterruptedException, IOException {
        String inputDataPath = dualParam.getInputDataPath_i();
        String[] inputFiles = ReadFileListUtils.getFileName(inputDataPath);

        for(int i = 0; i < inputFiles.length; i++){
            runDualWMDR(dualParam, inputFiles[i]);
            System.out.println("inputFiles["+ i +"]:"+inputFiles[i]);
            Thread.sleep(200);
        }
    }

    public static void main(String[] args) throws IOException {
        File directory = new File("");// 参数要为空
        String inputPath = "G:\\SNPalgorithm\\DualWMDR\\testInputData\\test_data_2loci.txt";
        String resPath = "G:\\SNPalgorithm\\DualWMDR\\testResData\\resData.txt";
        String haveFinished = MyConst.TEM_DATA_PATH + "111100" + "\\haveFinished";
        String haveFinished2 = MyConst.TEM_DATA_PATH + "111100" + "\\haveFinished\\" + "resData.txt";
        String AbsolutePath = directory.getCanonicalPath();
        String exePath = "\\src\\main\\resources\\static\\DualWMDRExe\\DualWMDR.exe";
        AbsolutePath = AbsolutePath + exePath;
        String[] testCmd = {AbsolutePath, "1.0", "200", "100", "1","5", "0.5", "0.25", inputPath, resPath, haveFinished, haveFinished2};
        RunExeUtils.openExe(testCmd);
    }
}
