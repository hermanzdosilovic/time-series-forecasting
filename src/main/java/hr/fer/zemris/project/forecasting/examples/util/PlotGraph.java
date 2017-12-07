package hr.fer.zemris.project.forecasting.examples.util;

import hr.fer.zemris.project.forecasting.util.Util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlotGraph {

    public static void main(String[] args) throws IOException {
        double[] exchangeRate   = Util.readDataset("./datasets/exchange-rate-twi-may-1970-aug-1.csv");
        double[] milkProduction = Util.readDataset("./datasets/monthly-milk-production-pounds-p.csv");

        Map<String, double[]> graph = new HashMap<>();
        graph.put("Exchange Rate", exchangeRate);
        graph.put("Milk Production", milkProduction);
        Util.plot(graph);
    }
}
