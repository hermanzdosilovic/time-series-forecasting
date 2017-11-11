package hr.fer.zemris.projekt.predictions.tdnn;

public interface IErrorProvider {

    double getError(double[] inputs, INeuralNetwork neuralNetwork);

}
