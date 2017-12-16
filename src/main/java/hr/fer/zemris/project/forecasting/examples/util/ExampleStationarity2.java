package hr.fer.zemris.project.forecasting.examples.util;

import hr.fer.zemris.project.forecasting.models.Stationary;

import java.util.List;

public class ExampleStationarity2 {

    public static void main(String[] args) {
        double[]   dataset = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Stationary stat    = new Stationary(dataset);

        List<Double> statDataset = stat.getDatasetAsList();
        System.out.println(statDataset);

        statDataset.add(1.0);
        statDataset.add(1.0);
        statDataset.add(2.0);

        System.out.println(stat.accumulate(statDataset));
    }
}
