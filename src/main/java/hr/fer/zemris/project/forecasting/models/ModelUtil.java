package hr.fer.zemris.project.forecasting.models;

import org.surus.math.AugmentedDickeyFuller;

import java.util.ArrayList;
import java.util.List;

public class ModelUtil {

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
        System.out.println("Differencing data given.");
        double[] result = new double[data.length - 1];
        for (int i = 1; i < data.length; i++) {
            result[i - 1] = data[i] - data[i - 1];
        }
        return result;
    }
}
