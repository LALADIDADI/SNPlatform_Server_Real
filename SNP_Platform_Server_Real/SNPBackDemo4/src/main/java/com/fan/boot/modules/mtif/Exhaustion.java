package com.fan.boot.modules.mtif;

import com.fan.boot.modules.fastl.Datag;
import com.fan.boot.modules.fastl.ListAscendStore;
import com.fan.boot.modules.fastl.ListMutipleElement;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Exhaustion {
    public static int[] funcSNP = new int[]{0, 1, 2, 3};
    public static int ksize = 2;
    public static int[] combination;
    public static double pv;
    public static Datag SNPData;
    public static int nSample;
    public static int nSNP;
    public static int nCase;
    private int countConstant = 10000;
    private int countEvaluation = 0;
    public boolean flagPrint = false;
    private double[] arrCV = new double[3];
    private ListAscendStore[] arrListLocus = new ListAscendStore[3];
    private EveTwoLoci eve = new EveTwoLoci();
    private int accuarcy = 0;
    private int accuarcy2 = 0;
    private String enter = "\n";
    private static int idxSort = -1;
    private static int tempSort = 0;

    public Exhaustion(int _nSample, int _nSNP, int _nCase) {
        nCase = _nCase;
        nSample = _nSample;
        nSNP = _nSNP;
        SNPData = new Datag(_nCase, _nSample - _nCase, _nSNP);
        int[] list = new int[]{200, 50, 10};
        this.setList(list);
        double[] alpha = new double[]{0.0015D, 1.2E-7D, 1.0E-21D};
        this.setCV(alpha);
    }

    public Exhaustion(int _nSample, int _nSNP, int _nCase, double[] _alpha, int[] _list) {
        nCase = _nCase;
        nSample = _nSample;
        nSNP = _nSNP;
        SNPData = new Datag(_nCase, _nSample - _nCase, _nSNP);
        this.setCV(_alpha);
        this.setList(_list);
    }

    public void checkResult() {
        ListMutipleElement pair = this.arrListLocus[0].getTop();
        if (pair.list[0] == funcSNP[0] && pair.list[1] == funcSNP[1]) {
            System.out.print("t");
            if (pair.value > this.arrCV[0]) {
                System.out.print("f");
            }

            ++this.accuarcy;
        } else if (pair.list[1] == funcSNP[0] && pair.list[0] == funcSNP[1]) {
            System.out.print("t");
            if (pair.value > this.arrCV[0]) {
                System.out.print("f");
            }

            ++this.accuarcy;
        } else {
            System.out.print("f");
        }

        System.out.print("\t");
        this.arrListLocus[0].list[0].print();
    }

    public void checkResult(FileWriter fwR) throws IOException {
        ListMutipleElement pair = this.arrListLocus[0].getTop();
        pair.Sort();
        if (pair.list[0] == funcSNP[0] && pair.list[1] == funcSNP[1]) {
            fwR.write("t");
            if (pair.value > this.arrCV[0]) {
                fwR.write("f");
            }
        } else {
            fwR.write("f");
        }

        fwR.write("\t");
        fwR.write(pair.getString());
        fwR.write("\n");
    }

    public void checkResultsThree(FileWriter fwR) throws IOException {
        ListMutipleElement pair = this.arrListLocus[1].getTop();
        pair.Sort();
        if (pair.list[0] == funcSNP[0] && pair.list[1] == funcSNP[1] && pair.list[2] == funcSNP[2]) {
            fwR.write("t");
            if (pair.value > this.arrCV[1]) {
                fwR.write("f");
            }
        } else {
            fwR.write("f");
        }

        fwR.write("\t");
        fwR.write(pair.getString());
        fwR.write("\n");
    }

    public void checkResultsFour(FileWriter fwR) throws IOException {
        ListMutipleElement pair = this.arrListLocus[2].getTop();
        pair.Sort();
        if (pair.list[0] == funcSNP[0] && pair.list[1] == funcSNP[1] && pair.list[2] == funcSNP[2] && pair.list[3] == funcSNP[3]) {
            fwR.write("t");
            if (pair.value > this.arrCV[2]) {
                fwR.write("f");
            }
        } else {
            fwR.write("f");
        }

        fwR.write("\t");
        fwR.write(pair.getString());
        fwR.write("\n");
    }

    public void clean() {
        this.accuarcy = 0;
        this.accuarcy2 = 0;
    }

    public static int combinatorial(int _n, int _x) {
        int result = 1;

        int i;
        for(i = 0; i < _x; ++i) {
            result *= _n - i;
        }

        for(i = 0; i < _x; ++i) {
            result /= i + 1;
        }

        return result;
    }

    public double testSearch(int[] combinations) {
        double value = this.eve.evaluate(combinations, SNPData);
        return value;
    }

    public void twoSearch() {
        this.arrListLocus[0].clean();
        this.countEvaluation = 0;
        int[] combinations = new int[2];
        double value = 0.0D;

        for(int i = 0; i < nSNP; ++i) {
            combinations[0] = i;

            for(int j = i + 1; j < nSNP; ++j) {
                combinations[1] = j;
                if (this.flagPrint && this.countEvaluation % this.countConstant == 0) {
                    System.out.print(this.countEvaluation + " ");
                }

                ++this.countEvaluation;
                value = this.eve.evaluate(combinations, SNPData);
                this.arrListLocus[0].add(combinations, value);
            }
        }

    }

    public void twoSearch2() {
        this.arrListLocus[0].clean();
        int[] combinations = new int[2];
        double value = 0.0D;

        for(int i = 0; i < nSNP; ++i) {
            combinations[0] = i;

            for(int j = i + 1; j < nSNP; ++j) {
                combinations[1] = j;
                value = this.eve.evaluate2(combinations, SNPData);
                this.arrListLocus[0].add(combinations, value);
            }
        }

    }

    public void threeSearch() {
        this.arrListLocus[1].clean();
        this.countEvaluation = 0;
        int[] combinations = new int[3];
        double value = 0.0D;

        for(int i = 0; i < this.arrListLocus[0].idx; ++i) {
            for(int j = 0; j < nSNP; ++j) {
                combinations[0] = this.arrListLocus[0].list[i].list[0];
                combinations[1] = this.arrListLocus[0].list[i].list[1];
                if (combinations[0] != j && combinations[1] != j) {
                    combinations[2] = j;
                    Sort(combinations);
                    if (this.flagPrint && this.countEvaluation % this.countConstant == 0) {
                        System.out.print(this.countEvaluation + " ");
                    }

                    ++this.countEvaluation;
                    value = this.eve.evaluate(combinations, SNPData);
                    this.arrListLocus[1].add(combinations, value);
                }
            }
        }

    }

    public void fourSearch() {
        this.arrListLocus[2].clean();
        this.countEvaluation = 0;
        int[] combinations = new int[4];
        double value = 0.0D;

        for(int i = 0; i < this.arrListLocus[1].idx; ++i) {
            for(int j = 0; j < nSNP; ++j) {
                combinations[0] = this.arrListLocus[1].list[i].list[0];
                combinations[1] = this.arrListLocus[1].list[i].list[1];
                combinations[2] = this.arrListLocus[1].list[i].list[2];
                if (combinations[0] != j && combinations[1] != j && combinations[2] != j) {
                    combinations[3] = j;
                    Sort(combinations);
                    if (this.flagPrint && this.countEvaluation % this.countConstant == 0) {
                        System.out.print(this.countEvaluation + " ");
                    }

                    ++this.countEvaluation;
                    value = this.eve.evaluate(combinations, SNPData);
                    this.arrListLocus[2].add(combinations, value);
                }
            }
        }

    }

    public void output(FileWriter fwR) throws IOException {
        fwR.write("Total " + this.accuarcy + "\n");
        fwR.write("Another " + this.accuarcy2 + "\n");
    }

    public void output() {
        System.out.println("Total " + this.accuarcy);
    }

    public void printTwoAll() {
        this.arrListLocus[0].printAll();
    }

    public void printThreeAll() {
        this.arrListLocus[1].printAll();
    }

    public void printTwo(FileWriter fwR) throws IOException {
        this.arrListLocus[0].printAll(fwR);
    }

    public void printTwoOne(FileWriter fwR) throws IOException {
        if (this.arrListLocus[0].idx == 0) {
            fwR.write(this.enter);
        } else {
            fwR.write(this.arrListLocus[0].list[0].getString());
            fwR.write(this.enter);
        }
    }

    public void printThree(FileWriter fwR) throws IOException {
        this.arrListLocus[1].printAll(fwR);
    }

    public void printFour(FileWriter fwR) throws IOException {
        this.arrListLocus[2].printAll(fwR);
    }

    public static void Sort(int[] _arr) {
        idxSort = -1;
        tempSort = 0;

        for(int i = 0; i < _arr.length; ++i) {
            idxSort = i;

            for(int j = i + 1; j < _arr.length; ++j) {
                if (_arr[j] < _arr[idxSort]) {
                    idxSort = j;
                }
            }

            if (idxSort != i) {
                tempSort = _arr[i];
                _arr[i] = _arr[idxSort];
                _arr[idxSort] = tempSort;
            }
        }

    }

    public void readData(String path) {
        String inputLine = null;
        SNPData.clean();

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));

            while((inputLine = br.readLine()) != null) {
                SNPData.add(inputLine);
            }

            br.close();
        } catch (FileNotFoundException var4) {
            var4.printStackTrace();
        } catch (IOException var5) {
            var5.printStackTrace();
        }

    }

    public void setCV(double[] _alpha) {
        this.arrCV = new double[_alpha.length];

        for(int i = 0; i < _alpha.length; ++i) {
            this.arrCV[i] = _alpha[i] / (double)combinatorial(nSNP, i + 2);
        }

    }

    public void setList(int[] _list) {
        this.arrListLocus = new ListAscendStore[_list.length];

        for(int i = 0; i < _list.length; ++i) {
            this.arrListLocus[i] = new ListAscendStore(_list[i], i + 2);
        }

    }

    public void writeResults(String _fileName, int _order) {
        try {
            FileWriter fwR = new FileWriter(_fileName, true);
            this.arrListLocus[_order].printAll(fwR, this.arrCV[_order]);
            fwR.flush();
            fwR.close();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public static void test() {
        int id ;
        String tab = "\t";
        String ca = "1";
        int[] arrIntCase = new int[50];
        int[] arrIntControl = new int[50];

        try {
            BufferedReader br = new BufferedReader(new FileReader("test_0_4.txt"));
            String inputLine = null;
            String[] arrStr = null;

            while((inputLine = br.readLine()) != null) {
                arrStr = inputLine.split(tab);
                id = 0;

                for(int i = 1; i < 3; ++i) {
                    id = (id << 2) + Integer.parseInt(arrStr[i]);
                }

                int var10002;
                if (arrStr[0].compareTo(ca) == 0) {
                    var10002 = arrIntCase[id]++;
                } else {
                    var10002 = arrIntControl[id]++;
                }
            }

            br.close();
        } catch (FileNotFoundException var9) {
            var9.printStackTrace();
        } catch (IOException var10) {
            var10.printStackTrace();
        }

    }
}
