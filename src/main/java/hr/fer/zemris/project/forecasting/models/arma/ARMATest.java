package hr.fer.zemris.project.forecasting.models.arma;

import hr.fer.zemris.project.forecasting.util.DataReaderUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ARMATest {

    public static void main(String[] args) {
        double[] dataset;

        try {
            dataset = DataReaderUtil.readDataset("datasets/exchange-rate-twi-may-1970-aug-1.csv");

        } catch (IOException e) {
            System.out.println(e);
            return;
        }
//
        double[] backupDataset = dataset.clone();
        ARMA arma = new ARMA(0, 2, dataset, false);


        for(int i = 0; i < backupDataset.length; i++){
            System.out.println(backupDataset[i] - arma.testDataset()[i]);
        }
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
