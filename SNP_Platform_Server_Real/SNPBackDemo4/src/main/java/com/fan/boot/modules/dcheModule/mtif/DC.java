package com.fan.boot.modules.dcheModule.mtif;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DC {
    public DC() {
    }

    public static void main(String[] args) throws IOException {
        String folderPath = "D:\\Research\\Research-Data\\SNP\\AMD\\";
        String prename = "AMDData";
        String postname = ".txt";
        int st = 99;
        int ed = 100;
        int nSNP = 90449;
        int nSample = 800;
        int nCase = 96;
        String resultsPath = "";
        String resultsName = "real";
        int[] answers = new int[]{0, 1, 2, 3};
        double cv = 0.0015D;
        if (args.length == 11) {
            folderPath = args[0];
            st = Integer.parseInt(args[1]);
            ed = Integer.parseInt(args[2]);
            nSNP = Integer.parseInt(args[3]);
            nSample = Integer.parseInt(args[4]);
            nCase = Integer.parseInt(args[5]);
            resultsPath = args[6];
            resultsName = args[7];
            answers[0] = Integer.parseInt(args[8]);
            answers[1] = Integer.parseInt(args[9]);
            cv = Double.parseDouble(args[10]);
            Exhaustion.funcSNP = answers;
        } else {
            System.out.println("java -jar DC.jar Model_folder_path start_order end_order nSNP nSample nCase Results_Path Results_File_Name funSNP1 funSNP2 critical_value");
            System.out.println("java -jar DC.jar /home/xguo9/SNP/false/800/ 0 100 2000 800 400 /home/xguo9/SNP/false/ChiDC/resultshighorder/ false0 0 1 0.00340311151758776");
        }

        for(int i = st; i < ed; ++i) {
            System.out.println("Process " + i);
            Exhaustion objE = new Exhaustion(nSample, nSNP, nCase);
            double[] arrCV = new double[]{cv};
            objE.setCV(arrCV);
            String filepath = folderPath + prename + postname;
            objE.readData(filepath);
            objE.twoSearch();
            FileWriter fwR = new FileWriter(resultsPath + resultsName + ".txt", true);
            objE.checkResult(fwR);
            fwR.flush();
            fwR.close();
        }

        System.out.println("Over");
    }

    public static void test() {
        String filename = "D:\\Research\\2013_0709_SNP\\Results\\RA\\2_SNPs.txt";
        String filepath = "D:\\Code\\SNP\\RealDataConvert\\Data_narac_filted.txt";
        String mapname = "D:\\Code\\SNP\\RealDataConvert\\Data_narac.map";
        Exhaustion objE = new Exhaustion(2062, 487678, 868);
        objE.readData(filepath);
        System.out.println("end reading");

        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            BufferedReader brN = new BufferedReader(new FileReader(mapname));
            String[] names = new String[487678];

            String inputLine;
            int i;
            for(i = 0; (inputLine = brN.readLine()) != null; ++i) {
                names[i] = inputLine;
            }

            brN.close();
            FileWriter fw = new FileWriter("results_2.txt");
            int[] combinations = new int[2];
            String space = " ";
            String tab = "\t";
            String enter = "\n";
            double value = 0.0D;
            i = 0;

            while((inputLine = br.readLine()) != null) {
                String[] arrStr = inputLine.split(space);
                combinations[0] = Integer.parseInt(arrStr[0]);
                combinations[1] = Integer.parseInt(arrStr[1]);
                value = objE.testSearch(combinations);
                ++i;
                System.out.println("processing " + i);
                fw.write(names[combinations[0]] + tab + names[combinations[1]] + tab + value);
                fw.write(enter);
                fw.flush();
            }

            br.close();
            fw.close();
        } catch (IOException var17) {
            var17.printStackTrace();
        }

    }
}
