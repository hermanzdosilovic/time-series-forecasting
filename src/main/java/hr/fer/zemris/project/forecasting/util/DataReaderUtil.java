package hr.fer.zemris.project.forecasting.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class DataReaderUtil {

    private static final int    LAST_VALUE_INDICATOR = -1;
    private static final String DEFAULT_DELIMITER    = ",";

    public static double[] readDataset(InputStream inputStream, Integer indexOfValue, String delimiter) throws IOException {
        BufferedReader reader  = new BufferedReader(new InputStreamReader(inputStream));

        List<Double> dataset = new ArrayList<>();

        String line;
        while( (line = reader.readLine()) != null) {
            String[] data  = line.trim().split(String.format("[%s]", delimiter));
            int      index = checkIndex(indexOfValue, data.length - 1);
            dataset.add(Double.parseDouble(data[index].trim()));
        }

        Double[] doubleResult = dataset.toArray(new Double[dataset.size()]);
        double[] result = new double[doubleResult.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = doubleResult[i];
        }

        return result;
    }

    public static double[] readDataset(Path path, Integer indexOfValue, String delimiter) throws IOException {
        return readDataset(new FileInputStream(path.toFile()), indexOfValue, delimiter);
    }

    private static int checkIndex(int indexOfValue, int max) {
        if (indexOfValue == LAST_VALUE_INDICATOR) {
            return max;
        }
        if (indexOfValue < 0) {
            return 0;
        }
        if (indexOfValue > max) {
            return max;
        }
        return indexOfValue;
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

    public static double[] readDataset(InputStream inputStream) throws IOException {
        return readDataset(inputStream, LAST_VALUE_INDICATOR, DEFAULT_DELIMITER);
    }
}
