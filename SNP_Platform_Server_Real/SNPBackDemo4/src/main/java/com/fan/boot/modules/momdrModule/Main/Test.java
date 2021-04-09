package com.fan.boot.modules.momdrModule.Main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class Test {
    public static void main(String[] args) throws FileNotFoundException {
        String resDataPath = "C:\\Users\\Administrator\\Desktop\\毕业设计\\代码\\MOMDR\\resData.txt";
        PrintWriter out= new PrintWriter(new FileOutputStream(resDataPath));

        out.println("ssssssssssssssss");
        out.println("ssssssssssssssss");
        out.println("ssssssssssssssss");
        out.println("ssssssssssssssss");
        out.flush();
        out.close();
    }
}
