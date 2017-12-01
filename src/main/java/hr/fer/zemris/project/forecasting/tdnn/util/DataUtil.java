package hr.fer.zemris.project.forecasting.tdnn.util;

import hr.fer.zemris.project.forecasting.tdnn.model.DataEntry;
import hr.fer.zemris.project.forecasting.util.Pair;

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
}
