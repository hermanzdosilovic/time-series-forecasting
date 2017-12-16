package hr.fer.zemris.project.forecasting.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public final class DataReaderUtil {

    private static final int    LAST_VALUE_INDICATOR = -1;
    private static final String DEFAULT_DELIMITER    = ",";

    public static double[] readDataset(Path path, Integer indexOfValue, String delimiter) throws IOException {
        List<String> dataset = Files.readAllLines(path);
        double[]     result  = new double[dataset.size()];

        for (int i = 0, n = dataset.size(); i < n; ++i) {
            String[] data  = dataset.get(i).trim().split(String.format("[%s]", delimiter));
            int      index = (indexOfValue == LAST_VALUE_INDICATOR ? data.length - 1 : indexOfValue);

            result[i] = Double.parseDouble(data[index].trim());
        }

        return result;
    }

    public static double[] readDataset(Path path, Integer indexOfValue) throws IOException {
        return readDataset(path, indexOfValue, DEFAULT_DELIMITER);
    }

    public static double[] readDataset(Path path, String delimiter) throws IOException {
        return readDataset(path, LAST_VALUE_INDICATOR, delimiter);
    }

    public static double[] readDataset(Path path) throws IOException {
        return readDataset(path, LAST_VALUE_INDICATOR);
    }

    public static double[] readDataset(String path, Integer indexOfValue, String delimiter) throws IOException {
        return readDataset(Paths.get(path), indexOfValue, delimiter);
    }

    public static double[] readDataset(String path, Integer indexOfValue) throws IOException {
        return readDataset(Paths.get(path), indexOfValue);
    }

    public static double[] readDataset(String path, String delimiter) throws IOException {
        return readDataset(Paths.get(path), delimiter);
    }

    public static double[] readDataset(String path) throws IOException {
        return readDataset(Paths.get(path));
    }
}
