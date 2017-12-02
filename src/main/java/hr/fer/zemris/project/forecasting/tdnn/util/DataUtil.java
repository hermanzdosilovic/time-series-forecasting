package hr.fer.zemris.project.forecasting.tdnn.util;

import hr.fer.zemris.project.forecasting.tdnn.TDNN;
import hr.fer.zemris.project.forecasting.tdnn.model.DataEntry;
import hr.fer.zemris.project.forecasting.util.Pair;
import hr.fer.zemris.project.forecasting.util.math.Vectors;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.List;

public final class DataUtil {
    public static List<DataEntry> createTDNNDateset(List<Double> rawData,
        int inputSize, int outputSize) {
        List<DataEntry> dataset = new ArrayList<>();

        for (int i = inputSize; i < rawData.size() - outputSize; i++) {
            dataset.add(new DataEntry(
                rawData.subList(i - inputSize, i),
                rawData.subList(i, i + outputSize)
            ));
        }

        return dataset;
    }

    public static Pair<List<Double>, List<Double>> splitDataset(List<Double> rawData, double trainPercentage) {
        int trainSize = (int) (rawData.size()*trainPercentage);
        return new Pair<>(
            rawData.subList(0, trainSize),
            rawData.subList(trainSize, rawData.size())
        );
    }

    public static List<Double> joinExpectedValues(List<DataEntry> dataset) {
        List<Double> joinedValues = new ArrayList<>();
        for (DataEntry dataEntry : dataset) {
            joinedValues.addAll(dataEntry.getExpectedOutput());
        }
        return joinedValues;
    }

    public static double calculateMeanSquaredError(TDNN network, List<DataEntry> dataset) {
        double error = 0;
        for (DataEntry entry : dataset) {
            RealVector expected = Vectors.asRealVector(entry.getExpectedOutput());
            RealVector actual = Vectors.asRealVector(network.forward(entry.getInput()));
            RealVector difference = expected.subtract(actual);
            error += difference.dotProduct(difference);
        }
        return error / dataset.size();
    }

    public static List<Double> forward(TDNN tdnn, List<DataEntry> dataset) {
        List<Double> result = new ArrayList<>();
        for (DataEntry entry : dataset) {
            result.addAll(tdnn.forward(entry.getInput()));
        }
        return result;
    }
}
