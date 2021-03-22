package com.fan.boot.modules.mtif;

public abstract class ContinuedFraction {
    private static final double DEFAULT_EPSILON = 1.0E-8D;

    protected ContinuedFraction() {
    }

    protected abstract double getA(int var1, double var2);

    protected abstract double getB(int var1, double var2);

    public double evaluate(double x) {
        return this.evaluate(x, 1.0E-8D, 2147483647);
    }

    public double evaluate(double x, double epsilon) {
        return this.evaluate(x, epsilon, 2147483647);
    }

    public double evaluate(double x, int maxIterations) {
        return this.evaluate(x, 1.0E-8D, maxIterations);
    }

    public double evaluate(double x, double epsilon, int maxIterations) {
        double small = 1.0E-50D;
        double hPrev = this.getA(0, x);
        if (Math.abs(hPrev) <= 1.0E-50D) {
            hPrev = 1.0E-50D;
        }

        int n = 1;
        double dPrev = 0.0D;
        double cPrev = hPrev;

        double hN;
        for(hN = hPrev; n < maxIterations; ++n) {
            double a = this.getA(n, x);
            double b = this.getB(n, x);
            double dN = a + b * dPrev;
            if (Math.abs(dN) <= 1.0E-50D) {
                dN = 1.0E-50D;
            }

            double cN = a + b / cPrev;
            if (Math.abs(cN) <= 1.0E-50D) {
                cN = 1.0E-50D;
            }

            dN = 1.0D / dN;
            double deltaN = cN * dN;
            hN = hPrev * deltaN;
            if (Math.abs(deltaN - 1.0D) < epsilon) {
                break;
            }

            dPrev = dN;
            cPrev = cN;
            hPrev = hN;
        }

        return hN;
    }
}
