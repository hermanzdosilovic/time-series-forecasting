package hr.fer.zemris.project.forecasting.models.arma;

import Jama.Matrix;


//TODO provjeri kako implementirati vrijednost funkcije, gradijent i hessian
public class Correction implements IHFunction {
    private final double[] a;
    private final double[] u;
    private final double[] v;
    private final int p;
    private final int q;

    public Correction(double[] a, double[] u, double[] v, int p, int q) {
        this.a = a;
        this.u = u;
        this.v = v;
        this.p = p;
        this.q = q;
    }

    @Override public int getNumberOfVariables() {
        return p + q;
    }

    @Override public double getValueOfFunction(Matrix vector) {

        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double error = a[i];

            for (int j = 1; j <= p; j++) {
                if (i - j >= 0)
                    error -= vector.get(j - 1, 0) * u[i - j];
            }

            for (int j = 1; j <= q; j++) {
                if (i - j >= 0)
                    error -= vector.get(j - 1 + p, 0) * v[i - j];
            }

            sum += error * error;
        }
        return sum;
    }

    @Override public Matrix getGradientVector(Matrix vector) {

        Matrix grad = new Matrix(vector.getRowDimension(), 1);
        double[] error = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            error[i] = a[i];

            for (int j = 0; j < p; j++) {
                if (i - j >= 0)
                    error[i] -= vector.get(j, 0) * u[i - j];
            }

            for (int j = 0; j < q; j++) {
                if (i - j >= 0)
                    error[i] -= vector.get(j + p, 0) * v[i - j];
            }
        }

        for (int i = 0; i < p; i++) {
            double diff = 0;
            for (int j = 0; j < a.length; j++) {
                if (j - i >= 0)
                    diff -= 2 * u[j - i] * error[j];

            }
            grad.set(i, 0, diff);
        }

        for (int i = 0; i < q; i++) {
            double diff = 0;
            for (int j = 0; j < a.length; j++) {
                if (j - i >= 0)
                    diff -= 2 * v[j - i] * error[j];

            }
            grad.set(i + p, 0, diff);
        }
        return grad;
    }

    @Override public Matrix getHessianMatrix(Matrix vector) {

        Matrix hess = new Matrix(this.getNumberOfVariables(), this.getNumberOfVariables());

        for (int i = 1; i <= p; i++) {

            for (int j = 1; j <= p; j++) {

                double diff = 0;

                for (int row = 0; row < a.length; row++) {

                    if (row - i >= 0 && row - j >= 0) {
                        diff += 2 * u[row - i] * u[row - j];
                    }

                }

                hess.set(i - 1, j - 1, diff);
            }
        }

        for (int i = 1; i <= p; i++) {

            for (int j = 1; j <= q; j++) {

                double diff = 0;

                for (int row = 0; row < a.length; row++) {

                    if (row - i >= 0 && row - j >= 0) {
                        diff += 2 * u[row - i] * v[row - j];
                    }

                }

                hess.set(i - 1, j - 1 + p, diff);
            }
        }

        for (int i = 1; i <= q; i++) {

            for (int j = 1; j <= p; j++) {

                double diff = 0;

                for (int row = 0; row < a.length; row++) {

                    if (row - i >= 0 && row - j >= 0) {
                        diff += 2 * v[row - i] * u[row - j];
                    }

                }

                hess.set(i - 1 + p, j - 1, diff);
            }
        }

        for (int i = 0; i < q; i++) {

            for (int j = 0; j < q; j++) {

                double diff = 0;

                for (int row = 0; row < a.length; row++) {

                    if (row - i >= 0 && row - j >= 0) {
                        diff += 2 * v[row - i] * v[row - j];
                    }

                }

                hess.set(i + p, j + p, diff);
            }
        }

        return hess;
    }

}
