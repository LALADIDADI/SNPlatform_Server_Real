package com.fan.boot.modules.mtif;

import com.fan.boot.modules.fastl.Datag;
import com.fan.boot.modules.fastl.Fisher;
import com.fan.boot.modules.fastl.ListAscendList;
import com.fan.boot.modules.fastl.ListAscendListElement;
import com.fan.boot.modules.fastl.ListDescendList;
import com.fan.boot.modules.fastl.Phi;

public class EveTwoLoci {
    private boolean[] arrEmpty = new boolean[243];
    private int[][] matrix = new int[2][243];
    private ListAscendList listFast = new ListAscendList(100);
    private ChiSquare2 calculator = new ChiSquare2();
    private Chi chiCal = new Chi();
    private static int numGroup = 3;
    private static int numGroupBound = 6;
    private ListDescendList listF = new ListDescendList(100);
    private Fisher fish = new Fisher();
    private Phi phi = new Phi();

    public EveTwoLoci() {
    }

    public double evaluate3(int[] combinations, Datag _SNPData) {
        this.listFast.Thre = 0.05D;
        int n = _SNPData.getDistribution(combinations, combinations.length, this.matrix, this.arrEmpty);
        this.listFast.clean();

        int i;
        for(i = 1; i < n; ++i) {
            if (this.arrEmpty[i]) {
                for(int j = 0; j < i; ++j) {
                    if (this.arrEmpty[j]) {
                        this.phi.setVectors(this.matrix, j, i);
                        this.listFast.add(j, i, this.phi.getPhi());
                    }
                }
            }
        }

        double result = 1.0D;
        i = n;

        while(true) {
            if (n > numGroup) {
                ListAscendListElement pair = this.listFast.getTop();
                if (pair == null) {
                    if (result == 1.0D) {
                        result = this.chiCal.cal(this.matrix, this.arrEmpty, i);
                        result = ChiSquare2.pValue((double)(n - 1), result);
                    }

                    return result;
                }

                this.matrix[0][i] = this.matrix[0][pair.id1] + this.matrix[0][pair.id2];
                this.matrix[1][i] = this.matrix[1][pair.id1] + this.matrix[1][pair.id2];
                this.arrEmpty[i] = true;
                this.arrEmpty[pair.id1] = false;
                this.arrEmpty[pair.id2] = false;
                --n;
                if (n <= numGroupBound) {
                    double temp = this.chiCal.cal(this.matrix, this.arrEmpty, i + 1);
                    temp = ChiSquare2.pValue((double)(n - 1), temp);
                    if (temp < result) {
                        result = temp;
                    }
                }

                if (n > numGroup) {
                    for(int j = 0; j < i; ++j) {
                        if (this.arrEmpty[j]) {
                            this.phi.setVectors(this.matrix, j, i);
                            this.listFast.add(j, i, this.phi.getPhi());
                        }
                    }

                    ++i;
                    continue;
                }
            }

            return result;
        }
    }

    public double evaluate2(int[] combinations, Datag _SNPData) {
        int n = _SNPData.getDistribution(combinations, combinations.length, this.matrix, this.arrEmpty);
        this.listF.clean();

        int i;
        for(i = 1; i < n; ++i) {
            if (this.arrEmpty[i]) {
                for(int j = 0; j < i; ++j) {
                    if (this.arrEmpty[j]) {
                        this.fish.setVectors(this.matrix, j, i);
                        this.listF.add(j, i, this.fish.fisher());
                    }
                }
            }
        }

        double result = 1.0D;
        i = n;

        while(true) {
            if (n > numGroup) {
                ListAscendListElement pair = this.listF.getTop();
                if (pair == null) {
                    if (result == 1.0D) {
                        result = this.chiCal.cal(this.matrix, this.arrEmpty, i);
                        result = ChiSquare2.pValue((double)(n - 1), result);
                    }

                    return result;
                }

                this.matrix[0][i] = this.matrix[0][pair.id1] + this.matrix[0][pair.id2];
                this.matrix[1][i] = this.matrix[1][pair.id1] + this.matrix[1][pair.id2];
                this.arrEmpty[i] = true;
                this.arrEmpty[pair.id1] = false;
                this.arrEmpty[pair.id2] = false;
                --n;
                if (n <= numGroupBound) {
                    double temp = this.chiCal.cal(this.matrix, this.arrEmpty, i + 1);
                    temp = ChiSquare2.pValue((double)(n - 1), temp);
                    if (temp < result) {
                        result = temp;
                    }
                }

                if (n > numGroup) {
                    for(int j = 0; j < i; ++j) {
                        if (this.arrEmpty[j]) {
                            this.fish.setVectors(this.matrix, j, i);
                            this.listF.add(j, i, this.fish.fisher());
                        }
                    }

                    ++i;
                    continue;
                }
            }

            return result;
        }
    }

    public double evaluate(int[] combinations, Datag _SNPData) {
        int n = _SNPData.getDistribution(combinations, combinations.length, this.matrix, this.arrEmpty);
        this.listFast.clean();

        int i;
        for(i = 1; i < n; ++i) {
            if (this.arrEmpty[i]) {
                for(int j = 0; j < i; ++j) {
                    if (this.arrEmpty[j]) {
                        this.calculator.setVectors(this.matrix, j, i);
                        this.listFast.add(j, i, this.calculator.calculateChi2());
                    }
                }
            }
        }

        double result = 1.0D;
        i = n;

        while(true) {
            if (n > numGroup) {
                ListAscendListElement pair = this.listFast.getTop();
                if (pair == null) {
                    if (result == 1.0D) {
                        result = this.chiCal.cal(this.matrix, this.arrEmpty, i);
                        result = ChiSquare2.pValue((double)(n - 1), result);
                    }

                    return result;
                }

                this.matrix[0][i] = this.matrix[0][pair.id1] + this.matrix[0][pair.id2];
                this.matrix[1][i] = this.matrix[1][pair.id1] + this.matrix[1][pair.id2];
                this.arrEmpty[i] = true;
                this.arrEmpty[pair.id1] = false;
                this.arrEmpty[pair.id2] = false;
                --n;
                if (n <= numGroupBound) {
                    double temp = this.chiCal.cal(this.matrix, this.arrEmpty, i + 1);
                    temp = ChiSquare2.pValue((double)(n - 1), temp);
                    if (temp < result) {
                        result = temp;
                    }
                }

                if (n > numGroup) {
                    for(int j = 0; j < i; ++j) {
                        if (this.arrEmpty[j]) {
                            this.calculator.setVectors(this.matrix, j, i);
                            this.listFast.add(j, i, this.calculator.calculateChi2());
                        }
                    }

                    ++i;
                    continue;
                }
            }

            return result;
        }
    }
}
