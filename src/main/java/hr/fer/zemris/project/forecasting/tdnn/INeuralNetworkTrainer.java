package hr.fer.zemris.project.forecasting.tdnn;

public interface INeuralNetworkTrainer {

    void trainNeuralNetwork(INeuralNetwork neuralNetwork, IErrorCalculator errorCalculator);
}
