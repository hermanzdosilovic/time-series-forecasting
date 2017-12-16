package hr.fer.zemris.project.forecasting.nn;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.Random;

public class TDNN implements INeuralNetwork {

    private int numberOfInputs;
    private int numberOfOutputs;
    private int numberOfWeights;

    private RealMatrix[]       layerWeights;
    private UnivariateFunction activationFunction;

    private static final Random RAND = new Random();

    public TDNN(UnivariateFunction activationFunction, int... architecture) {
        numberOfInputs = architecture[0];
        numberOfOutputs = architecture[architecture.length - 1];

        layerWeights = new RealMatrix[architecture.length - 1];

        for (int i = 1; i < architecture.length; i++) {
            int        rows        = architecture[i - 1] + 1;
            int        cols        = architecture[i];
            RealMatrix layerWeight = new Array2DRowRealMatrix(rows, cols);

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    layerWeight.setEntry(row, col, RAND.nextDouble());
                }
            }

            numberOfWeights += rows * cols;
            layerWeights[i - 1] = layerWeight;
        }

        this.activationFunction = activationFunction;
    }

    public TDNN(int... architecture) {
        this(ActivationFunction.IDENTITY, architecture);
    }

    public double[] forward(double[] input) {
        RealVector inputVector = new ArrayRealVector(input.length + 1);
        for (int i = 0; i < input.length; i++) {
            inputVector.setEntry(i, input[i]);
        }
        inputVector.setEntry(input.length, 1);

        for (int i = 0; i < layerWeights.length; i++) {
            inputVector = layerWeights[i].preMultiply(inputVector);

            if (i + 1 < layerWeights.length) {
                inputVector = inputVector.map(activationFunction);
                inputVector = inputVector.append(1);
            }
        }

        return inputVector.toArray();
    }

    public void setWeights(double[] weights) {
        if (weights.length != numberOfWeights) {
            throw new IllegalArgumentException(
                "Invalid number of weights. Given " + weights.length + ", expected "
                + numberOfWeights);
        }

        int k = 0;
        for (RealMatrix layerWeight : layerWeights) {
            for (int i = 0; i < layerWeight.getRowDimension(); i++) {
                for (int j = 0; j < layerWeight.getColumnDimension(); j++) {
                    layerWeight.setEntry(i, j, weights[k++]);
                }
            }
        }
    }

    @Override public int getNumberOfInputs() {
        return numberOfInputs;
    }

    @Override
    public int getNumberOfOutputs() {
        return numberOfOutputs;
    }

    public int getNumberOfWeights() {
        return numberOfWeights;
    }
}
