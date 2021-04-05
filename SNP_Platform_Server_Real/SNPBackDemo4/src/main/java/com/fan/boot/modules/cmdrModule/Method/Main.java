package com.fan.boot.modules.cmdrModule.Method;

import com.fan.boot.modules.cmdrModule.UI.Help;
import com.fan.boot.modules.cmdrModule.UI.Preprocess;
import com.fan.boot.modules.cmdrModule.UI.UserSetParameters;
import java.io.File;
import java.util.Scanner;

public class Main {
    private final int DEFAULT_VALUE = -1;

    public Main(String[] s) throws Exception {
        String dataName = "";
        UserSetParameters parameters = new UserSetParameters();
        boolean flag = true;

        for(int i = 0; i < s.length; ++i) {
            String var6;
            switch((var6 = s[i]).hashCode()) {
                case 1498:
                    if (var6.equals("-g")) {
                        ++i;
                        parameters.setMaxGeneration((int)this.getNumerics(s[i], "Max generation"));
                        continue;
                    }
                    break;
                case 1504:
                    if (var6.equals("-m")) {
                        ++i;
                        parameters.setMutationFactor(this.getNumerics(s[i], "Mutation factor"));
                        continue;
                    }
                    break;
                case 1506:
                    if (var6.equals("-o")) {
                        ++i;
                        parameters.setOrder((int)this.getNumerics(s[i], "Order"));
                        continue;
                    }
                    break;
                case 1507:
                    if (var6.equals("-p")) {
                        ++i;
                        parameters.setSwarmSize((int)this.getNumerics(s[i], "Population size"));
                        continue;
                    }
                    break;
                case 1509:
                    if (var6.equals("-r")) {
                        ++i;
                        parameters.setRecombinationCRfactor(this.getNumerics(s[i], "Recombination CR factor"));
                        continue;
                    }
                    break;
                case 1510:
                    if (var6.equals("-s")) {
                        ++i;
                        parameters.setSeed((int)this.getNumerics(s[i], "Seed"));
                        continue;
                    }
                    break;
                case 44757230:
                    if (var6.equals("-help")) {
                        flag = false;
                        new Help();
                        continue;
                    }
            }

            dataName = this.checkData(s[i], true);
        }

        if (flag) {
            new Preprocess(dataName, "Class", parameters);
        }

    }

    private double getNumerics(String inputValue, String parameterType) throws Exception {
        double numericus = 0.0D;

        try {
            numericus = Double.valueOf(inputValue);
        } catch (NumberFormatException var7) {
            if (!inputValue.equals("No") && !inputValue.equals("no") && !inputValue.equals("NO") && !inputValue.equals("nO")) {
                Scanner scanner = new Scanner(System.in);
                System.out.print("Please input number for " + parameterType + " (Cancel: no): ");
                this.getNumerics(scanner.next(), parameterType);
            } else {
                numericus = -1.0D;
            }
        }

        return numericus;
    }

    private String checkData(String dataPathway, boolean flag) throws Exception {
        String Pathway = dataPathway + ".txt";
        File f = new File(Pathway);
        return f.isFile() ? dataPathway : (flag ? this.checkData(System.getProperty("user.dir") + "\\" + Pathway, false) : this.checkData(this.getPathway(), true));
    }

    private String getPathway() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please input correct dataset name/pathway: ");
        return scanner.next();
    }

    public static void main(String[] args) {
        try {
            String[] s = {"D:\\model\\Example_data"};
            new Main(s);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
