package com.fan.boot.modules.mtif;

import java.util.HashMap;

public class ChiSquare2 {
    private static HashMap<Long, Double> arrChiValue = new HashMap();
    private static long[] arrEle4 = new long[4];
    private static long temp4ArrEle4;
    private double[][] matrixO2 = new double[2][2];
    private double[] sumR2 = new double[2];
    private double[] sumC2 = new double[2];
    private double sum2;
    private double[][] matrixE2 = new double[2][2];
    private double chi2;
    private static double expectedVal = 0.002D;

    public ChiSquare2() {
    }

    public double calculateChi2() {
        int i;
        for(i = 0; i < 2; ++i) {
            this.sumR2[i] = 0.0D;
            this.sumC2[i] = 0.0D;
        }

        this.sum2 = 0.0D;

        for(i = 0; i < 2; ++i) {
            this.sumR2[i] = this.matrixO2[i][0] + this.matrixO2[i][1];
            this.sumC2[i] = this.matrixO2[0][i] + this.matrixO2[1][i];
            this.sum2 += this.sumC2[i];
        }

        int j;
        for(i = 0; i < 2; ++i) {
            for(j = 0; j < 2; ++j) {
                this.matrixE2[i][j] = this.sumR2[i] * this.sumC2[j] / this.sum2;
            }
        }

        this.chi2 = 0.0D;

        for(i = 0; i < 2; ++i) {
            for(j = 0; j < 2; ++j) {
                if (this.matrixE2[i][j] == 0.0D) {
                    this.chi2 += Math.pow(this.matrixO2[i][j] - expectedVal, 2.0D) / expectedVal;
                } else {
                    this.chi2 += Math.pow(this.matrixO2[i][j] - this.matrixE2[i][j], 2.0D) / this.matrixE2[i][j];
                }
            }
        }

        return this.chi2;
    }

    public double checkValue() {
        arrEle4[0] = (long)this.matrixO2[0][0];
        arrEle4[1] = (long)this.matrixO2[0][1];
        arrEle4[2] = (long)this.matrixO2[1][0];
        arrEle4[3] = (long)this.matrixO2[1][1];

        int i;
        for(i = 0; i < 3; ++i) {
            for(int j = i + 1; j < 4; ++j) {
                if (arrEle4[i] > arrEle4[j]) {
                    temp4ArrEle4 = arrEle4[i];
                    arrEle4[i] = arrEle4[j];
                    arrEle4[j] = temp4ArrEle4;
                }
            }
        }

        temp4ArrEle4 = 0L;

        for(i = 0; i < 4; ++i) {
            temp4ArrEle4 <<= 15;
            temp4ArrEle4 += arrEle4[i];
        }

        if (arrChiValue.containsKey(temp4ArrEle4)) {
            return (Double)arrChiValue.get(temp4ArrEle4);
        } else {
            return -1.0D;
        }
    }

    public double getChi2() {
        return this.chi2;
    }

    public static double pValue(double degree, double critical) {
        double p = 0.0D;
        if (critical / 2.0D > degree / 2.0D + 1.0D) {
            p = MyGamma.regularizedGammaQ(degree / 2.0D, critical / 2.0D, 1.0E-49D, 2147483647);
        } else {
            p = 1.0D - MyGamma.regularizedGammaP(degree / 2.0D, critical / 2.0D, 1.0E-49D, 2147483647);
        }

        return p;
    }

    public void run() {
        this.calculateChi2();
    }

    public void setVectors(int[][] _matrix, int _c1, int _c2) {
        for(int i = 0; i < 2; ++i) {
            this.matrixO2[i][0] = (double)_matrix[i][_c1];
            this.matrixO2[i][1] = (double)_matrix[i][_c2];
        }

        if (this.matrixO2[0][0] == 0.0D && this.matrixO2[0][1] == 0.0D) {
            this.matrixO2[0][0] = expectedVal;
            this.matrixO2[0][1] = expectedVal;
        }

        if (this.matrixO2[1][0] == 0.0D && this.matrixO2[1][1] == 0.0D) {
            this.matrixO2[1][0] = expectedVal;
            this.matrixO2[1][1] = expectedVal;
        }

    }

    public static void main(String[] args) {
        double c = pValue(26.0D, 95.94D);
        double d = c * 1.66167E8D;
        System.out.println(c + "\t" + d);
        Chi chiCal = new Chi();
        int[][] matrix = new int[][]{{100, 5, 41}, {0, 0, 72}};
        ChiSquare2 chi = new ChiSquare2();
        chi.setVectors(matrix, 0, 1);
        System.out.println("chi: " + chi.calculateChi2());
        int size = 3;
        boolean[] arrEmpty = new boolean[size];

        for(int i = 0; i < size; ++i) {
            arrEmpty[i] = true;
        }

        double x = chiCal.cal(matrix, arrEmpty, size);
        System.out.println(x);
        System.out.println(pValue(2.0D, x));
    }
}
