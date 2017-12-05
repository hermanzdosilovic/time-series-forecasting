package hr.fer.zemris.projekt.predictions.examples;

import hr.fer.zemris.project.forecasting.util.Util;
import hr.fer.zemris.projekt.predictions.models.AR;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ARExample {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("./datasets/ARDataset.txt");
        List<Double> ex_rate = Util.readDataset(path, 3, " ");
        List<Double> new_list = ex_rate.stream().map((m)->{
            int n =(int) (m * 100);
            int k = n % 1000;
            return k / 100.0;
        }).collect(Collectors.toList());
        AR ar = new AR(1, new_list);
        System.out.println(ar.computeNextValues(5));
//        List<Double> results = ar.computeNextValues(50);
//        Util.plot(results);
    }
}
