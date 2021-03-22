package com.fan.boot.service;

import com.fan.boot.modules.mtif.Exhaustion;
import com.fan.boot.param.DCHEParam;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DCHEImpl {

    // 传入DCHEParam对象，执行算法
    public static void runDCHE(DCHEParam dcheParam){
        String inputDataPath = dcheParam.getInputDataPath();
        String resDataPath = dcheParam.getResDataPath();
        int nSample = dcheParam.getNoSamples();
        int nCases = dcheParam.getNoCases();
        int nSNPs = dcheParam.getNoSNPs();
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

        dcheParam.setFinished(true);
        System.out.println("DCHE Over");
    }
}
