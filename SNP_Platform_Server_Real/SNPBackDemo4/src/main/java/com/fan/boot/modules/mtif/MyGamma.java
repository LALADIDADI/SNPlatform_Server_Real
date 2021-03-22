package com.fan.boot.modules.mtif;

public class MyGamma {
    public static final double GAMMA = 0.5772156649015329D;
    public static final double LANCZOS_G = 4.7421875D;
    private static final double DEFAULT_EPSILON = 1.0E-14D;
    private static final double[] LANCZOS = new double[]{0.9999999999999971D, 57.15623566586292D, -59.59796035547549D, 14.136097974741746D, -0.4919138160976202D, 3.399464998481189E-5D, 4.652362892704858E-5D, -9.837447530487956E-5D, 1.580887032249125E-4D, -2.1026444172410488E-4D, 2.1743961811521265E-4D, -1.643181065367639E-4D, 8.441822398385275E-5D, -2.6190838401581408E-5D, 3.6899182659531625E-6D};
    private static final double HALF_LOG_2_PI = 0.5D * Math.log(6.283185307179586D);
    private static final double SQRT_TWO_PI = 2.5066282746310007D;
    private static final double C_LIMIT = 49.0D;
    private static final double S_LIMIT = 1.0E-5D;
    private static final double INV_GAMMA1P_M1_A0 = 6.116095104481416E-9D;
    private static final double INV_GAMMA1P_M1_A1 = 6.247308301164655E-9D;
    private static final double INV_GAMMA1P_M1_B1 = 0.203610414066807D;
    private static final double INV_GAMMA1P_M1_B2 = 0.026620534842894922D;
    private static final double INV_GAMMA1P_M1_B3 = 4.939449793824468E-4D;
    private static final double INV_GAMMA1P_M1_B4 = -8.514194324403149E-6D;
    private static final double INV_GAMMA1P_M1_B5 = -6.4304548177935305E-6D;
    private static final double INV_GAMMA1P_M1_B6 = 9.926418406727737E-7D;
    private static final double INV_GAMMA1P_M1_B7 = -6.077618957228252E-8D;
    private static final double INV_GAMMA1P_M1_B8 = 1.9575583661463974E-10D;
    private static final double INV_GAMMA1P_M1_P0 = 6.116095104481416E-9D;
    private static final double INV_GAMMA1P_M1_P1 = 6.8716741130671986E-9D;
    private static final double INV_GAMMA1P_M1_P2 = 6.820161668496171E-10D;
    private static final double INV_GAMMA1P_M1_P3 = 4.686843322948848E-11D;
    private static final double INV_GAMMA1P_M1_P4 = 1.5728330277104463E-12D;
    private static final double INV_GAMMA1P_M1_P5 = -1.2494415722763663E-13D;
    private static final double INV_GAMMA1P_M1_P6 = 4.343529937408594E-15D;
    private static final double INV_GAMMA1P_M1_Q1 = 0.3056961078365221D;
    private static final double INV_GAMMA1P_M1_Q2 = 0.054642130860422966D;
    private static final double INV_GAMMA1P_M1_Q3 = 0.004956830093825887D;
    private static final double INV_GAMMA1P_M1_Q4 = 2.6923694661863613E-4D;
    private static final double INV_GAMMA1P_M1_C = -0.42278433509846713D;
    private static final double INV_GAMMA1P_M1_C0 = 0.5772156649015329D;
    private static final double INV_GAMMA1P_M1_C1 = -0.6558780715202539D;
    private static final double INV_GAMMA1P_M1_C2 = -0.04200263503409524D;
    private static final double INV_GAMMA1P_M1_C3 = 0.16653861138229148D;
    private static final double INV_GAMMA1P_M1_C4 = -0.04219773455554433D;
    private static final double INV_GAMMA1P_M1_C5 = -0.009621971527876973D;
    private static final double INV_GAMMA1P_M1_C6 = 0.0072189432466631D;
    private static final double INV_GAMMA1P_M1_C7 = -0.0011651675918590652D;
    private static final double INV_GAMMA1P_M1_C8 = -2.1524167411495098E-4D;
    private static final double INV_GAMMA1P_M1_C9 = 1.280502823881162E-4D;
    private static final double INV_GAMMA1P_M1_C10 = -2.013485478078824E-5D;
    private static final double INV_GAMMA1P_M1_C11 = -1.2504934821426706E-6D;
    private static final double INV_GAMMA1P_M1_C12 = 1.133027231981696E-6D;
    private static final double INV_GAMMA1P_M1_C13 = -2.056338416977607E-7D;

