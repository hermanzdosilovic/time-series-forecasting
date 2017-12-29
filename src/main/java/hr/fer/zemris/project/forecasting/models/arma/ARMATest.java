package hr.fer.zemris.project.forecasting.models.arma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ARMATest {

    public static void main(String[] args) {
        double[] dataset;
        try {
            List<String> b = Files.readAllLines(
                Paths.get("C:\\Users\\JARVIS\\workspace\\ARMA\\target\\ARDataset.txt"));
            int i = 0;
            dataset = new double[b.size()];
            for (String s : b) {
                double value = Double.parseDouble(s.split(" ")[4]);
                //					int k = (int)(value * 100);
                //					int l = k % 1000;
                //					value = l / 100.;
                dataset[i] = value;
                //					System.out.println(value + ",");
                i++;
            }
        } catch (IOException e) {
            System.out.println(e);
            return;
        }
//
//        ARMA arma = new ARMA(5, 0, dataset, false);
//
//        List<Double> armaForecast = arma.forecast(100);
//        System.out.println(armaForecast.size());
//        System.out.println(armaForecast);
//
//        armaForecast = arma.forecast(100);
//        System.out.println(armaForecast.size());
//        System.out.println(armaForecast);
    }
}
