package hr.fer.zemris.projekt.predictions.models;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.List;

public class AR {

    /* Order of AR model */
    private int p;

    private List<Double> data;

    public AR(int p, List<Double> data) {
        this.p = p;
        this.data = new ArrayList<>();
        this.data.addAll(data);
    }

    public double computeNextValue() {
        double nextValue = 0.0;
        double[] coefficients = YuleWalker(data, p);
        for (int i = 0; i < p; i++) {
            nextValue += coefficients[i] * data.get(data.size() - 1 - i);
        }

        // TODO error
        double error = 0.0;
        nextValue += error;

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
                autocovarianceMatrixArray[lag - 1][k - 1] = computeAutocorellation(data, lag - k);
            }
        }

        RealVector autocovarianceVector = MatrixUtils.createRealVector(autocovarianceArray);
        RealMatrix autocovarianceMatrix = MatrixUtils.createRealMatrix(autocovarianceMatrixArray);
        //autocovarianceVector
        // tu bi zapravo trebalo ić da je a(transponirano) = -m(-1)*v(t)
        // tu ide ili operate ili preMultiply
        // TODO

        RealVector coefficients = new LUDecomposition(autocovarianceMatrix).getSolver().getInverse()
                .operate(autocovarianceVector)
                .mapMultiply(-1);
        return coefficients.toArray();
    }

    public static double computeAutocorellation(List<Double> data, int index) {
        return estimateAutocovariance(data, index) / estimateAutocovariance(data, 0);
    }


    public static double estimateAutocovariance(List<Double> data, int index) {
        int N = data.size();
        double dataMean = computeMean(data);
        double sum = 0.0;
        // TODO provjeri  za ovo ok kud kreće t

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
