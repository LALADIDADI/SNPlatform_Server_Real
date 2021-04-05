package com.fan.boot.modules.dcheModule.mtif;

class Chi {
    private double sum1;
    private double sum2;
    private double sum3;
    private double sum;
    private double ex;
    private double result;

    Chi() {
    }

    public double cal(int[][] matrix, boolean[] arr) {
        this.sum1 = 0.0D;
        this.sum2 = 0.0D;
        this.result = 0.0D;

        int i;
        for(i = 0; i < matrix[0].length; ++i) {
            if (arr[i]) {
                this.sum1 += (double)matrix[0][i];
                this.sum2 += (double)matrix[1][i];
            }
        }

        this.sum = this.sum1 + this.sum2;

        for(i = 0; i < arr.length; ++i) {
            if (arr[i]) {
                this.sum3 = (double)(matrix[0][i] + matrix[1][i]);
                this.ex = this.sum3 * this.sum1 / this.sum;
                this.result += Math.pow((double)matrix[0][i] - this.ex, 2.0D) / this.ex;
                this.ex = this.sum3 * this.sum2 / this.sum;
                this.result += Math.pow((double)matrix[1][i] - this.ex, 2.0D) / this.ex;
            }
        }

        return this.result;
    }

    public double cal(int[][] _matrix, boolean[] _arr, int _size) {
        this.sum1 = 0.0D;
        this.sum2 = 0.0D;
        this.result = 0.0D;

        int i;
        for(i = 0; i < _size; ++i) {
            if (_arr[i]) {
                this.sum1 += (double)_matrix[0][i];
                this.sum2 += (double)_matrix[1][i];
            }
        }

        this.sum = this.sum1 + this.sum2;

        for(i = 0; i < _size; ++i) {
            if (_arr[i]) {
                this.sum3 = (double)(_matrix[0][i] + _matrix[1][i]);
                this.ex = this.sum3 * this.sum1 / this.sum;
                this.result += Math.pow((double)_matrix[0][i] - this.ex, 2.0D) / this.ex;
                this.ex = this.sum3 * this.sum2 / this.sum;
                this.result += Math.pow((double)_matrix[1][i] - this.ex, 2.0D) / this.ex;
            }
        }

        return this.result;
    }
}
