package hr.fer.zemris.project.forecasting.examples.util;

import hr.fer.zemris.project.forecasting.models.Stationary;
import hr.fer.zemris.project.forecasting.util.Util;

import java.util.List;

public class ExampleStationarity2 {
    public static void main(String[] args) {
        double[] dataset = new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Stationary stat = new Stationary(dataset);

        List<Double> stat_dataset = stat.getDatasetAsList();
        System.out.println(stat_dataset);

        stat_dataset.add(1.0);
        stat_dataset.add(1.0);
        stat_dataset.add(2.0);
        System.out.println(stat.accumulate(stat_dataset));
    }
}
