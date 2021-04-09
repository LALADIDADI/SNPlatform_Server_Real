package com.fan.boot.service;

import com.fan.boot.modules.momdrModule.Main.MOMDR;
import com.fan.boot.param.MOMDRParam;
import com.fan.boot.utils.ReadFileListUtils;

public class MOMDRImpl {

    public static void runMOMDR(MOMDRParam momdrParam, String fileName) {
        System.out.println("MOMDR Start");

        // 基本属性
        int seed = momdrParam.getSeed();
        int order = momdrParam.getOrder();
        int foldCV = momdrParam.getFoldCV();

        // 位置属性
        String inputDataPath = momdrParam.getInputDataPath_i() + fileName;
        String resDataPath = momdrParam.getResDataPath_i() + "resData_" + fileName;

        // 开始运行算法
        try {
            new MOMDR(seed, order, foldCV, inputDataPath, resDataPath);
        }catch (Exception e) {
            e.printStackTrace();
        }

        momdrParam.setFinishedCount(momdrParam.getFinishedCount() + 1);
        System.out.println("MOMDR Over");
    }
    public static void batchRun(MOMDRParam momdrParam) {
        String inputDataPath = momdrParam.getInputDataPath_i();
        String[] inputFiles = ReadFileListUtils.getFileName(inputDataPath);

        for(int i = 0; i < inputFiles.length; i++){
            runMOMDR(momdrParam, inputFiles[i]);
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
