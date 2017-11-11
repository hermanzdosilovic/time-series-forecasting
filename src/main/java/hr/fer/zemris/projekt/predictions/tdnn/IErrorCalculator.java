package hr.fer.zemris.projekt.predictions.tdnn;

public interface IErrorCalculator {

    double calculateError(double[] inputs, INeuralNetwork neuralNetwork);

}
