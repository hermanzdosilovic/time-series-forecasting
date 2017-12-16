package hr.fer.zemris.project.forecasting.examples.util;

import hr.fer.zemris.project.forecasting.models.Stationary;
import hr.fer.zemris.project.forecasting.util.DataReaderUtil;
import hr.fer.zemris.project.forecasting.util.GraphUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ExampleStationarity {

    public static void main(String[] args) throws IOException {
        double[] exchangeRate = DataReaderUtil.readDataset("./datasets/exchange-rate-twi-may-1970-aug-1.csv", 1);

        Stationary stat        = new Stationary(exchangeRate);
        double[]   accumulated = stat.accumulate(stat.getDataset());
        GraphUtil.plot(exchangeRate, accumulated);
    }
}
