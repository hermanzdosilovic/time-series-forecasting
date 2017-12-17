package hr.fer.zemris.project.forecasting.models.arma;

import hr.fer.zemris.project.forecasting.util.DataUtil;

import java.util.Arrays;

public class Dataset {

    private double mean;
    private double[] dataset;
    private double[] datasetBackup;

    public Dataset(double[] dataset) {
        datasetBackup = dataset.clone();
        this.dataset = dataset;
        mean = DataUtil.getMean(dataset);
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
