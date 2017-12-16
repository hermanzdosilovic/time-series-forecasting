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
}
