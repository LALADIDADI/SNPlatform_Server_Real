package com.fan.boot.service;


import com.fan.boot.modules.decmdrModule.Method.Entrance;
import com.fan.boot.param.DECMDRParam;
import com.fan.boot.utils.ReadFileListUtils;

public class DECMDRImpl {

    public static void runDECMDR(DECMDRParam decmdrParam, String fileName) {

        System.out.println("DECMDR Start");

        // 基本属性
        int seed = decmdrParam.getSeed();
        int populationSize = decmdrParam.getPopulationSize();
        int maxGeneration = decmdrParam.getMaxGeneration();
        int order = decmdrParam.getOrder();
        double CRFactor = decmdrParam.getCRFactor();
        double mutationFactor = decmdrParam.getMutationFactor();
        // 位置属性
        String inputDataPath = decmdrParam.getInputDataPath_i() + fileName;
        String resDataPath = decmdrParam.getResDataPath_i() + "resData_" + fileName;

        // 开始运行算法
        try {
            new Entrance(inputDataPath, resDataPath, seed, populationSize, maxGeneration, mutationFactor, CRFactor, order);
        }catch (Exception e) {
            e.printStackTrace();
        }

        decmdrParam.setFinishedCount(decmdrParam.getFinishedCount() + 1);
        System.out.println("DECMDR Over");
    }

    public static void batchRun(DECMDRParam decmdrParam) {
        String inputDataPath = decmdrParam.getInputDataPath_i();
        String[] inputFiles = ReadFileListUtils.getFileName(inputDataPath);

        for(int i = 0; i < inputFiles.length; i++){
            runDECMDR(decmdrParam, inputFiles[i]);
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
