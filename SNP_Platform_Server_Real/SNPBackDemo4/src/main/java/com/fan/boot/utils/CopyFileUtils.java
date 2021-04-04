package com.fan.boot.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class CopyFileUtils {
    public static void main(String[] args) {
        //copyFile();
        DecimalFormat df=new DecimalFormat("0.00");
        double i = 0;
        i = 1/2;
        System.out.println(df.format((double)1/2));
    }

    public static void copyFile() {
        File src = new File("G:\\SNPalgorithm\\ClusterMI\\inputData\\data_1.txt");
        File dest;
        // 定义文件输入流和输出流对象
        int i = 2;
        FileInputStream fis = null;// 输入流
        FileOutputStream fos = null;// 输出流
        for( ; i <= 100; i++){
            String destPath = "C:\\Users\\Administrator\\Desktop\\毕业设计\\代码\\testInputFile\\testdata_"+ i +".txt";
            dest = new File(destPath);
            try {
                fis = new FileInputStream(src);
                fos = new FileOutputStream(dest);
                byte[] bs = new byte[1024];
                while (true) {
                    int len = fis.read(bs, 0, bs.length);
                    if (len == -1) {
                        break;
                    } else {
                        fos.write(bs, 0, len);
                    }
                }
                System.out.println("复制文件成功");
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    fis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }
}

