package hr.fer.zemris.project.forecasting.nn;

import com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork;
import com.dosilovic.hermanzvonimir.ecfjava.neural.activations.IActivation;
import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static hr.fer.zemris.project.forecasting.nn.util.BackpropagationUtil.*;

public class Backpropagation {

    private List<DatasetEntry> trainingSet;
    private List<DatasetEntry> validationSet;
    private double learningRate;
    private long maxIteration;
    private double desiredError;
    private double desiredPrecision;
    private long currentIteration;
    private double trainingMSE;
    private double validationMSE;

    private List<BackpropagationObserver> observers = new ArrayList<>();

    public Backpropagation(List<DatasetEntry> trainingSet, List<DatasetEntry> validationSet, double learningRate,
                           long maxIteration, double desiredError, double desiredPrecision) {
        this.trainingSet = trainingSet;
        this.validationSet = validationSet;
        this.learningRate = learningRate;
        this.maxIteration = maxIteration;
        this.desiredError = desiredError;
        this.desiredPrecision = desiredPrecision;
    }

    public void train(INeuralNetwork neuralNetwork, int batchSize) {
        List<DatasetEntry>[] batches = createBatches(batchSize, trainingSet);
        RealMatrix[] layerOutputs = new RealMatrix[neuralNetwork.getNumberOfLayers()];

        for (currentIteration = 1; currentIteration <= maxIteration; ++currentIteration) {
            RealVector trainingMse = new ArrayRealVector(neuralNetwork.getOutputSize());
            for (List<DatasetEntry> batch : batches) {
                RealMatrix outputMatrix = new Array2DRowRealMatrix(batch.size(), neuralNetwork.getOutputSize());
                double[][] inputs = new double[batch.size()][];
                for (int j = 0; j < batch.size(); ++j) {
                    DatasetEntry batchElement = batch.get(j);
                    inputs[j] = batchElement.getInput();
                    outputMatrix.setRow(j, batchElement.getOutput());
                }
                neuralNetwork.forward(inputs);

                double[][] outputsByLayer = neuralNetwork.getOutput();
                for (int k = 0; k < outputsByLayer.length; ++k) {
                    double[] layerOutput = outputsByLayer[k];
                    int layerLength = neuralNetwork.getArchitecture()[k];
                    int width = k < outputsByLayer.length - 1 ? layerLength + 1 : layerLength;
                    layerOutputs[k] = new Array2DRowRealMatrix(batch.size(), width);
                    int offset = 0;
                    for (int l = 0; l < layerOutput.length; l += layerLength) {
                        double[] output = Arrays.copyOfRange(layerOutput, l, l + layerLength);
                        RealVector outputVector = new ArrayRealVector(output);
                        if (k < outputsByLayer.length - 1) {
                            outputVector = outputVector.append(1.);
                        }
                        layerOutputs[k].setRowVector(offset++, outputVector);
                    }
                }

                RealMatrix forecastMatrix = layerOutputs[layerOutputs.length - 1];
                RealMatrix outputDeltaMatrix = outputMatrix.subtract(forecastMatrix);
                for (int k = 0; k < outputDeltaMatrix.getRowDimension(); ++k) {
                    trainingMse = trainingMse.add(outputDeltaMatrix.getRowVector(k));
                }
                doBackpropagation(neuralNetwork, outputDeltaMatrix, layerOutputs);
            }
            trainingMSE = trainingMse.dotProduct(trainingMse) / trainingSet.size();

            RealVector validationMse = new ArrayRealVector(neuralNetwork.getOutputSize());
            for (DatasetEntry entry : validationSet) {
                RealVector forecast = new ArrayRealVector(neuralNetwork.forward(entry.getInput()));
                RealVector expected = new ArrayRealVector(entry.getOutput());
                validationMse = validationMse.add(expected.subtract(forecast));
            }
            double validationSetMse = validationMSE;
            validationMSE = validationMse.dotProduct(validationMse) / validationSet.size();

            notifyObservers();
            System.err.println("iteration: " + currentIteration + " training set mse: " + trainingMSE
                    + " validation set mse: " + validationMSE);
            if ((validationSetMse < validationMSE && currentIteration > maxIteration / 2)
                    || Math.abs(trainingMSE - desiredError) < desiredPrecision) {
                break;
            }
        }
    }

