package hr.fer.zemris.project.forecasting.tdnn.util;

import hr.fer.zemris.project.forecasting.tdnn.TimeDelayNN;
import hr.fer.zemris.project.forecasting.tdnn.model.DataEntry;
import hr.fer.zemris.project.forecasting.util.Pair;
import hr.fer.zemris.project.forecasting.util.math.VectorUtil;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.List;

public final class DataUtil {
    public static List<DataEntry> createDataset(List<Double> rawData,
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

    public static Pair<List<DataEntry>, List<DataEntry>> splitDataset(List<DataEntry> dataset, double trainPercentage) {
        int trainSize = (int) (dataset.size()*trainPercentage);
        return new Pair<>(
            dataset.subList(0, trainSize),
            dataset.subList(trainSize, dataset.size())
        );
    }

    public static Pair<List<Double>, List<Double>> splitRawData(List<Double> rawData, double trainPercentage) {
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

    public static double calculateMeanSquaredError(TimeDelayNN network, List<DataEntry> dataset) {
        double error = 0;
        for (DataEntry entry : dataset) {
            RealVector expected = VectorUtil.createArrayRealVector(entry.getExpectedOutput());
            RealVector actual = VectorUtil.createArrayRealVector(network.forward(entry.getInput()));
            RealVector difference = expected.subtract(actual);
            error += difference.dotProduct(difference);
        }
        return error / dataset.size();
    }
}
