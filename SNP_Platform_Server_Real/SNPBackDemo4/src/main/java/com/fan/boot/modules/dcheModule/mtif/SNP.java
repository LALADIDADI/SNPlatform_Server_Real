package com.fan.boot.modules.dcheModule.mtif;

public class SNP {
    private int[] arrComb;
    private double score;

    public SNP(int[] combination, double _s) {
        this.arrComb = new int[combination.length];

        for(int i = 0; i < combination.length; ++i) {
            this.arrComb[i] = combination[i];
        }

        this.score = _s;
    }

    public double getScore() {
        return this.score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int[] getArrComb() {
        return this.arrComb;
    }

    public void setArrComb(int[] arrComb) {
        this.arrComb = arrComb;
    }

    public void print() {
        for(int i = 0; i < this.arrComb.length; ++i) {
            System.out.print(this.arrComb[i] + " ");
        }

        System.out.println(this.score);
    }
}
