package com.fan.boot.modules.cmdrModule.BasicOperation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;

public class AverageNfoldResult {
    private double[] errorRate = null;
    private int[] rankCombinations = null;
    private int[] combination = null;
    private double[] TP = null;
    private double[] FP = null;
    private double[] FN = null;
    private double[] TN = null;
    private double averageErrorRate = 0.0D;
    private nfoldDataTools nfoldData = null;
    private int m_nfold = 0;

    public AverageNfoldResult(int nfold) {
        this.errorRate = new double[nfold];
        this.TP = new double[nfold];
        this.FP = new double[nfold];
        this.FN = new double[nfold];
        this.TN = new double[nfold];
        this.averageErrorRate = 100.0D;
        this.m_nfold = nfold;
    }

    public void setErrorRate(int index, double error) {
        if (this.errorRate != null) {
            this.errorRate[index] = error;
        }

    }

    public double getErrorRate(int index) {
        return this.errorRate[index];
    }

    public void setErrorRate(double[] error) {
        this.errorRate = error;
    }

    public void setTP_FP_TN_FN(double tp, double fp, double tn, double fn, int index) {
        this.TP[index] = tp;
        this.FP[index] = fp;
        this.TN[index] = tn;
        this.FN[index] = fn;
    }

    public double[] getErrorRate() {
        return this.errorRate;
    }

    public void computeAverageErrorRate() {
        double average = 0.0D;
        double[] var7;
        int var6 = (var7 = this.errorRate).length;

        for(int var5 = 0; var5 < var6; ++var5) {
            double value = var7[var5];
            average += value;
        }

        this.averageErrorRate = average / (double)this.errorRate.length;
    }

    public void setCombination(int[] combinations) {
        this.combination = combinations;
    }

    public int[] getCombination() {
        return this.rankCombinations;
    }

    public double[] getTP() {
        return this.TP;
    }

    public double getTP(int index) {
        return this.TP[index];
    }

    public double[] getFP() {
        return this.FP;
    }

    public double getFP(int index) {
        return this.FP[index];
    }

    public double[] getTN() {
        return this.TN;
    }

    public double getTN(int index) {
        return this.TN[index];
    }

    public double[] getFN() {
        return this.FN;
    }

    public double getFN(int index) {
        return this.FN[index];
    }

    public double getAverageErrorRate() {
        return this.averageErrorRate;
    }

    public void setAverageErrorRate(double averageErrorRate) {
        this.averageErrorRate = averageErrorRate;
    }

    public void setnfoldDataTools(nfoldDataTools nfoldData) {
        this.nfoldData = nfoldData;
    }

    public String showResult(String outpath) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(3);
        StringBuffer result = new StringBuffer();
        result.append("\n**********  " + this.combination.length + "-order SNP-SNP interaction************\n");
        result.append("\nThe best model is \t");

        int i;
        for(i = 0; i < this.combination.length; ++i) {
            result.append((String)this.nfoldData.getIndependentHeading().get(this.combination[i]) + ", ");
        }

        result.append("\n\nCV Results:\n");

        for(i = 0; i < this.m_nfold; ++i) {
            result.append(i + 1 + "-fold:\tTP: " + this.TP[i] + "\tFP: " + this.FP[i] + "\tFN: " + this.FN[i] + "\tTN: " + this.TN[i] + "\t Error: " + nf.format(this.getErrorRate(i)) + "\n");
        }

        result.append("\nThe average of error rates in " + this.m_nfold + "-fold is " + nf.format(this.getAverageErrorRate()));

        try {
            BufferedWriter ooutput = new BufferedWriter(new FileWriter(outpath, true));
            ooutput.append(result);
            ooutput.newLine();
            ooutput.flush();
            ooutput.close();
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        return result.toString();
    }
}
