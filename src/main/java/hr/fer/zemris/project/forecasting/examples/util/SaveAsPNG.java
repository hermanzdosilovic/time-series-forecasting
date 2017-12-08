package hr.fer.zemris.project.forecasting.examples.util;

import hr.fer.zemris.project.forecasting.util.Graph;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SaveAsPNG {

    public static void main(String[] args) throws IOException {
        double[] exchangeRate = Graph.readDataset("./datasets/exchange-rate-twi-may-1970-aug-1.csv");

        Map<String, double[]> graph = new HashMap<>();
        graph.put("Exchange Rate", exchangeRate);

        Graph.saveAsPNG("./graph.png", "Save As PNG Example", graph);
    }
}
