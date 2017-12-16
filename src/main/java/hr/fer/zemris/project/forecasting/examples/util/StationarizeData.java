package hr.fer.zemris.project.forecasting.examples.util;

import hr.fer.zemris.project.forecasting.models.Stationary;
import hr.fer.zemris.project.forecasting.util.DataReaderUtil;
import hr.fer.zemris.project.forecasting.util.GraphUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StationarizeData {

    public static void main(String[] args) throws IOException {
        double[] exchangeRate =
            DataReaderUtil.readDataset("./datasets/exchange-rate-twi-may-1970-aug-1.csv", 1);
        double[] stationarizedExchangeRate = Stationary.stationarize(exchangeRate);

        Map<String, double[]> graph = new HashMap<>();
        graph.put("Exchange Rate", exchangeRate);
        graph.put("Stationarized Exchange Rate", stationarizedExchangeRate);

        GraphUtil.plot(graph, "Stationarize DataReaderUtil Example");
    }
}
