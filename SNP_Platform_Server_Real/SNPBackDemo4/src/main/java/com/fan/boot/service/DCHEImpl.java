package com.fan.boot.service;

import com.fan.boot.modules.dcheModule.mtif.Exhaustion;
import com.fan.boot.param.DCHEParam;
import com.fan.boot.utils.CalParamsUtils;
import com.fan.boot.utils.ReadFileListUtils;

import java.io.FileWriter;
import java.io.IOException;

public class DCHEImpl {

    // 传入DCHEParam对象，执行算法
    public static void runDCHE(DCHEParam dcheParam, String fileName){
        // String inputDataPath = dcheParam.getInputDataPath();
        String inputDataPath = dcheParam.getInputDataPath_i() + fileName;
        String resDataPath = dcheParam.getResDataPath_i()+"resData_"+fileName;
        // 这里需要对每个算法进行计算
        CalParamsUtils.calParams(inputDataPath);
        int nSample = CalParamsUtils.getNumCase() + CalParamsUtils.getNumControl();
        int nCases = CalParamsUtils.getNumCase();
        int nSNPs = CalParamsUtils.getNumSnp();

        int order = dcheParam.getOrder();
        double[] alpha0 = dcheParam.getAlpha0();
        int[] sizeList = dcheParam.getSizeList();

        // 准备工作完成，开始执行算法

        // 应该为读入数据阶段
        Exhaustion objE = new Exhaustion(nSample, nSNPs, nCases, alpha0, sizeList);
        System.out.println("Begin reading dataset.");
        objE.readData(inputDataPath);
        System.out.println("Finish reading dataset.");
        objE.flagPrint = true;

        try {
            FileWriter fwR = new FileWriter(resDataPath, false);
            fwR.write("Index\tLoci\t\t\tP_value\n");
            fwR.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 根据order，执行算法阶段
        if (order >= 2) {
            System.out.println("Start two loci interaction detection:");
            objE.twoSearch();
            System.out.println();
            System.out.println("Finish two loci interaction detection and writing into files.");
            objE.writeResults(resDataPath, 0);
        }

        if (order >= 3) {
            System.out.println("Start three loci interaction detection:");
            objE.threeSearch();
            System.out.println();
            System.out.println("Finish three loci interaction detection and writing into files.");
            objE.writeResults(resDataPath, 1);
        }

        if (order >= 4) {
            System.out.println("Start four loci interaction detection:");
            objE.fourSearch();
            System.out.println();
            System.out.println("Finish four loci interaction detection and writing into files.");
            objE.writeResults(resDataPath, 2);
        }

        dcheParam.setFinishedCount(dcheParam.getFinishedCount() + 1);
        System.out.println("DCHE Over");
    }

    public static void batchRun(DCHEParam dcheParam) {
        String inputDataPath = dcheParam.getInputDataPath_i();
        String[] inputFiles = ReadFileListUtils.getFileName(inputDataPath);

        for(int i = 0; i < inputFiles.length; i++){
            runDCHE(dcheParam, inputFiles[i]);
            System.out.println("inputFiles["+ i +"]:"+inputFiles[i]);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

    }
}
