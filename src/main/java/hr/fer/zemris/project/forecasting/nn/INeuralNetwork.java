package hr.fer.zemris.project.forecasting.nn;

public interface INeuralNetwork {

    int getNumberOfInputs();

    int getNumberOfOutputs();

    int getNumberOfWeights();

    double[] forward(double[] input);

    void setWeights(double[] weights);
}
