package hr.fer.zemris.project.forecasting.examples.util;

import hr.fer.zemris.project.forecasting.util.Util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveAsPNG {

    public static void main(String[] args) throws IOException {
        double[] exchangeRate = Util.readDataset("./datasets/exchange-rate-twi-may-1970-aug-1.csv");

        Map<String, double[]> graph = new HashMap<>();
        graph.put("Exchange Rate", exchangeRate);

        Util.saveAsPNG("./graph.png", "Save As PNG Example", graph);
    }
}
