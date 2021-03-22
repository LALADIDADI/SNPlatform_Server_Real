package com.fan.boot.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {

    /**
     * 按照当前时间生成请求号的方法
     *
     * @return queryId, 请求号
     */
    public static String createQueryId() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String currentDate = df.format(new Date());
        System.out.println(currentDate);// new Date()为获取当前系统时间
        return currentDate;
    }

    // 新建文件夹的两个方法，这个也放入工具类里面

    /**
     * 传入请求号与文件名，自动建立文件夹并返回完整的输入文件路径
     *
     * @param queryId       传入的请求号
     * @param inputDataName 传入的输入文件名称
     * @return 完整的输入文件路径
     */
    public static String createDirAndPath(String queryId, String inputDataName) {
        String filePath1 = "D:/SNPPlatfromData/" + queryId + "/inputData";
        String filePath2 = "D:/SNPPlatfromData/" + queryId + "/resultData";
        mkdirs(filePath1);
        mkdirs(filePath2);

        String inputDataPath = filePath1 + "/" + inputDataName;
        System.out.println("最后的inputDataName: " + inputDataName);
        System.out.println("inputDataPath: " + inputDataPath);

        return inputDataPath;
    }

    /**
     * 返回结果文件应该存入的路径的方法
     *
     * @param queryId 传入的请求号
     * @param resDataName 传入的输入文件名称
     * @return 完整的输入文件路径
     *
     */

    public static String getResultDataPathAndName(String queryId, String resDataName){
        String filePath2 = "D:/SNPPlatfromData/" + queryId + "/resultData";
        String resDataPathAndName = filePath2 + "/" + resDataName;
        return resDataPathAndName;
    }
    // 这个也放到工具类里面

    /**
     * 单纯的生成文件夹的方法
     *
     * @param path 文件夹生成的绝对路径
     */
    public static void mkdirs(String path) {
        File fd = null;
        try {
            fd = new File(path);
            if (!fd.exists()) {
                fd.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fd = null;
        }
    }

    /**
     * 查找某文件夹是否为空，不空说明算法完成(暂时弃用)
     * @param dirPath 目标目录
     * @return true of false
     */
    public static boolean dirEmpty(String dirPath) {
        File file = new File(dirPath);
        if (file.isDirectory()) {
            if (file.list().length > 0) {
                System.out.println("目录不为空!");
                return false;
            } else {
                System.out.println("目录为空!");
                return true;
            }
        } else {
            System.out.println("这不是一个目录!");
            return true;
        }
    }

    /**
     * 判断目标文件夹(haveFinished)是否存在，若存在，说明算法执行完成
     * @param dirPath
     * @return true or false
     */
    public static boolean haveDir(String dirPath){
        File fd = new File(dirPath);
        if(fd.exists())
        {
            System.out.println("目标目录存在");
            return true;
        }else {
            System.out.println("目标目录不存在");
            return false;
        }
    }


    public static void main(String[] args) {

    }
}
