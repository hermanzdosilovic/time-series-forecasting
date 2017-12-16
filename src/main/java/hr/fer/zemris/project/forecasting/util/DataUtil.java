package hr.fer.zemris.project.forecasting.util;

import org.surus.math.AugmentedDickeyFuller;

import java.util.Arrays;
import java.util.List;

public class DataUtil {

    public static Pair<double[], double[]> splitDataset(
        double[] dataset,
        double trainPercentage
    ) {
        int trainSize = (int) (dataset.length * trainPercentage);
        return new Pair<>(
            Arrays.copyOfRange(dataset, 0, trainSize),
            Arrays.copyOfRange(dataset, trainSize, dataset.length)
        );
    }

    public static double getMean(double[] data) {
        double mean = 0;
        for (double d : data) {
            mean += d;
        }
        return mean / data.length;
    }

    public static double getMean(List<Double> data) {
        double mean = 0;
        for (Double d : data) {
            mean += d;
        }
        return mean / data.size();
    }
}
