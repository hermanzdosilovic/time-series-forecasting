package hr.fer.zemris.projekt.predictions.examples.util;

import hr.fer.zemris.projekt.predictions.models.ModelUtil;
import hr.fer.zemris.projekt.predictions.util.Util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ExampleStationarity {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("./datasets/exchange-rate-twi-may-1970-aug-1.csv");
        List<Double> ex_rate = Util.readDataset(path, 1);
        Util.plot(ModelUtil.stationarize(ex_rate));
    }
}