    private MyGamma() {
    }

    public static double logGamma(double x) {
        double ret;
        if (!Double.isNaN(x) && !(x <= 0.0D)) {
            if (x < 0.5D) {
                return logGamma1p(x) - Math.log(x);
            }

            if (x <= 2.5D) {
                return logGamma1p(x - 0.5D - 0.5D);
            }

            if (x <= 8.0D) {
                int n = (int)Math.floor(x - 1.5D);
                double prod = 1.0D;

                for(int i = 1; i <= n; ++i) {
                    prod *= x - (double)i;
                }

                return logGamma1p(x - (double)(n + 1)) + Math.log(prod);
            }

            double sum = lanczos(x);
            double tmp = x + 4.7421875D + 0.5D;
            ret = (x + 0.5D) * Math.log(tmp) - tmp + HALF_LOG_2_PI + Math.log(sum / x);
        } else {
            ret = 0.0D / 0.0;
        }

        return ret;
    }

    public static double regularizedGammaP(double a, double x) {
        return regularizedGammaP(a, x, 1.0E-14D, 2147483647);
    }

    public static double regularizedGammaP(double a, double x, double epsilon, int maxIterations) {
        double ret = 0.0D;
        if (!Double.isNaN(a) && !Double.isNaN(x) && !(a <= 0.0D) && !(x < 0.0D)) {
            if (x == 0.0D) {
                ret = 0.0D;
            } else if (x >= a + 1.0D) {
                ret = 1.0D - regularizedGammaQ(a, x, epsilon, maxIterations);
            } else {
                double n = 0.0D;
                double an = 1.0D / a;

                double sum;
                for(sum = an; Math.abs(an / sum) > epsilon && n < (double)maxIterations && sum < 1.0D / 0.0; sum += an) {
                    ++n;
                    an *= x / (a + n);
                }

                if (!(n >= (double)maxIterations)) {
                    if (Double.isInfinite(sum)) {
                        ret = 1.0D;
                    } else {
                        ret = Math.exp(-x + a * Math.log(x) - logGamma(a)) * sum;
                    }
                }
            }
        } else {
            ret = 0.0D / 0.0;
        }

        return ret;
    }

    public static double regularizedGammaQ(double a, double x) {
        return regularizedGammaQ(a, x, 1.0E-14D, 2147483647);
    }

    public static double regularizedGammaQ(final double a, double x, double epsilon, int maxIterations) {
        double ret;
        if (!Double.isNaN(a) && !Double.isNaN(x) && !(a <= 0.0D) && !(x < 0.0D)) {
            if (x == 0.0D) {
                ret = 1.0D;
            } else if (x < a + 1.0D) {
                ret = 1.0D - regularizedGammaP(a, x, epsilon, maxIterations);
            } else {
                ContinuedFraction cf = new ContinuedFraction() {
                    protected double getA(int n, double x) {
                        return 2.0D * (double)n + 1.0D - a + x;
                    }

                    protected double getB(int n, double x) {
                        return (double)n * (a - (double)n);
                    }
                };
                ret = 1.0D / cf.evaluate(x, epsilon, maxIterations);
                double loggammaa = -logGamma(a);
                double summm = a * Math.log(x);
                summm = summm - x - loggammaa;
                summm = Math.exp(summm);
                double var10000 = summm * ret;
                ret = Math.exp(-x + a * Math.log(x) - logGamma(a)) * ret;
            }
        } else {
            ret = 0.0D / 0.0;
        }

        return ret;
    }

    public static double digamma(double x) {
        if (x > 0.0D && x <= 1.0E-5D) {
            return -0.5772156649015329D - 1.0D / x;
        } else if (x >= 49.0D) {
            double inv = 1.0D / (x * x);
            return Math.log(x) - 0.5D / x - inv * (0.08333333333333333D + inv * (0.008333333333333333D - inv / 252.0D));
        } else {
            return digamma(x + 1.0D) - 1.0D / x;
        }
    }

    public static double trigamma(double x) {
        if (x > 0.0D && x <= 1.0E-5D) {
            return 1.0D / (x * x);
        } else if (x >= 49.0D) {
            double inv = 1.0D / (x * x);
            return 1.0D / x + inv / 2.0D + inv / x * (0.16666666666666666D - inv * (0.03333333333333333D + inv / 42.0D));
        } else {
            return trigamma(x + 1.0D) + 1.0D / (x * x);
        }
    }

