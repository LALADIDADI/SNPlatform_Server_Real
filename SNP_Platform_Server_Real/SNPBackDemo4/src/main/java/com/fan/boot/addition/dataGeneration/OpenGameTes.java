package com.fan.boot.addition.dataGeneration;

import com.fan.boot.utils.RunExeUtils;

import java.io.File;
import java.io.IOException;

/**
 * 前端发起请求后，调起GameTes的类
 */
public class OpenGameTes {
    public static void runGameTes () {
        // 获取exe文件在程序中的位置
        File directory = new File("");// 参数要为空
        try {
            String AbsolutePath = directory.getCanonicalPath();
            String exePath = "\\model\\GameTesExe\\run.bat";
            String exePath2 = "\\src\\main\\resources\\static\\GameTesExe\\run.bat";
            AbsolutePath = AbsolutePath + exePath2;
            Runtime.getRuntime().exec("cmd.exe   /C   start   " + AbsolutePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        runGameTes();
    }
}
