package com.fan.boot.modules.decmdrModule.BasicOperation;

import java.util.Iterator;

public class GeneratePermutationCombination implements Iterable<int[]> {
    private int order = 0;
    private int numberOfColumns = 0;
    private int[] availableCombination = null;

    public GeneratePermutationCombination(int order, int numbrOfColumns) {
        this.order = order;
        this.availableCombination = new int[order];
        this.numberOfColumns = numbrOfColumns;

        for(int i = 0; i < order; this.availableCombination[i] = i++) {
        }

        int var10002 = this.availableCombination[order - 1]--;
    }

    public Iterator<int[]> iterator() {
        return new Iterator<int[]>() {
            int pos;
            int region;

            {
                this.pos = GeneratePermutationCombination.this.order - 1;
                this.region = GeneratePermutationCombination.this.numberOfColumns - GeneratePermutationCombination.this.order;
            }

            public boolean hasNext() {
                return this.pos >= 0;
            }

            public int[] next() {
                boolean flag = true;
                if (GeneratePermutationCombination.this.availableCombination[this.pos] < this.pos + this.region) {
                    if (this.pos == GeneratePermutationCombination.this.order - 1) {
                        int var10002 = GeneratePermutationCombination.this.availableCombination[this.pos]++;
                        flag = true;
                    } else if (GeneratePermutationCombination.this.availableCombination[this.pos + 1] >= this.pos + this.region) {
                        GeneratePermutationCombination.this.availableCombination[this.pos + 1] = ++GeneratePermutationCombination.this.availableCombination[this.pos++];
                        flag = false;
                    }
                } else if (GeneratePermutationCombination.this.availableCombination[this.pos] >= this.pos + this.region) {
                    --this.pos;
                    flag = false;
                }

                if (flag) {
                    return GeneratePermutationCombination.this.availableCombination;
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
