package hr.fer.zemris.projekt.predictions.models;

import org.surus.math.AugmentedDickeyFuller;

import java.util.ArrayList;
import java.util.List;

public class ModelUtil {

    public static List<Double> stationarize(List<Double> data) {
        List<Double> result = new ArrayList<>();
        result.addAll(data);

        while (!checkIfStationary(result)) {
            result = differentiate(result);
        }
        return result;
    }


    public static boolean checkIfStationary(List<Double> data) {
        // creating a primitive array
        double[] pom = new double[data.size()];
        for (int i = 0; i < pom.length; i++) {
            pom[i] = data.get(i);
        }

        AugmentedDickeyFuller adf = new AugmentedDickeyFuller(pom, pom.length - 2);
        return !adf.isNeedsDiff();
    }


    public static List<Double> differentiate(List<Double> data) {
        System.out.println("Differencing data given.");
        List<Double> result = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            result.add(data.get(i) - data.get(i - 1));
        }
        return result;
    }


}
