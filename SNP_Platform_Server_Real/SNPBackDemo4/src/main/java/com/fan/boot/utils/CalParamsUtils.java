package com.fan.boot.utils;

import java.io.*;

/**
 * 这个工具类是计算一些不需要手动输入的参数的工具
 * 比如给一个输入文件，那么相应的病例数、对照组数、SNP数都应该计算出来
 */
public class CalParamsUtils {
    /**
     * 类变量：snp数，case数，control数
     */
    private static int numSnp;
    private static int numCase;
    private static int numControl;


    /**
     * 给出输入文件，计算SNP数，即行数
     *
     * @param filePath
     *
     * @return int SNP数
     */

    public static void calParams(String filePath) {
        int nSnp = 0;
        String filePath0 = filePath;
        System.out.println("需要计算SNP数的文件的位置：" + filePath0);
        // 测试用
        String testPath = "D:/SNPPlatfromData/20210320133307/inputData/testdata.txt";
        File file = new File(filePath0);
        String encoding = "GBK";
        String nextLine = null;
        String[] splitHeadLine;
        int row = 0;
        int temCase = 0;
        int temControl = 0;
        try {
            if (file.isFile() && file.exists()) {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(reader);

                // 这一部分是计算case数和control数

                while((nextLine = bufferedReader.readLine()) != null){
                    // 判断空格，分割字符串
                    splitHeadLine = nextLine.split(" ");

                    if(row++ == 0){
                        // 这一部分是计算snp数
                        // System.out.println(nextLine);
                        // 减1是减掉标签
                        // 返回snp数的结果
                        nSnp = splitHeadLine.length - 1;
                    }

                    if(splitHeadLine[0].equals("0")){
                        temControl++;
                    }else{
                        temCase++;
                    }
                }
//                System.out.println("对照组数：" + temControl);
//                System.out.println("病例组数：" + temCase);

                // 返回最后结果并保存为类变量
                numSnp = nSnp;
                numControl = temControl;
                numCase = temCase;


                // 关闭输入流Reader
                reader.close();
                System.out.println("读取文件成功！");
            }else {
                System.out.println("找不到指定文件！");
            }
        }catch (Exception e){
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }

    }

    public static int getNumSnp() {
        return numSnp;
    }

    public static void setNumSnp(int numSnp) {
        CalParamsUtils.numSnp = numSnp;
    }

    public static int getNumCase() {
        return numCase;
    }

    public static void setNumCase(int numCase) {
        CalParamsUtils.numCase = numCase;
    }

    public static int getNumControl() {
        return numControl;
    }

    public static void setNumControl(int numControl) {
        CalParamsUtils.numControl = numControl;
    }

    public static void main(String[] args) {
        calParams("D:\\SNPPlatfromData\\20210318154604\\inputData\\testdata.txt");
        System.out.println("SNP数：" + getNumSnp());
        System.out.println("对照组数：" + getNumControl());
        System.out.println("病例组数：" + getNumCase());

    }
}
