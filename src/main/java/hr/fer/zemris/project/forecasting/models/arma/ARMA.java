package hr.fer.zemris.project.forecasting.models.arma;

import Jama.Matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ARMA {

    private static final double STARTING_VALUE_MIN = -1;
    private static final double STARTING_VALUE_MAX = 1;
    private static final int CONVERGENCE_ITERS = 100;
    private static final int MAX_REC_ITER = 10_000;
    private static double STARTING_VALUE_STEP;
    private final int p;
    private final int q;
    /**
     * Coefficients of the ARMA model. The first p are AR coefficients, while the
     * rest are MA coefficients.
     */
    private double[] betaCoeffs;
    /**
     * White noise of the ARMA model.
     */
    private Dataset a;
    private Dataset dataset;

    public ARMA(int p, int q, double[] dataset, boolean differentiated) {

        this.p = p;
        this.q = q;
        this.dataset = new Dataset(dataset);
        this.betaCoeffs = new double[this.p + this.q];
        if (p == 0 && q == 0)
            return;
    /*
		 * Osigurava da ce u prolazu za početne vrijednosti biti MAX_REC_ITER iteracija.
		 */
        STARTING_VALUE_STEP =
            (STARTING_VALUE_MAX - STARTING_VALUE_MIN) / Math.pow(MAX_REC_ITER, 1. / (p + q));

        NumOptAlgorithms.setInitialVector(new double[] {0, 0});
        fitModel(CONVERGENCE_ITERS);
    }

    private void fitModel(int numberOfIters) {
        findStartingValues();
        for (int i = 0; i < numberOfIters; i++) {
            betaCoeffs = adjustBetas();
        }
        a = new Dataset(findA(betaCoeffs));
    }

    private void findStartingValues() {

        double[] currentCoeffs = new double[betaCoeffs.length];
        Arrays.fill(currentCoeffs, STARTING_VALUE_MIN);
        double[] currentBestCoeffs = currentCoeffs.clone();

        Double currentBestResidualSquareSum =
            calculateSquareSumOfResiduals(findA(currentBestCoeffs));
        RecursionParameters rec;
        rec = iterate(
            new RecursionParameters(currentBestCoeffs, currentBestResidualSquareSum, currentCoeffs),
            0);

        betaCoeffs = rec.currentBestCoeffs;
    }

    private RecursionParameters iterate(RecursionParameters rec, int i) {

        for (
            rec.currentCoeffs[i] = STARTING_VALUE_MIN;
            rec.currentCoeffs[i] <= STARTING_VALUE_MAX;
            rec.currentCoeffs[i] += STARTING_VALUE_STEP) {
            if (i == rec.currentCoeffs.length - 1) {
                double currentSquareSum = calculateSquareSumOfResiduals(findA(rec.currentCoeffs));
                if (currentSquareSum < rec.currentBestResidualSquareSum) {
                    rec.currentBestCoeffs = rec.currentCoeffs.clone();
                    rec.currentBestResidualSquareSum = currentSquareSum;
                }
            } else
                rec = iterate(rec, i + 1);
        }

        return rec;
    }

    // Uvijek računa sa dataset - mean

    private double[] findA(double[] coeffs) {

        double[] newDataset = new double[dataset.getDataset().length];
        for (int i = 0; i < newDataset.length; i++) {
            newDataset[i] = dataset.getDataset()[i];
        }

        double[] a = new double[newDataset.length];
        for (int i = p; i < a.length; i++) {
            a[i] = newDataset[i];
            for (int j = 1; j <= p; j++) {
                if (i - j >= 0)
                    a[i] -= coeffs[j - 1] * newDataset[i - j];

            }

            for (int j = 1; j <= q; j++) {
                if (i - j >= 0)
                    a[i] += coeffs[p + j - 1] * a[i - j];
            }
        }

        return a;
    }

    private double calculateSquareSumOfResiduals(double[] a) {
        double sum = 0;
        for (double d : a) {
            sum += d * d;
        }
        return sum;
    }

    private double[] adjustBetas() {
        double[] a = findA(betaCoeffs);
        double[] ARcoeffs = Arrays.copyOfRange(betaCoeffs, 0, p);
        double[] MAcoeffs = Arrays.copyOfRange(betaCoeffs, p, p + q);

        double[] u = findU(a, ARcoeffs);
        double[] v = findV(a, MAcoeffs);

        IHFunction f = new Correction(a, u, v, p, q);

        NumOptAlgorithms.setInitialVector(new double[betaCoeffs.length]);

        Matrix oldBetaCoeffs = new Matrix(betaCoeffs, betaCoeffs.length);
        oldBetaCoeffs.plusEquals(NumOptAlgorithms.newtonMethod(f, 10));
        return oldBetaCoeffs.getRowPackedCopy();
    }

    private double[] findU(double[] a, double[] coeffs) {

        if (p == 0)
            return null;

        double[] u = new double[a.length];

        for (int i = 0; i < u.length; i++) {
            u[i] = a[i];
            for (int j = 1; j <= this.p; j++) {
                if (i - j >= 0)
                    u[i] += coeffs[j - 1] * u[i - j];
            }
        }

        return u;
    }

    private double[] findV(double[] a, double[] coeffs) {

        if (q == 0)
            return null;

        double[] v = new double[a.length];

        for (int i = 0; i < v.length; i++) {
            v[i] = -a[i];
            for (int j = 1; j <= this.q; j++) {
                if (i - j >= 0)
                    v[i] += coeffs[j - 1] * v[i - j];
            }
        }

        return v;
    }

    public double[] getCoeffs() {
        return betaCoeffs;
    }

    public Dataset getResiduals() {
        return a;
    }

    /*
     * treba pogledati racuna li r svaki put novi mean ili za nepoznate vrijednosti
     * samo koristi mean i nista ne racuna
     */
    public double forecastOneValue() {

        double forecastedValue = dataset.getMean();

        for (int i = 0; i < p; i++) {
            forecastedValue +=
                betaCoeffs[i] * dataset.getDataset()[dataset.getDataset().length - 1 - i];
        }

        forecastedValue += a.getMean();

        for (int j = 0; j < q; j++) {
            forecastedValue -= betaCoeffs[p + j] * a.getDataset()[a.getDataset().length - 1 - j];
        }

        return forecastedValue;
    }

    public List<Double> forecast(int numberOfForecasts) {
        Dataset datasetBackup = new Dataset(dataset.getDatasetBackup());
        Dataset residualBackup = new Dataset(a.getDatasetBackup());

        List<Double> results = new ArrayList<>(numberOfForecasts);

        for (int i = 0; i < numberOfForecasts; i++) {
            double oneForecast = forecastOneValue();

            results.add(oneForecast);

            dataset.addSample(oneForecast);
            a = new Dataset(findA(betaCoeffs));
        }

        dataset = datasetBackup;
        a = residualBackup;

        return results;
    }

    private class RecursionParameters {

        double[] currentBestCoeffs;
        double currentBestResidualSquareSum;
        double[] currentCoeffs;

        public RecursionParameters(double[] currentBestCoeffs, double currentBestResidualSquareSum,
            double[] currentCoeffs) {
            this.currentBestCoeffs = currentBestCoeffs;
            this.currentBestResidualSquareSum = currentBestResidualSquareSum;
            this.currentCoeffs = currentCoeffs;
        }

    }
}
