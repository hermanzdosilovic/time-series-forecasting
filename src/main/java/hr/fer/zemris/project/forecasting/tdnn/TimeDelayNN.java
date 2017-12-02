package hr.fer.zemris.project.forecasting.tdnn;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TimeDelayNN {

    private RealMatrix[] layerWeights;
    private int numberOfWeights;

    private static final Random rand = new Random();

    public TimeDelayNN(int... architecture) {
        layerWeights = new RealMatrix[architecture.length - 1];

        for (int i = 1; i < architecture.length; i++) {
            int rows = architecture[i];
            int cols = architecture[i - 1] + 1;
            RealMatrix layerWeight = new Array2DRowRealMatrix(rows, cols);

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    layerWeight.setEntry(row, col, rand.nextDouble());
                }
            }

            numberOfWeights += rows * cols;
            layerWeights[i - 1] = layerWeight.transpose();
        }
    }

    public int getNumberOfWeights() {
        return numberOfWeights;
    }

    public List<Double> forward(List<Double> input) {
        RealVector inputVector = new ArrayRealVector(input.size() + 1);
        for (int i = 0; i < input.size(); i++) {
            inputVector.setEntry(i, input.get(i));
        }
        inputVector.setEntry(input.size(), 1);

        for (int i = 0; i < layerWeights.length; i++) {
            inputVector = layerWeights[i].preMultiply(inputVector);

            if (i + 1 < layerWeights.length) {
                inputVector = inputVector.append(1);
            }
        }

        return Arrays.asList(ArrayUtils.toObject(inputVector.toArray()));
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
