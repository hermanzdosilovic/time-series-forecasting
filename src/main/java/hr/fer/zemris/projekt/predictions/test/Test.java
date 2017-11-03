package hr.fer.zemris.projekt.predictions.test;

import hr.fer.zemris.projekt.predictions.util.Util;
import hr.fer.zemris.projekt.predictions.util.UtilException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class Test {

    public static void main(String[] args) {
        Path path = Paths.get("./src/main/resources/exchange-rate-twi-may-1970-aug-1.csv");
        Path save = Paths.get("./src/main/resources");
        try {
            Map<String, Double> dataset = Util.parseDataset(path);
            Util.plotDataset(dataset, path.getFileName().toString(), save);
        } catch (UtilException e) {
            System.out.printf("%s happened on file %s.", e.getMessage(), path.getFileName().toString());
        }

    }
}
