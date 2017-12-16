package hr.fer.zemris.project.forecasting.models.arma;

import java.util.Arrays;

public class Dataset {

    private double mean;
    private double[] dataset;
    private double[] datasetBackup;

    public Dataset(double[] dataset) {
        datasetBackup = dataset.clone();
        this.dataset = dataset;
        double sum = 0;
        for (double d : dataset) {
            sum += d;
        }
        this.mean = sum / dataset.length;
        for (int i = 0; i < dataset.length; i++) {
            this.dataset[i] -= mean;
        }
    }

    public double getMean() {
        return mean;
    }

    public double[] getDataset() {
        return dataset;
    }

    public double[] getDatasetBackup() {
        return datasetBackup;
    }

    public void addSample(double sample) {
        double[] newDataset = Arrays.copyOf(dataset, dataset.length + 1);
        double newMean = (mean * dataset.length + sample) / newDataset.length;
        newDataset[newDataset.length - 1] = sample - newMean;
        for (int i = 0; i < dataset.length - 1; i++) {
            newDataset[i] = newDataset[i] + mean - newMean;
        }
        this.dataset = newDataset;
        this.mean = newMean;
    }
}
