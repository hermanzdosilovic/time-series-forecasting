package hr.fer.zemris.project.forecasting.tdnn;

import hr.fer.zemris.project.forecasting.tdnn.model.DataEntry;
import hr.fer.zemris.project.forecasting.tdnn.util.DataUtil;
import hr.fer.zemris.project.forecasting.util.Pair;
import hr.fer.zemris.project.forecasting.util.Util;

import java.io.IOException;
import java.util.List;

public class TimeSeriesPrediction {

    public static void main(String[] args) throws IOException {
        List<Double> rawData = Util.readDataset("./datasets/exchange-rate-twi-may-1970-aug-1.csv");
        Util.plot(rawData);

        List<DataEntry> dataset = DataUtil.createDataset(rawData, 5, 1);
//        INeuralNetwork tdnn = new TDNN(5, 5, 4, 3);
    }

}
