
package com.fan.boot.modules.dcheModule.mtif;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class Score {
    private static int mask = 3;
    public static double[] arrLog;
    private static double logHS;
    private static double logDS;
    private static double[] arrLogTotal;
    private static double ratioHS;
    private static double ratioDS;
    private static double countH;
    private static double countD;
    private static HashMap<Integer, Integer> RecordCase = new HashMap();
    private static HashMap<Integer, Integer> RecordHealth = new HashMap();

    public Score() {
    }

    public static double motifEvaluationCor(byte[][] matrix, int[] combinations, int lastD, double[][] matrixP, double degree) {
        int row = matrix.length;
        int column = combinations.length;
        RecordCase.clear();
        RecordHealth.clear();

        int i;
        int j;
        int id;
        int value;

        for(i = 0; i <= lastD; ++i) {
            id = 0;

            for(j = 0; j < column; ++j) {
                id <<= 2;
                id += matrix[i][combinations[j]];
            }

            if (RecordCase.containsKey(id)) {
                value = (Integer)RecordCase.get(id);
                ++value;
                RecordCase.put(id, value);
            } else {
                value = 1;
                RecordCase.put(id, Integer.valueOf(value));
            }

            if (!RecordHealth.containsKey(id)) {
                RecordHealth.put(id, 0);
            }
        }

        for(i = lastD + 1; i < row; ++i) {
            id = 0;

            for(j = 0; j < column; ++j) {
                id <<= 2;
                id += matrix[i][combinations[j]];
            }

            if (RecordHealth.containsKey(id)) {
                value = (Integer)RecordHealth.get(id);
                ++value;
                RecordHealth.put(id, value);
            } else {
                value = 1;
                RecordHealth.put(id, Integer.valueOf(value));
            }

            if (!RecordCase.containsKey(id)) {
                RecordCase.put(id, 0);
            }
        }

        double result = 0.0D;
        Entry<Integer, Integer> entry = null;
        int D;
        int H;
        double pv = 0.0D;
        double temp = 0.0D;
        int key;
        int val;
        double p = 1.0D;
        Iterator ite = RecordCase.entrySet().iterator();

        while(true) {
            do {
                if (!ite.hasNext()) {
                    return result;
                }

                entry = (Entry)ite.next();
                D = (Integer)entry.getValue();
                H = (Integer)RecordHealth.get(entry.getKey());
            } while((double)D / countD < (double)H / countH);

            key = (Integer)entry.getKey();
            p = 1.0D;

            for(i = combinations.length - 1; i > -1; --i) {
                val = key & mask;
                key >>= 2;
                p *= matrixP[combinations[i]][val];
            }

            pv = logPValue(D + H, H, p, degree);
            temp = -Math.log(pv);
            result += temp;
        }
    }

    private static double logPValue(int C, int dH, double p, double degree) {
        int idx = (int)(p / degree);
        double logH = arrLog[idx];
        idx = (int)((1.0D - p) / degree);
        double logD = arrLog[idx];
        double result = 0.0D;
        double product = 0.0D;

        for(int i = 0; i <= dH; ++i) {
            product = 0.0D;

            int k;
            for(k = 0; k < i; ++k) {
                product += arrLogTotal[C - i + 1 + k];
                product -= arrLogTotal[k + 1];
                product += logH;
            }

            for(k = 0; k < C - i; ++k) {
                product += logD;
            }

            result += Math.pow(2.718281828459045D, product);
        }

        return result;
    }

    public static double pValue(double HS, double DS, int C, int dH) {
        double result = 0.0D;
        int i = 1;
        int di;
        int j;
        result += Math.pow(DS, (double)C);

        for(double product = 1.0D; i <= dH; ++i) {
            di = C - i;
            j = 0;
            product = 1.0D;

            int k;
            for(k = 1; k <= i; ++k) {
                product *= (double)(C - i + 1 + k - 1);
                product /= (double)k;
                product *= HS;
                if (j < di) {
                    ++j;
                    product *= DS;
                }
            }

            for(k = j; k < di; ++k) {
                product *= DS;
            }

            result += product;
        }

        return result;
    }

    public static void generateLogValue(double degree) {
        int n = (int)(1.0D / degree);
        arrLog = new double[n + 1];
        arrLog[0] = Math.log(degree - degree / 10.0D);

        for(int i = 1; i < n; ++i) {
            arrLog[i] = Math.log((double)i * degree);
        }

        arrLog[n] = 0.0D;
    }

    public static void initLog(double HS, double DS, int total) {
        ratioHS = HS;
        ratioDS = DS;
        logHS = Math.log(HS);
        logDS = Math.log(DS);
        arrLogTotal = new double[total + 1];

        for(int i = 1; i <= total; ++i) {
            arrLogTotal[i] = Math.log((double)i);
        }

        countH = ratioHS * (double)total;
        countD = ratioDS * (double)total;
    }

    public static double logPValue(int C, int dH) {
        double result = 0.0D;
        double product = 0.0D;

        for(int i = 0; i <= dH; ++i) {
            product = 0.0D;

            int k;
            for(k = 0; k < i; ++k) {
                product += arrLogTotal[C - i + 1 + k];
                product -= arrLogTotal[k + 1];
                product += logHS;
            }

            for(k = 0; k < C - i; ++k) {
                product += logDS;
            }

            result += Math.pow(2.718281828459045D, product);
        }

        return result;
    }

    public static double motifEvaluation(byte[][] matrix, int[] combinations, int lastD, double HS, double DS, int limit) {
        int row = matrix.length;
        int column = combinations.length;
        int id;
        int value;
        RecordCase.clear();
        RecordHealth.clear();

        int i;
        int j;
        for(i = 0; i <= lastD; ++i) {
            id = 0;

            for(j = 0; j < column; ++j) {
                id <<= 2;
                id += matrix[i][combinations[j]];
            }

            if (RecordCase.containsKey(id)) {
                value = (Integer)RecordCase.get(id);
                ++value;
                RecordCase.put(id, value);
            } else {
                value = 1;
                RecordCase.put(id, Integer.valueOf(value));
            }

            if (!RecordHealth.containsKey(id)) {
                RecordHealth.put(id, 0);
            }
        }

        for(i = lastD + 1; i < row; ++i) {
            id = 0;

            for(j = 0; j < column; ++j) {
                id <<= 2;
                id += matrix[i][combinations[j]];
            }

            if (RecordHealth.containsKey(id)) {
                value = (Integer)RecordHealth.get(id);
                ++value;
                RecordHealth.put(id, value);
            } else {
                value = 1;
                RecordHealth.put(id, Integer.valueOf(value));
            }

            if (!RecordCase.containsKey(id)) {
                RecordCase.put(id, 0);
            }
        }

        double result = 0.0D;
        Entry<Integer, Integer> entry = null;
        int D;
        int H;
        double pv = 0.0D;
        double temp = 0.0D;
        Iterator ite = RecordCase.entrySet().iterator();

        while(ite.hasNext()) {
            entry = (Entry)ite.next();
            D = (Integer)entry.getValue();
            H = (Integer)RecordHealth.get(entry.getKey());
            if (!((double)D / countD < (double)H / countH)) {
                pv = logPValue(D + H, H);
                temp = -Math.log(pv);
                result += temp;
            }
        }

        return result;
    }

    public static double motifEvaluation2(byte[][] matrix, int[] combinations, int lastD, double HS, double DS, int limit) {
        double result = 1.7976931348623157E308D;
        int row = matrix.length;
        int column = combinations.length;
        int id;
        int value;
        RecordCase.clear();
        RecordHealth.clear();

        int i;
        int D;

        for(i = 0; i <= lastD; ++i) {
            id = 0;

            for(D = 0; D < column; ++D) {
                id <<= 2;
                id += matrix[i][combinations[D]];
            }

            if (RecordCase.containsKey(id)) {
                value = (Integer)RecordCase.get(id);
                ++value;
                RecordCase.put(id, value);
            } else {
                value = 1;
                RecordCase.put(id, Integer.valueOf(value));
            }

            if (!RecordHealth.containsKey(id)) {
                RecordHealth.put(id, 0);
            }
        }

        for(i = lastD + 1; i < row; ++i) {
            id = 0;

            for(D = 0; D < column; ++D) {
                id <<= 2;
                id += matrix[i][combinations[D]];
            }

            if (RecordHealth.containsKey(id)) {
                value = (Integer)RecordHealth.get(id);
                ++value;
                RecordHealth.put(id, value);
            } else {
                value = 1;
                RecordHealth.put(id, Integer.valueOf(value));
            }

            if (!RecordCase.containsKey(id)) {
                RecordCase.put(id, 0);
            }
        }

        Entry<Integer, Integer> entry = null;
        int H;
        double pv = 0.0D;
        Iterator ite = RecordCase.entrySet().iterator();

        while(ite.hasNext()) {
            entry = (Entry)ite.next();
            D = (Integer)entry.getValue();
            H = (Integer)RecordHealth.get(entry.getKey());
            if (H <= limit && !((double)D / countD < (double)H / countH)) {
                pv = logPValue(D + H, H);
            } else {
                pv = 1.0D;
            }

            if (pv < result) {
                result = pv;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        initLog(0.5D, 0.5D, 800);
        double a = -Math.log(logPValue(21, 10));
        double b = -Math.log(logPValue(38, 10));
        double c = -Math.log(logPValue(21, 10));
        double d = -Math.log(logPValue(30, 10));
        System.out.println(a + b + c + 2.0D * d);
        a = -Math.log(logPValue(24, 10));
        b = -Math.log(logPValue(35, 10));
        c = -Math.log(logPValue(24, 10));
        d = -Math.log(logPValue(34, 10));
        System.out.println(a + b + c + d);
    }
}
