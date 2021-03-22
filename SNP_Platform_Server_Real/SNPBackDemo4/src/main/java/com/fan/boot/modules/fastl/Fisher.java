package com.fan.boot.modules.fastl;

public class Fisher {
    private int a;
    private int b;
    private int c;
    private int d;
    private double p;
    private int j;
    private int i;
    private int l;
    private int[][] matrix = new int[2][2];

    public Fisher() {
    }

    public void setVectors(int[][] _matrix, int _c1, int _c2) {
        for(int i = 0; i < 2; ++i) {
            this.matrix[i][0] = _matrix[i][_c1];
            this.matrix[i][1] = _matrix[i][_c2];
        }

    }

    public double twoSide() {
        double p = this.fisher();
        double current = p;
        if (this.a > this.c) {
            if (this.c == 0 || this.b == 0) {
                return p;
            }

            for(this.i = this.c - 1; this.i >= 0; --this.i) {
                current = current * (double)this.b * (double)this.c / (double)((this.a + 1) * (this.d + 1));
                p += current;
                ++this.a;
                --this.c;
                --this.b;
                ++this.d;
                if (this.c == 0 || this.b == 0) {
                    break;
                }
            }
        } else {
            if (this.a == 0 || this.d == 0) {
                return p;
            }

            for(this.i = this.a - 1; this.i >= 0; --this.i) {
                current = current * (double)this.a * (double)this.d / (double)((this.c + 1) * (this.b + 1));
                p += current;
                --this.a;
                ++this.c;
                ++this.b;
                --this.d;
                if (this.a == 0 || this.d == 0) {
                    break;
                }
            }
        }

        return p;
    }

    public double fisher() {
        this.p = 1.0D;
        this.a = this.matrix[0][0] + this.matrix[0][1];
        this.b = this.matrix[1][0] + this.matrix[1][1];
        this.j = this.a + this.b;
        this.c = this.matrix[0][0] + this.matrix[1][0];
        this.d = this.matrix[0][1] + this.matrix[1][1];
        if (this.a <= this.b && this.a <= this.c && this.a <= this.d) {
            this.c = this.matrix[0][1];
            this.d = this.matrix[0][0];
            this.a = this.matrix[1][1];
            this.b = this.matrix[1][0];
        } else if (this.b <= this.a && this.b <= this.c && this.b <= this.d) {
            this.c = this.matrix[1][0];
            this.d = this.matrix[1][1];
            this.a = this.matrix[0][0];
            this.b = this.matrix[0][1];
        } else if (this.c <= this.a && this.c <= this.b && this.c <= this.d) {
            this.c = this.matrix[0][0];
            this.d = this.matrix[1][0];
            this.a = this.matrix[0][1];
            this.b = this.matrix[1][1];
        } else {
            this.c = this.matrix[1][1];
            this.d = this.matrix[0][1];
            this.a = this.matrix[1][0];
            this.b = this.matrix[0][0];
        }

        this.l = this.d;

        for(this.i = this.a + 1; this.i <= this.a + this.c; ++this.i) {
            this.p *= (double)this.i;
            if (this.p > 1.0D) {
                this.p /= (double)this.j;
                --this.j;
            }
        }

        for(this.i = this.c + 1; this.i <= this.c + this.d; ++this.i) {
            this.p *= (double)this.i;
            if (this.p > 1.0D) {
                this.p /= (double)this.j;
                --this.j;
            }
        }

        for(this.i = this.b + 1; this.i <= this.b + this.d; ++this.i) {
            this.p *= (double)this.i;
            if (this.p > 1.0D && this.j > this.a + this.b) {
                this.p /= (double)this.j;
                --this.j;
            }

            if (this.p > 1.0D) {
                this.p /= (double)this.l;
                --this.l;
            }
        }

        while(this.j > this.a + this.b) {
            this.p /= (double)this.j;
            --this.j;
        }

        while(this.l > 1) {
            this.p /= (double)this.l;
            --this.l;
        }

        return this.p;
    }

    public static void main(String[] args) {
        int[][] matrix = new int[][]{{5, 5}, {2, 0}};
        Fisher fish = new Fisher();
        fish.setVectors(matrix, 0, 1);
        System.out.println(fish.twoSide());
    }
}
