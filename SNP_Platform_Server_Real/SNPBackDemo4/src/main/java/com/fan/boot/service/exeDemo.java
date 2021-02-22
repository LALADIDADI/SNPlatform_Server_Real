package com.fan.boot.service;

public class exeDemo {
    public static void openExe(String[] AbsolutePath) {
        Runtime rn = Runtime.getRuntime();
        Process p = null;
        try {
            p = rn.exec(AbsolutePath);
        } catch (Exception e) {
            System.out.println("Error exec!");
        }
    }
    public static void main(String[] args) {
        System.out.println("我运行啦");
        String AbsolutePath = "G:\\SNPalgorithm\\HiSeeker\\VSTest\\HiSeekerRun\\Release\\HiSeekerRun.exe";
        String param1 = "2";
        String param2 = "2.7";
        String[] cmd = {AbsolutePath, param1, param2};
        openExe(cmd);
        System.out.println("我结束啦");
    }


}
