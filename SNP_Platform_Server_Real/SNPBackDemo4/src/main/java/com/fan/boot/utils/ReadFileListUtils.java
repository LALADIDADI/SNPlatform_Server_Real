package com.fan.boot.utils;

import java.io.File;

public class ReadFileListUtils {
    public static void main(String[] args) {
        String[] res = getFileName( "C:\\Users\\Administrator\\Desktop\\毕业设计\\代码\\testInputFile");
        for(int i = 0; i < res.length; i++)
            System.out.println(res[i]);
    }

    public static String[] getFileName(String filePath) {
        String path = filePath;
        File f = new File(path);//获取路径
        if (!f.exists()) {
            System.out.println(path + " 路径不存在");
            return null;
        }

        File fa[] = f.listFiles();
        String resFileList[] = new String[fa.length];
        for (int i = 0; i < fa.length; i++) {
            File fs = fa[i];
            if (fs.isDirectory()) {
                System.out.println(fs.getName() + " [目录]");
            } else {
                resFileList[i] = fs.getName();
                System.out.println(fs.getName());
            }
        }

        return resFileList;
    }
}
