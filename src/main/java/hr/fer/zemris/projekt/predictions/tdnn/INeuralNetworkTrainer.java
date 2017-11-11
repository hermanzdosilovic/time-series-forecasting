package hr.fer.zemris.projekt.predictions.tdnn;

public interface INeuralNetworkTrainer {

    void trainNeuralNetwork(INeuralNetwork neuralNetwork, IErrorCalculator errorProvider);
}
