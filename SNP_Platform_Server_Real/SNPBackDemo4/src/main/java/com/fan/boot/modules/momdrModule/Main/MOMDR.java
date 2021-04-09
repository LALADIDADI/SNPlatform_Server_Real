package com.fan.boot.modules.momdrModule.Main;

import com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction.MultifactorDimensionalityReduction;

public class MOMDR {


    public MOMDR(int seed, int order, int cv, String inputDataPath, String resDataPath) {
        try{
            new MultifactorDimensionalityReduction(order, cv, seed, inputDataPath, resDataPath).implement();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        int seed = 1;
        int order = 2;
        int foldCV = 5;
        String inputDataPath = "C:\\Users\\Administrator\\Desktop\\毕业设计\\代码\\MOMDRInputFile\\data_1.txt";
        String resDataPath = "C:\\Users\\Administrator\\Desktop\\毕业设计\\代码\\MOMDR\\resData.txt";
        new MOMDR(seed, order, foldCV, inputDataPath, resDataPath);
    }
}

