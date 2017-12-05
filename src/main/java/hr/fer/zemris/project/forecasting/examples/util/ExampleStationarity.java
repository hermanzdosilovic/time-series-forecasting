package hr.fer.zemris.project.forecasting.examples.util;

import hr.fer.zemris.project.forecasting.models.Stationary;
import hr.fer.zemris.project.forecasting.util.Util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ExampleStationarity {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("./datasets/exchange-rate-twi-may-1970-aug-1.csv");
        List<Double> ex_rate = Util.readDataset(path, 1);
//        Util.plot(ex_rate, Stationary.stationarize(ex_rate));

        Stationary stat = new Stationary(ex_rate);
        List<Double> stationarized = stat.getDatasetAsList();
        List<Double> accumulated = stat.accumulate(stat.getDatasetAsList());
        Util.plot(ex_rate, accumulated);

    }
}
