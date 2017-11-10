package hr.fer.zemris.projekt.predictions.examples.util;

import hr.fer.zemris.projekt.predictions.util.Util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ExamplePNG {
    public static void main(String[] args) throws IOException {
        Path pathToDataset = Paths.get("./datasets/exchange-rate-twi-may-1970-aug-1.csv");
        Path toPNG = Paths.get("./graph.png");
        List<Double> ex_rate = Util.readDataset(pathToDataset);
        Util.datasetToPNG(toPNG, ex_rate);
    }
}
