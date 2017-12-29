package hr.fer.zemris.project.forecasting.models;

import java.util.List;

public abstract class AModel {

    public List<Double> getDataset;

    public abstract double computeNextValue();

    public double[] computeNextValues(int n) {
        double[] array = new double[n];
        for (int i = 0; i < array.length; i++) {
            array[i] = computeNextValue();
        }
        return array;
    }

}
