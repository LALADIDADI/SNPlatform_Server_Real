package com.fan.boot.modules.dcheModule.fastl;

public class Phi {
    private double a;
    private double b;
    private double c;
    private double d;
    private double p;
    private double e;
    private double f;
    private double g;
    private double h;
    private int[][] matrix = new int[2][2];

    public Phi() {
    }

    public void setVectors(int[][] _matrix, int _c1, int _c2) {
        for(int i = 0; i < 2; ++i) {
            this.matrix[i][0] = _matrix[i][_c1];
            this.matrix[i][1] = _matrix[i][_c2];
        }

    }

    public double getPhi() {
        this.a = (double)this.matrix[0][0];
        this.b = (double)this.matrix[0][1];
        this.c = (double)this.matrix[1][0];
        this.d = (double)this.matrix[1][1];
        if (this.a == 0.0D && this.b == 0.0D) {
            this.a = 0.001D;
            this.b = 0.001D;
        } else if (this.c == 0.0D && this.d == 0.0D) {
            this.c = 0.001D;
            this.d = 0.001D;
        }

        this.e = this.a + this.b;
        this.f = this.c + this.d;
        this.g = this.a + this.c;
        this.h = this.b + this.d;
        this.p = this.a * this.d - this.b * this.c;
        this.p /= Math.sqrt(this.e * this.f * this.g * this.h);
        return this.p >= 0.0D ? this.p : -this.p;
    }
}