    private void doBackpropagation(INeuralNetwork neuralNetwork, RealMatrix outputDeltaMatrix, RealMatrix[] allLayerOutputs) {
        RealMatrix outputLayerError = new Array2DRowRealMatrix(
                outputDeltaMatrix.getRowDimension(), outputDeltaMatrix.getColumnDimension()
        );
        for (int i = 0; i < outputDeltaMatrix.getRowDimension(); ++i) {
            RealVector outputDerived = allLayerOutputs[allLayerOutputs.length - 1].getRowVector(i)
                    .map(t -> neuralNetwork.
                            getLayerActivations()[neuralNetwork.getLayerActivations().length - 1].getDerivative(t)
                    );
            outputLayerError.setRowVector(i, outputDerived.ebeMultiply(outputDeltaMatrix.getRowVector(i)));
        }
        RealMatrix[] layerWeights = createLayerWeights(neuralNetwork.getWeights(), neuralNetwork.getArchitecture());
        RealMatrix lastLayerWeights = layerWeights[neuralNetwork.getNumberOfLayers() - 2];
        RealMatrix lastHiddenLayerOutput = allLayerOutputs[allLayerOutputs.length - 2];
        for (int i = 0; i < lastLayerWeights.getRowDimension(); ++i) {
            RealVector yOutput = lastHiddenLayerOutput.getColumnVector(i);
            for (int j = 0; j < lastLayerWeights.getColumnDimension(); ++j) {
                double sum = outputLayerError.getColumnVector(j).dotProduct(yOutput);
                double currentWeight = lastLayerWeights.getEntry(i, j);
                lastLayerWeights.setEntry(i, j, currentWeight + sum * learningRate);
            }
        }

        RealMatrix currentError = outputLayerError;
        IActivation[] activations = neuralNetwork.getLayerActivations();
        for (int i = layerWeights.length - 1; i > 0; --i) {
            RealMatrix nextLayerMatrix = layerWeights[i];
            RealMatrix currentLayerOutput = allLayerOutputs[i];
            RealMatrix currentLayerError = new Array2DRowRealMatrix(
                    currentLayerOutput.getRowDimension(), currentLayerOutput.getColumnDimension()
            );
            final int index = i;
            for (int j = 0; j < currentLayerOutput.getRowDimension(); ++j) {
                RealVector row = currentLayerOutput.getRowVector(j);
                row = row.map(t -> activations[index].getDerivative(t));
                for (int k = 0; k < row.getDimension(); ++k) {
                    double err = nextLayerMatrix.getRowVector(k).dotProduct(currentError.getRowVector(j));
                    currentLayerError.setEntry(j, k, err * row.getEntry(k));
                }
            }
            currentError = currentLayerError.getSubMatrix(0, currentLayerError.getRowDimension() - 1,
                    0, currentLayerError.getColumnDimension() - 2);

            RealMatrix currentWeights = layerWeights[i - 1];
            RealMatrix currentOutput = allLayerOutputs[i - 1];
            for (int j = 0; j < currentWeights.getRowDimension(); j++) {
                for (int k = 0; k < currentWeights.getColumnDimension(); ++k) {
                    double currentWeight = currentWeights.getEntry(j, k);
                    double gradient = currentError.getColumnVector(k).dotProduct(currentOutput.getColumnVector(j));
                    currentWeights.setEntry(j, k, currentWeight + learningRate * gradient);
                }
            }
        }
        double[] weights = extractWeights(layerWeights, neuralNetwork.getNumberOfWeights());
        neuralNetwork.setWeights(weights);
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

    public double getTrainingMSE() {
        return trainingMSE;
    }

    public double getValidationMSE() {
        return validationMSE;
    }

    private void notifyObservers() {
        observers.forEach(o -> o.update(this));
    }

    public void addObserver(BackpropagationObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(BackpropagationObserver observer) {
        observers.remove(observer);
    }
}