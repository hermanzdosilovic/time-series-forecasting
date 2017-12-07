package hr.fer.zemris.project.forecasting.examples.util;

import hr.fer.zemris.project.forecasting.util.Util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamplePlotGraphs {

    public static void main(String[] args) throws IOException {
        Path path1 = Paths.get("./datasets/exchange-rate-twi-may-1970-aug-1.csv");
        Path path2 = Paths.get("./datasets/monthly-milk-production-pounds-p.csv");
        double[] ex_rate = Util.readDataset(path1);
        double[] milk_prod = Util.readDataset(path2);
//        Util.plot(ex_rate, milk_prod);

        Map<String,double[]> tmp = new HashMap<>();
        tmp.put("Ex rate", ex_rate);
        tmp.put("Milk prod", milk_prod);
        Util.plot(tmp);


    }
}
