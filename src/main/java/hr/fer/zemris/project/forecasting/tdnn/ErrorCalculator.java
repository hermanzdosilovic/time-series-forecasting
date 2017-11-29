package hr.fer.zemris.project.forecasting.tdnn;

import java.util.Arrays;

public class ErrorCalculator implements IErrorCalculator {


    @Override
    public double calculateError(double[] inputs, INeuralNetwork neuralNetwork) {
        double error = 0.;
        int windowSize = neuralNetwork.getInputLayerSize();
        for (int i = 0, n = inputs.length - windowSize; i < n; ++i) {
            double[] input = Arrays.copyOfRange(inputs, i, i + windowSize);
            neuralNetwork.setInput(input);
            double result = neuralNetwork.feedForward();
            error += Math.pow(result - inputs[i + windowSize + 1], 2.) / 2.;
        }
        return error;
    }
}
