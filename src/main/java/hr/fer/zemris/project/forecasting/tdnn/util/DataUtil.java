package hr.fer.zemris.project.forecasting.tdnn.util;

import hr.fer.zemris.project.forecasting.tdnn.TDNN;
import hr.fer.zemris.project.forecasting.tdnn.model.DataEntry;
import hr.fer.zemris.project.forecasting.util.Pair;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class DataUtil {

    public static List<DataEntry> createTDNNDateset(double[] rawData, int inputSize,
                                                    int outputSize) {
        List<DataEntry> dataset = new ArrayList<>();

        for (int i = inputSize; i < rawData.length - outputSize; i++) {
            dataset.add(new DataEntry(
                    Arrays.copyOfRange(rawData, i - inputSize, i), Arrays.copyOfRange(rawData, i, i + outputSize)
            ));
        }

        return dataset;
    }

    public static Pair<List<Double>, List<Double>> splitDataset(List<Double> dataset,
                                                                double trainPercentage) {
        int trainSize = (int) (dataset.size() * trainPercentage);
        return new Pair<>(dataset.subList(0, trainSize),
                dataset.subList(trainSize, dataset.size()));
    }

    public static Pair<List<DataEntry>, List<DataEntry>> splitTDNNDataset(List<DataEntry> dataset,
                                                                          double trainPercentage) {
        int trainSize = (int) (dataset.size() * trainPercentage);
        return new Pair<>(dataset.subList(0, trainSize),
                dataset.subList(trainSize, dataset.size()));
    }

    public static double[] joinExpectedValues(List<DataEntry> dataset) {
        if (dataset.size() < 1) {
            throw new IllegalArgumentException("Dataset size should be greater than zero");
        }
        int offset = 0;
        double[] joinedValues = new double[dataset.size() * dataset.get(0).getExpectedOutput().length];
        for (DataEntry dataEntry : dataset) {
            double[] entries = dataEntry.getExpectedOutput();
            for (double entry : entries) {
                joinedValues[offset++] = entry;
            }
        }
        return joinedValues;
    }

    public static double calculateMeanSquaredError(TDNN network, List<DataEntry> dataset) {
        double error = 0;
        int n = 0;
        for (DataEntry entry : dataset) {
            RealVector expected = new ArrayRealVector(entry.getExpectedOutput());
            RealVector actual = new ArrayRealVector(network.forward(entry.getInput()));
            RealVector difference = expected.subtract(actual);
            error += difference.dotProduct(difference);
            n += expected.getDimension();
        }
        return error / n;
    }

    public static double calculateMeanPercentageError(TDNN network, List<DataEntry> dataset) {
        double error = 0;
        int n = 0;
        for (DataEntry entry : dataset) {
            RealVector expected = new ArrayRealVector(entry.getExpectedOutput());
            RealVector actual = new ArrayRealVector(network.forward(entry.getInput()));
            n += expected.getDimension();
            for (int i = 0; i < expected.getDimension(); i++) {
                error += (actual.getEntry(i) - expected.getEntry(i)) / actual.getEntry(i);
            }
        }
        return 100.0 * error / n;
    }

    public static double calculateMeanAbsoluteError(TDNN network, List<DataEntry> dataset) {
        double error = 0;
        int n = 0;
        for (DataEntry entry : dataset) {
            RealVector expected = new ArrayRealVector(entry.getExpectedOutput());
            RealVector actual = new ArrayRealVector(network.forward(entry.getInput()));
            n += expected.getDimension();
            for (int i = 0; i < expected.getDimension(); i++) {
                error += Math.abs(actual.getEntry(i) - expected.getEntry(i));
            }
        }
        return error / n;
    }

    public static double[] forward(TDNN tdnn, List<DataEntry> dataset) {
        int offset = 0;
        int size = dataset.size() * tdnn.getNumberOfOutputs();
        double[] result = new double[size];
        for (DataEntry entry : dataset) {
            double[] forwardResults = (tdnn.forward(entry.getInput()));
            for (double forwardResult : forwardResults) {
                result[offset++] = forwardResult;
            }
        }
        return result;
    }

    public static double[] featureScale(double[] dataset) {
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        for (double v : dataset) {
            max = Math.max(v, max);
            min = Math.min(v, min);
        }

        double[] scaled = new double[dataset.length];
        for (int i = 0, n = dataset.length; i < n; ++i) {
            scaled[i] = (dataset[i] - min) / (max - min);
        }

        return scaled;
    }
}
