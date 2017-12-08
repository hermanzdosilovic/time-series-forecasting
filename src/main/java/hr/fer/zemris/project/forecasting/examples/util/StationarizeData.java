package hr.fer.zemris.project.forecasting.examples.util;

import hr.fer.zemris.project.forecasting.models.ModelUtil;
import hr.fer.zemris.project.forecasting.util.Graph;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StationarizeData {

    public static void main(String[] args) throws IOException {
        double[] exchangeRate              = Graph.readDataset("./datasets/exchange-rate-twi-may-1970-aug-1.csv", 1);
        double[] stationarizedExchangeRate = ModelUtil.stationarize(exchangeRate);

        Map<String, double[]> graph = new HashMap<>();
        graph.put("Exchange Rate", exchangeRate);
        graph.put("Stationarized Exchange Rate", stationarizedExchangeRate);

        Graph.plot(graph, "Stationarize Data Example");
    }
}
