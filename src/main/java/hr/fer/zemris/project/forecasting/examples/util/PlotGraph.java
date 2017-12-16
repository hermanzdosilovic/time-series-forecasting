package hr.fer.zemris.project.forecasting.examples.util;

import hr.fer.zemris.project.forecasting.util.DataReaderUtil;
import hr.fer.zemris.project.forecasting.util.GraphUtil;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PlotGraph {

    public static void main(String[] args) throws IOException {
        double[] exchangeRate   = DataReaderUtil.readDataset("./datasets/exchange-rate-twi-may-1970-aug-1.csv");
        double[] milkProduction = DataReaderUtil.readDataset("./datasets/monthly-milk-production-pounds-p.csv");

        Map<String, double[]> graph = new HashMap<>();
        graph.put("Exchange Rate", exchangeRate);
        graph.put("Milk Production", milkProduction);

        GraphUtil.plot(graph, "Plot GraphUtil Example");
    }
}
