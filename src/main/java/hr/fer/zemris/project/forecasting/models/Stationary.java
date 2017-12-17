package hr.fer.zemris.project.forecasting.models;

import hr.fer.zemris.project.forecasting.util.ArraysUtil;
import org.surus.math.AugmentedDickeyFuller;

import java.util.ArrayList;
import java.util.List;

public class Stationary {

    private List<Double> firstValues;

    private double[] dataset;

    private int order;

    public Stationary(double[] dataset) {
        this.dataset = dataset;
        order = 0;
        firstValues = new ArrayList<>();
        stationarize();
    }

    public Stationary(List<Double> dataset) {
        this(ArraysUtil.toPrimitiveArray(dataset));
    }

    private void stationarize() {
        while (!isStationary(dataset)) {
            order++;
            firstValues.add(0, dataset[0]);
            dataset = differentiate(dataset);
        }
    }

    public double[] getDataset() {
        return dataset;
    }

    public List<Double> getDatasetAsList() {
        return ArraysUtil.toList(dataset);
    }

    public int getOrder() {
        return order;
    }

    public double[] accumulate(double[] data) {
        double[] tmp = data.clone();
        for (int i = 0; i < order; i++) {
            tmp = computeOneBefore(firstValues.get(i), tmp);
        }
        return tmp;
    }

    public double accumulateAndReturnLast(double[] data) {
        double[] tmp = accumulate(data);
        return tmp[tmp.length - 1];
    }

    public double accumulateAndReturnLast(List<Double> data) {
        List<Double> tmp = accumulate(data);
        return tmp.get(tmp.size() - 1);
    }

    public List<Double> accumulate(List<Double> data) {
        return ArraysUtil.toList(accumulate(ArraysUtil.toPrimitiveArray(data)));
    }

    private double[] computeOneBefore(double first, double[] tmp) {
        double[] result = new double[tmp.length + 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = first + sumOfAllBefore(tmp, i);
        }
        return result;
    }

    private double sumOfAllBefore(double[] tmp, int index) {
        double sum = 0.0;
        for (int i = 0; i < index; i++) {
            sum += tmp[i];
        }
        return sum;
    }

    public static double[] stationarize(double[] data) {
        double[] result = data.clone();
        while (!isStationary(result)) {
            result = differentiate(result);
        }
        return result;
    }

    public static List<Double> stationarize(List<Double> data) {
        return ArraysUtil.toList(stationarize(ArraysUtil.toPrimitiveArray(data)));
    }

    public static boolean isStationary(double[] data) {
        int                   lag = (int) Math.floor(4 * Math.pow(data.length / 100.0, 2.0 / 9));
        AugmentedDickeyFuller adf = new AugmentedDickeyFuller(data, data.length - lag);
        return !adf.isNeedsDiff();
    }

    public static boolean isStationary(List<Double> data) {
        return isStationary(ArraysUtil.toPrimitiveArray(data));
    }

    public static double[] differentiate(double[] data) {
        System.out.println("Differencing data given.");
        double[] result = new double[data.length - 1];
        for (int i = 1; i < data.length; i++) {
            result[i - 1] = data[i] - data[i - 1];
        }
        return result;
    }

    public static List<Double> differentiate(List<Double> data) {
        return ArraysUtil.toList(differentiate(ArraysUtil.toPrimitiveArray(data)));
    }
}
