package hr.fer.zemris.project.forecasting.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public final class ArraysUtil {

    public static double[] toPrimitiveArray(List<Double> list) {
        return list.stream().mapToDouble(d -> d).toArray();
    }

    public static List<Double> toList(double[] array) {
        return DoubleStream.of(array).boxed().collect(Collectors.toList());
    }

    public static double max(double[] array) {
        Double max = null;
        for (double number : array) {
            if (max == null || max < number) {
                max = number;
            }
        }
        return max;
    }

    public static double min(double[] array) {
        Double min = null;
        for (double number : array) {
            if (min == null || min > number) {
                min = number;
            }
        }
        return min;
    }
}
