package com.fan.boot.modules.decmdrModule.BasicOperation;

import java.util.Iterator;

public class GenerateAllCombination implements Iterable<int[]> {
    private int order = 0;
    private int numberOfColumns = 0;
    private int[] availableCombination = null;

    public GenerateAllCombination(int order, int numberOfColumns) {
        this.order = order;
        this.availableCombination = new int[order];
        this.numberOfColumns = numberOfColumns;

        for(int i = 0; i < order; ++i) {
            this.availableCombination[i] = 0;
        }

        int var10002 = this.availableCombination[order - 1]--;
    }

    public Iterator<int[]> iterator() {
        return new Iterator<int[]>() {
            int pos;
            int region;

            {
                this.pos = GenerateAllCombination.this.order - 1;
                this.region = GenerateAllCombination.this.numberOfColumns - GenerateAllCombination.this.order;
            }

            public boolean hasNext() {
                return this.pos >= 0;
            }

            public int[] next() {
                boolean flag = true;
                if (GenerateAllCombination.this.availableCombination[this.pos] < this.pos + this.region) {
                    int var10002;
                    if (this.pos == GenerateAllCombination.this.order - 1) {
                        var10002 = GenerateAllCombination.this.availableCombination[this.pos]++;
                        flag = true;
                    } else if (GenerateAllCombination.this.availableCombination[this.pos + 1] >= this.pos + this.region) {
                        GenerateAllCombination.this.availableCombination[this.pos + 1] = -1;
                        var10002 = GenerateAllCombination.this.availableCombination[this.pos++]++;
                        flag = false;
                    }
                } else if (GenerateAllCombination.this.availableCombination[this.pos] >= this.pos + this.region) {
                    --this.pos;
                    flag = false;
                }

                if (flag) {
                    return GenerateAllCombination.this.availableCombination;
                } else {
                    return this.hasNext() ? this.next() : null;
                }
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
