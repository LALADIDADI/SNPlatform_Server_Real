package com.fan.boot.utils;

import com.fan.boot.config.MyConst;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ReadFileUtils {

    public static void main(String[] args) {
        String filePath = CalParamsUtils.resShowPath("20210507151542");
        hiSeekerReadTxtFile(filePath, 10);
        String str = "a  b     cdef      g";
        String [] arr = str.split("\\s+");
        for(String ss : arr){
            System.out.println(ss);
        }
    }

    /**
     * 读取文件的静态方法,可指定读取的行数
     *
     * @param filePath
     *
     * @return void
     */
    public static Map[] hiSeekerReadTxtFile(String filePath, int rowCount) {
        int tem = rowCount;
        Map[] maps = new Map[rowCount];
        try {
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                // 写把标题头读出
                bufferedReader.readLine();
                while ((lineTxt = bufferedReader.readLine()) != null && rowCount >= 0) {
                    System.out.println(lineTxt);
                    // 将结果保存在maps中
                    Map<String, String> map = new HashMap<>();
                    String[] ss = lineTxt.split("\\s+");
                    map.put("SNP1", ss[0]);
                    map.put("SNP2", ss[1]);
                    map.put("SNP3", ss[2]);
                    map.put("x2", ss[3]);
                    map.put("p-value", ss[4]);
                    maps[tem-rowCount] = map;
                    rowCount--;
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return maps;
    }

    public static Map[] clusterMIReadTxtFile(String filePath, int rowCount) {
        int tem = rowCount;
        Map[] maps = new Map[rowCount];
        try {
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                // 写把标题头读出
                bufferedReader.readLine();
                while ((lineTxt = bufferedReader.readLine()) != null && rowCount >= 0) {
                    System.out.println(lineTxt);
                    // 将结果保存在maps中
                    Map<String, String> map = new HashMap<>();
                    String[] ss = lineTxt.split("\\s+");
                    map.put("SNP1", ss[0]);
                    map.put("SNP2", ss[1]);
                    map.put("SNP3", ss[2]);
                    map.put("chi2", ss[3]);
                    map.put("p-value", ss[4]);
                    maps[tem-rowCount] = map;
                    rowCount--;
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return maps;
    }

    public static Map[] dcheReadTxtFile(String filePath, int rowCount) {
        int tem = rowCount;
        Map[] maps = new Map[rowCount+1];
        try {
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                // 写把标题头读出
                bufferedReader.readLine();
                while ((lineTxt = bufferedReader.readLine()) != null && rowCount >= 0) {
                    System.out.println(lineTxt);
                    // 将结果保存在maps中
                    Map<String, String> map = new HashMap<>();
                    String[] ss = lineTxt.split("\\s+");
                    if(ss.length == 5) {
                        map.put("Index", ss[0]);
                        map.put("SNP1", ss[1]);
                        map.put("SNP2", ss[2]);
                        map.put("SNP3", ss[3]);
                        map.put("p-value", ss[4]);
                    }else if (ss.length == 4) {
                        map.put("Index", ss[0]);
                        map.put("SNP1", ss[1]);
                        map.put("SNP2", ss[2]);
                        map.put("p-value", ss[3]);
                    }
                    maps[tem-rowCount] = map;
                    rowCount--;
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return maps;
    }

    public static Map[] decmdrReadTxtFile(String filePath, int rowCount) {
        int tem = rowCount;
        Map[] maps = new Map[rowCount+1];
        try {
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                // 写把标题头读出
                bufferedReader.readLine();
                while ((lineTxt = bufferedReader.readLine()) != null && rowCount >= 0) {
                    System.out.println(lineTxt);
                    // 将结果保存在maps中
                    Map<String, String> map = new HashMap<>();
                    map.put("resLine", lineTxt);
                    maps[tem-rowCount] = map;
                    rowCount--;
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return maps;
    }

    public static Map[] dualWMDRReadTxtFile(String filePath, int rowCount) {
        int tem = rowCount;
        Map[] maps = new Map[rowCount+1];
        try {
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                // 写把标题头读出
                bufferedReader.readLine();
                while ((lineTxt = bufferedReader.readLine()) != null && rowCount >= 0) {
                    System.out.println(lineTxt);
                    // 将结果保存在maps中
                    Map<String, String> map = new HashMap<>();
                    String[] ss = lineTxt.split("\\s+");
                    map.put("SNP1", ss[0]);
                    map.put("SNP2", ss[1]);
                    map.put("SNP3", ss[2]);
                    map.put("chi2", ss[3]);
                    map.put("p-value", ss[4]);
                    maps[tem-rowCount] = map;
                    rowCount--;
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return maps;
    }

    public static Map[] macoedReadTxtFile(String filePath, int rowCount) {
        int tem = rowCount;
        Map[] maps = new Map[rowCount+1];
        try {
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;

                while ((lineTxt = bufferedReader.readLine()) != null && rowCount >= 0) {
                    System.out.println(lineTxt);
                    // 将结果保存在maps中
                    Map<String, String> map = new HashMap<>();
                    map.put("resLine", lineTxt);
                    maps[tem-rowCount] = map;
                    rowCount--;
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return maps;
    }

    public static Map[] momdrReadTxtFile(String filePath, int rowCount) {
        Map[] maps = new Map[rowCount+1];
        try {
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                // 写把标题头读出
                bufferedReader.readLine();
                while ((lineTxt = bufferedReader.readLine()) != null && rowCount >= 0) {
                    System.out.println(lineTxt);
                    // 将结果保存在maps中
                    Map<String, String> map = new HashMap<>();
                    String[] ss = lineTxt.split("\\s+");
                    map.put("SNP1", ss[0]);
                    map.put("SNP2", ss[1]);
                    map.put("SNP3", ss[2]);
                    map.put("chi2", ss[3]);
                    map.put("p-value", ss[4]);
                    maps[10-rowCount] = map;
                    rowCount--;
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return maps;
    }

    public static Map[] epiMCReadTxtFile(String filePath, int rowCount) {
        Map[] maps = new Map[rowCount+1];
        try {
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                // 写把标题头读出
                bufferedReader.readLine();
                while ((lineTxt = bufferedReader.readLine()) != null && rowCount >= 0) {
                    System.out.println(lineTxt);
                    // 将结果保存在maps中
                    Map<String, String> map = new HashMap<>();
                    String[] ss = lineTxt.split("\\s+");
                    map.put("SNP1", ss[0]);
                    map.put("SNP2", ss[1]);
                    map.put("SNP3", ss[2]);
                    map.put("chi2", ss[3]);
                    map.put("p-value", ss[4]);
                    maps[10-rowCount] = map;
                    rowCount--;
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return maps;
    }

}
