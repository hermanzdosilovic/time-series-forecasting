package hr.fer.zemris.project.forecasting.tdnn;

import hr.fer.zemris.project.forecasting.tdnn.model.DataEntry;
import hr.fer.zemris.project.forecasting.tdnn.util.DataUtil;
import hr.fer.zemris.project.forecasting.util.Pair;
import hr.fer.zemris.project.forecasting.util.Util;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.List;

public class TimeSeriesPrediction {

    private static final int[] ARCHITECTURE = {5, 5, 4, 1};

    public static void main(String[] args) throws IOException {
        List<Double> rawData = Util.readDataset("./datasets/exchange-rate-twi-may-1970-aug-1.csv");

        Pair<List<Double>, List<Double>> splittedRawData = DataUtil.splitRawData(rawData, 0.8);
        List<Double> rawTrainData = splittedRawData.getFirst();
        List<Double> rawTestData = splittedRawData.getSecond();

        int inputSize = ARCHITECTURE[0];
        int outputSize = ARCHITECTURE[ARCHITECTURE.length - 1];

        List<DataEntry> trainSet = DataUtil.createDataset(rawTrainData, inputSize, outputSize);
        List<DataEntry> testSet = DataUtil.createDataset(rawTestData, inputSize, outputSize);

        TimeDelayNN tdnn = new TimeDelayNN(ARCHITECTURE);
    }

}
