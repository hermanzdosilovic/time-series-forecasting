package hr.fer.zemris.project.forecasting.models;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

import java.util.ArrayList;
import java.util.List;

public class AR {

    /* Order of AR model */
    private int p;

    private List<Double> data;

    public AR(int p, List<Double> data) {
        this.p = p;
        this.data = new ArrayList<>(data);
    }

    public double computeNextValue() {
        double[] coefficients = YuleWalker(data, p);
        double nextValue = computeValue(coefficients, data.size());

        double[] error = computeErrorArray(coefficients);
        double mean = computeMean(error);
        nextValue += mean;

        data.add(nextValue);
        return nextValue;
    }

    public List<Double> computeNextValues(int number) {
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            result.add(computeNextValue());
        }
        return result;
    }

    public List<Double> getData() {
        return data;
    }

    /**
     * Computes coefficients for AR model.
     *
     * @param data dataset used
     * @param p    order of AR model
     * @return coefficients for AR model
     */
    public static double[] YuleWalker(List<Double> data, int p) {
        // autocovariance matrix and vector computed
        double[][] autocovarianceMatrixArray = new double[p][p];
        double[] autocovarianceArray = new double[p];
        for (int lag = 1; lag <= p; lag++) {
            autocovarianceArray[lag - 1] = computeAutocorellation(data, lag);
            for (int k = 1; k <= p; k++) {
                autocovarianceMatrixArray[lag - 1][k - 1] =
                    computeAutocorellation(data, Math.abs(lag - k));
            }
        }

        RealVector autocovarianceVector = MatrixUtils.createRealVector(autocovarianceArray);
        RealMatrix autocovarianceMatrix = MatrixUtils.createRealMatrix(autocovarianceMatrixArray);

        RealVector coefficients = new LUDecomposition(autocovarianceMatrix).getSolver().getInverse()
            .operate(autocovarianceVector);
        return coefficients.toArray();
    }

    public static double computeAutocorellation(List<Double> data, int index) {
        return estimateAutocovariance(data, index) / estimateAutocovariance(data, 0);
    }

    private double[] computeErrorArray(double[] coefficients) {
        int order = coefficients.length;
        double[] error = new double[data.size() - order];

        for (int i = 0; i < error.length; i++) {
            double coefficientValue = computeValue(coefficients, i + order);
            double realValue = data.get(i + order);
            error[i] = realValue - coefficientValue;
        }
        return error;
    }

    private double computeMean(double[] array) {
        return new Mean().evaluate(array);
    }

    private double computeValue(double[] coefficients, int index) {
        double nextValue = 0.0;
        for (int i = 0; i < p; i++) {
            nextValue += coefficients[i] * data.get(index - 1 - i);
        }
        return nextValue;
    }

    public static double estimateAutocovariance(List<Double> data, int index) {
        int N = data.size();
        double dataMean = computeMean(data);
        double sum = 0.0;

        for (int t = 0; t < N - index; t++) {
            sum += (data.get(t) - dataMean) * (data.get(t + index) - dataMean);
        }
        return sum / N;
    }

    public static double computeMean(List<Double> data) {
        int N = data.size();
        double sum = 0.0;

        for (int i = 0; i < N; i++) {
            sum += data.get(i);
        }
        return sum / N;
    }
}
