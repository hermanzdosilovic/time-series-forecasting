package hr.fer.zemris.project.forecasting.tdnn;

public interface IErrorCalculator {

    double calculateError(double[] inputs, INeuralNetwork neuralNetwork);

}
