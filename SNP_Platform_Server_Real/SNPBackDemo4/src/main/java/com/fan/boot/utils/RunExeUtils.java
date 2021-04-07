package com.fan.boot.utils;

public class RunExeUtils {
    // 打开并运行exe的方法

    /**
     * 打开并运行exe
     * @param AbsolutePath
     */
    public static void openExe(String[] AbsolutePath) {
        Runtime rn = Runtime.getRuntime();
        Process p = null;
        try {
            p = rn.exec(AbsolutePath);
        } catch (Exception e) {
            System.out.println("Error exec!");
        }
    }
}
