package hr.fer.zemris.project.forecasting.tdnn;

public interface INeuralNetwork {

    double[] getWeights();

    void setWeights(double[] weights);

    void setInput(double[] input);

    /**
     * The method feeds given input trough all layers of neural net and returns output value.
     *
     * @return the output of neural network
     */
    double feedForward();

    int getInputLayerSize();
}
