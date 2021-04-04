package com.fan.boot.utils;

import java.io.File;

public class CheckUtils {
    /**
     * 返回某一文件夹中的文件夹数目
     *
     */
    public static int getDirCount(String path){
        int res = 0;
        File f = new File(path);//获取路径
        if (!f.exists()) {
            System.out.println(path + " 路径不存在");
            return -1;
        }

        File fa[] = f.listFiles();

        for (int i = 0; i < fa.length; i++) {
            File fs = fa[i];
            if (fs.isDirectory()) {
                res++;
                System.out.println("目录更新：" + res);
            }
        }
        return res;
    }
    /**
     * 返回某一路径是否存在
     * @param path
     * @return boolean
     */
    public static boolean isDir(String path){
        boolean res = false;
        File f = new File(path);
        if(f.exists()){
            res = true;
        }
        return res;
    }
}
