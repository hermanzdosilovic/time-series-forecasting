package hr.fer.zemris.project.forecasting.tdnn;

import hr.fer.zemris.project.forecasting.tdnn.model.ActivationFunction;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TDNN implements INeuralNetwork {

    private RealMatrix[] layerWeights;
    private int numberOfWeights;
    private UnivariateFunction activationFunction;

    private static final Random rand = new Random();

    public TDNN(UnivariateFunction activationFunction, int... architecture) {
        layerWeights = new RealMatrix[architecture.length - 1];

        for (int i = 1; i < architecture.length; i++) {
            int rows = architecture[i - 1] + 1;
            int cols = architecture[i];
            RealMatrix layerWeight = new Array2DRowRealMatrix(rows, cols);

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    layerWeight.setEntry(row, col, rand.nextDouble());
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

    public int getNumberOfWeights() {
        return numberOfWeights;
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
                    "invalid number of weights. Given " + weights.length + ", expected "
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
}
