package hr.fer.zemris.project.forecasting.tdnn;

public interface INeuralNetwork {

    int getNumberOfWeights();

    void setWeights(double[] weights);

    double[] forward(double[] input);
}
