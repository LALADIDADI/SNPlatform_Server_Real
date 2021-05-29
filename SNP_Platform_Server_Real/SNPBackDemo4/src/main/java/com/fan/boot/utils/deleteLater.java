package com.fan.boot.utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class deleteLater {

    public static double calFx1(double x1, double x4, double x) {
        double res = 0;
        if(x>=x4) {
            res = 1.0;
        }else if (x <= x1) {
            res = 0.1;
        } else {
            res = 0.9*(x-x1)/(x4-x1) + 0.1;
        }
        return res;
    }

    public static double calFx2(double x1, double x2, double x3, double x4, double x) {
        double res = 0;
        if(x<=x1 || x>=x4) {
            res = 0.1;
        }else if (x>x1 && x<x2) {
            res = 0.9 * (x - x1) / (x2 - x1) + 0.1;
        }else if(x>=x2 && x<=x3)
        {
            res = 1.0;
        }else if(x>x3 && x<x4){
            res = 1.0 - 0.9*(x-x3)/(x4-x3);
        }
        return res;
    }

    public static void main(String[] args) throws IOException {

        double x1 = 10;
        double x2 = 15;
        double x3 = 25;
        double x4 = 35;

        String resPath = "C:\\Users\\Administrator\\Desktop\\PH\\碱解氮_res.txt";
        BufferedWriter bw = new BufferedWriter(new FileWriter(resPath));

        String filePath = "C:\\Users\\Administrator\\Desktop\\PH\\碱解氮.txt";
        File file = new File(filePath);
        String encoding = "GBK";
        if (file.isFile() && file.exists()) { //判断文件是否存在
            InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file), encoding);//考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                double x = Double.parseDouble(lineTxt.trim());
                double res = calFx2(x1, x2, x3, x4, x);
                System.out.println("输入x："+x+"，输出："+res);
                bw.write(""+res);
                bw.newLine(); //换行用
            }
            read.close();
        } else {
            System.out.println("找不到指定的文件");
        }
        bw.flush();
        bw.close();


    }
}
