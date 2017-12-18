package hr.fer.zemris.project.forecasting.nn;

import com.dosilovic.hermanzvonimir.ecfjava.neural.FeedForwardANN;
import com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork;
import com.dosilovic.hermanzvonimir.ecfjava.neural.activations.IActivation;
import com.dosilovic.hermanzvonimir.ecfjava.neural.activations.ReLUActivation;
import com.dosilovic.hermanzvonimir.ecfjava.neural.activations.SigmoidActivation;
import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.List;

public class Backpropagation {

    private List<DatasetEntry> trainingSet;
    private List<DatasetEntry> validationSet;
    private double learningRate;
    private long maxIteration;
    private double desiredError;
    private double desiredPrecision;
    private long currentIteration;
    private List<NeuralNetworkObserver> observers = new ArrayList<>();


    public Backpropagation(List<DatasetEntry> trainingSet, List<DatasetEntry> validationSet,
                           double learningRate, long maxIteration, double desiredError, double desiredPrecision) {
        this.trainingSet = trainingSet;
        this.validationSet = validationSet;
        this.learningRate = learningRate;
        this.maxIteration = maxIteration;
        this.desiredError = desiredError;
        this.desiredPrecision = desiredPrecision;
    }

    public double[] train(INeuralNetwork neuralNetwork) {
        for (int i = 0; i < maxIteration; ++i) {
            double mse = 0.;
            for (DatasetEntry entry : trainingSet) {
                double[] err = doBackpropagation(neuralNetwork, entry.getInput(), entry.getOutput());
                mse += err[0] * err[0];
            }
            System.out.println("Iter: " + i + " mse: " + mse / 2.);
            if (Math.abs(mse / 2. - desiredError) < desiredPrecision) {
                break;
            }
        }
        return new double[]{};
    }

    private double[] doBackpropagation(INeuralNetwork neuralNetwork, double[] inputValue, double[] expectedValue) {
        RealMatrix[] layerWeights = neuralNetwork.getLayerWeights();//tezine između dva sloja
        IActivation[] activationFunctions = neuralNetwork.getLayerActivations(); //aktiv fje, znaju i svoju derivaciju

        RealVector expected = new ArrayRealVector(expectedValue);
        RealVector forecast = new ArrayRealVector(neuralNetwork.forward(inputValue));
        RealVector deltaOutput = expected.subtract(forecast);
        RealVector[] layerOutputs = neuralNetwork.getLayerOutputs()[0].getLayerOutputs();

        RealVector outputLayerError = forecast
                .map(t -> activationFunctions[activationFunctions.length - 1].getDerivative(t))
                .ebeMultiply(deltaOutput);

        RealVector lastHiddenLayerOutput = layerOutputs[layerOutputs.length - 2];
        RealMatrix outputLayerMatrix = layerWeights[layerWeights.length - 1];
        for (int i = 0; i < outputLayerMatrix.getRowDimension(); ++i) {
            RealVector row = outputLayerMatrix.getRowVector(i);
            row = row.add(outputLayerError.mapMultiply(learningRate * lastHiddenLayerOutput.getEntry(i))
            );
            outputLayerMatrix.setRowVector(i, row);
        }

        int k = layerOutputs.length - 1;
        for (int i = layerWeights.length - 1; i > 0; --i) {
            RealMatrix oneBeforeMatrix = layerWeights[i];
            RealVector thisLayerOutput = layerOutputs[k - 1];
            RealVector thisLayerError = new ArrayRealVector(thisLayerOutput.getDimension());

            for (int j = 0; j < oneBeforeMatrix.getRowDimension(); ++j) {
                RealVector row = oneBeforeMatrix.getRowVector(j);
                double err = row.dotProduct(outputLayerError) *
                        activationFunctions[i - 1].getDerivative(thisLayerOutput.getEntry(j));
                thisLayerError.setEntry(j, err);
            }
            outputLayerError = thisLayerError.getSubVector(0, thisLayerError.getDimension() - 1);

            RealMatrix currentWeights = layerWeights[i - 1];
            RealVector currentOutput = layerOutputs[k - 2];
            --k;

            for (int j = 0; j < currentWeights.getRowDimension(); j++) {
                RealVector row = currentWeights.getRowVector(j);
                row = row.add(outputLayerError.mapMultiply(currentOutput.getEntry(j) * learningRate));
                currentWeights.setRowVector(j, row);
            }

        }


        return deltaOutput.toArray();
    }

    public List<DatasetEntry> getTrainingSet() {
        return trainingSet;
    }

    public List<DatasetEntry> getValidationSet() {
        return validationSet;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public long getMaxIteration() {
        return maxIteration;
    }

    public double getDesiredError() {
        return desiredError;
    }

    public double getDesiredPrecision() {
        return desiredPrecision;
    }

    public long getCurrentIteration() {
        return currentIteration;
    }

    private void notifyObservers() {
        observers.forEach(o -> o.update(this));
    }

}
