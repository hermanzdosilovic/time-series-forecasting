package hr.fer.zemris.project.forecasting.util;

import org.surus.math.AugmentedDickeyFuller;

import java.util.Arrays;

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


    public static double[] stationarize(double[] data) {
        while (!isStationary(data)) {
            data = differentiate(data);
        }
        return data;
    }

    public static boolean isStationary(double[] data) {
        AugmentedDickeyFuller adf = new AugmentedDickeyFuller(data, data.length - 2);
        return !adf.isNeedsDiff();
    }

    public static double[] differentiate(double[] data) {
        double[] result = new double[data.length - 1];

        for (int i = 1; i < data.length; i++) {
            result[i - 1] = data[i] - data[i - 1];
        }

        return result;
    }
}