    public static double lanczos(double x) {
        double sum = 0.0D;

        for(int i = LANCZOS.length - 1; i > 0; --i) {
            sum += LANCZOS[i] / (x + (double)i);
        }

        return sum + LANCZOS[0];
    }

    public static double invGamma1pm1(double x) {
        double t = x <= 0.5D ? x : x - 0.5D - 0.5D;
        double ret;
        double a;
        double b;
        double c;
        if (t < 0.0D) {
            a = 6.116095104481416E-9D + t * 6.247308301164655E-9D;
            b = 1.9575583661463974E-10D;
            b = -6.077618957228252E-8D + t * b;
            b = 9.926418406727737E-7D + t * b;
            b = -6.4304548177935305E-6D + t * b;
            b = -8.514194324403149E-6D + t * b;
            b = 4.939449793824468E-4D + t * b;
            b = 0.026620534842894922D + t * b;
            b = 0.203610414066807D + t * b;
            b = 1.0D + t * b;
            c = -2.056338416977607E-7D + t * (a / b);
            c = 1.133027231981696E-6D + t * c;
            c = -1.2504934821426706E-6D + t * c;
            c = -2.013485478078824E-5D + t * c;
            c = 1.280502823881162E-4D + t * c;
            c = -2.1524167411495098E-4D + t * c;
            c = -0.0011651675918590652D + t * c;
            c = 0.0072189432466631D + t * c;
            c = -0.009621971527876973D + t * c;
            c = -0.04219773455554433D + t * c;
            c = 0.16653861138229148D + t * c;
            c = -0.04200263503409524D + t * c;
            c = -0.6558780715202539D + t * c;
            c = -0.42278433509846713D + t * c;
            if (x > 0.5D) {
                ret = t * c / x;
            } else {
                ret = x * (c + 0.5D + 0.5D);
            }
        } else {
            a = 4.343529937408594E-15D;
            a = -1.2494415722763663E-13D + t * a;
            a = 1.5728330277104463E-12D + t * a;
            a = 4.686843322948848E-11D + t * a;
            a = 6.820161668496171E-10D + t * a;
            a = 6.8716741130671986E-9D + t * a;
            a = 6.116095104481416E-9D + t * a;
            b = 2.6923694661863613E-4D;
            b = 0.004956830093825887D + t * b;
            b = 0.054642130860422966D + t * b;
            b = 0.3056961078365221D + t * b;
            b = 1.0D + t * b;
            c = -2.056338416977607E-7D + a / b * t;
            c = 1.133027231981696E-6D + t * c;
            c = -1.2504934821426706E-6D + t * c;
            c = -2.013485478078824E-5D + t * c;
            c = 1.280502823881162E-4D + t * c;
            c = -2.1524167411495098E-4D + t * c;
            c = -0.0011651675918590652D + t * c;
            c = 0.0072189432466631D + t * c;
            c = -0.009621971527876973D + t * c;
            c = -0.04219773455554433D + t * c;
            c = 0.16653861138229148D + t * c;
            c = -0.04200263503409524D + t * c;
            c = -0.6558780715202539D + t * c;
            c = 0.5772156649015329D + t * c;
            if (x > 0.5D) {
                ret = t / x * (c - 0.5D - 0.5D);
            } else {
                ret = x * c;
            }
        }

        return ret;
    }

    public static double logGamma1p(double x) {
        return -Math.log1p(invGamma1pm1(x));
    }

    public static double gamma(double x) {
        if (x == Math.rint(x) && x <= 0.0D) {
            return 0.0D / 0.0;
        } else {
            double absX = Math.abs(x);
            double ret;
            double prod;
            double t;
            if (absX <= 20.0D) {
                if (x >= 1.0D) {
                    prod = 1.0D;

                    for(t = x; t > 2.5D; prod *= t) {
                        --t;
                    }

                    ret = prod / (1.0D + invGamma1pm1(t - 1.0D));
                } else {
                    prod = x;

                    for(t = x; t < -0.5D; prod *= t) {
                        ++t;
                    }

                    ret = 1.0D / (prod * (1.0D + invGamma1pm1(t)));
                }
            } else {
                prod = absX + 4.7421875D + 0.5D;
                t = 2.5066282746310007D / x * Math.pow(prod, absX + 0.5D) * Math.exp(-prod) * lanczos(absX);
                if (x > 0.0D) {
                    ret = t;
                } else {
                    ret = -3.141592653589793D / (x * Math.sin(3.141592653589793D * x) * t);
                }
            }

            return ret;
        }
    }
}
