package hr.fer.zemris.project.forecasting.models.arma;

import Jama.Matrix;
import Jama.QRDecomposition;
import hr.fer.zemris.project.forecasting.models.AModel;
import hr.fer.zemris.project.forecasting.util.ArraysUtil;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.complex.Complex;

import javax.sound.midi.SysexMessage;
import java.util.Arrays;

/**
 * DEGENERATE CLASS, ONLY MA COEFFICIENTS SHOULD BE INPUT!
 * -- rješenje nije u skladu s R-om jer on koristi intercept, a ja mean -> objasniti Čupiću
 */
public class ARMA extends AModel {

    private static final double STARTING_VALUE_MIN = -1;
    private static final double STARTING_VALUE_MAX = 1;
    private static final int CONVERGENCE_ITERS = 1;
    private static final int MAX_REC_ITER = 10_000;
    private static double STARTING_VALUE_STEP;
    private int p;
    private int q;
    /**
     * Coefficients of the ARMA model. The first p are AR coefficients, while the
     * rest are MA coefficients.
     */
    private double[] betaCoeffs;
    private double[] startingValues;
    /**
     * White noise of the ARMA model.
     */
    private Dataset a;
    private Dataset dataset;

    private boolean differenced;

    public ARMA(int p, int q, double[] dataset, boolean differenced){

        this.p = p;
        this.q = q;
        this.dataset = new Dataset(dataset, differenced);
        this.betaCoeffs = new double[this.p + this.q];
        this.startingValues = new double[this.p + this.q];
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

    public Dataset getDataset(){
        return dataset;
    }

    private void fitModel(int numberOfIters){
        findStartingValues();
        for (int i = 0; i < numberOfIters; i++) {
            betaCoeffs = adjustBetas();
        }
        if(!invertibleCheck(betaCoeffs)) a = new Dataset(findA(betaCoeffs), differenced);
        else a = new Dataset(findA(betaCoeffs),true);
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
        startingValues = Arrays.copyOf(betaCoeffs, betaCoeffs.length);
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

    //provjeriti je li problem u računanju a ili negdje drugdje
    public double[] findA(double[] coeffs) {

        double[] newDataset = new double[dataset.getDataset().length];
        for (int i = p; i < newDataset.length; i++) {
            newDataset[i] = dataset.getDataset()[i] + dataset.getMean();
        }
        Dataset next = new Dataset(newDataset, differenced);
        double[] a = new double[newDataset.length];
        for (int i = p; i < a.length; i++) {
            a[i] = next.getDataset()[i];
            for (int j = 1; j <= p; j++) {
                if (i - j >= 0) {
                    a[i] -= coeffs[j - 1] * next.getDataset()[i - j];
                }
            }

            for (int j = 1; j <= q; j++) {
                if (i - j >= 0) {
                    a[i] += coeffs[p + j-1] * a[i - j];
                }
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

        Matrix oldBetaCoeffs = new Matrix(betaCoeffs, betaCoeffs.length);

        Matrix y = new Matrix(a, a.length);
        Matrix X = new Matrix(a.length, betaCoeffs.length);

        for(int i = 0; i < X.getRowDimension(); i++){
            for(int j = 0; j < X.getColumnDimension(); j++){
                X.set(i, j, i - j > 0 ? v[i-j-1] : 0);
            }
        }

        Matrix betaAdditions = (X.transpose().times(X)).inverse().times(X.transpose()).times(y);
        oldBetaCoeffs.plusEquals(betaAdditions);
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
    @Override public double computeNextValue() {

        double forecastedValue = dataset.getMean();

        if(invertibleCheck(betaCoeffs)){
            for (int i = 0; i < p; i++) {
                forecastedValue +=
                    betaCoeffs[i] * dataset.getDataset()[dataset.getDataset().length - 1 - i];
            }
        }
        else{
            for (int i = 0; i < p; i++) {
                forecastedValue +=
                        startingValues[i] * dataset.getDataset()[dataset.getDataset().length - 1 - i];
            }
        }

        forecastedValue += a.getMean();

        if(invertibleCheck(betaCoeffs)) {
            for (int j = 0; j < q; j++) {
                forecastedValue -= betaCoeffs[p + j] * a.getDataset()[a.getDataset().length - 1 - j];
            }
        }
        else{
            for (int j = 0; j < q; j++) {
                forecastedValue -= startingValues[p + j] * a.getDataset()[a.getDataset().length - 1 - j];
            }
        }

        return forecastedValue;
    }

    @Override public double[] computeNextValues(int numberOfForecasts) {
        Dataset datasetBackup = new Dataset(dataset.getDatasetBackup(), differenced);
        Dataset residualBackup = new Dataset(a.getDatasetBackup(), false);

        double[] results = new double[numberOfForecasts];

        for (int i = 0; i < numberOfForecasts; i++) {
            double oneForecast = computeNextValue();

            results[i] = oneForecast;

            dataset.addSample(oneForecast);
            if(invertibleCheck(betaCoeffs))
                a = new Dataset(findA(betaCoeffs), false);
            else a = new Dataset(findA(startingValues), false);
        }

        dataset = datasetBackup;
        a = residualBackup;

        return results;
    }

    @Override
    public double[] testDataset() {
        double[] test = new double[dataset.getDataset().length];
        boolean isInvertible = invertibleCheck(betaCoeffs);
        for(int i = 0; i < q; i++){
            test[i] = dataset.getDataset()[i] + dataset.getMean();
        }

        for(int currentIndex = q; currentIndex < test.length; currentIndex++){
            for(int i = 0; i < q; i++){
                if(isInvertible) test[currentIndex] -= betaCoeffs[p + i] * a.getDataset()[currentIndex - 1 - i];
                else test[currentIndex] -= startingValues[p + i] * a.getDataset()[currentIndex - 1 - i];
            }
            test[currentIndex] += a.getMean();
            test[currentIndex] += dataset.getMean();
        }

        return test;
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


        public static boolean invertibleCheck(double...coeff){

            double[] a = new double[coeff.length + 1];
            a[0] = 1;
            for(int i = 1; i < a.length; i++){
                a[i] = coeff[i-1];
            }

            LaguerreSolver p = new LaguerreSolver();

            Complex[] solutions = p.solveAllComplex(a, 0);

            boolean invertible = true;

            for (Complex complex : solutions) {
                if(complex.abs() < 1){
                    invertible = false;
                }
            }

            return invertible;
        }

}
