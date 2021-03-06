package hr.fer.zemris.project.forecasting.nn.backpropagation;

import com.dosilovic.hermanzvonimir.ecfjava.models.solutions.ISolution;
import com.dosilovic.hermanzvonimir.ecfjava.models.solutions.SimpleSolution;
import com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork;
import com.dosilovic.hermanzvonimir.ecfjava.neural.activations.IActivation;
import com.dosilovic.hermanzvonimir.ecfjava.neural.errors.NNErrorUtil;
import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.Arrays;
import java.util.List;

import static hr.fer.zemris.project.forecasting.nn.util.BackpropagationUtil.*;

public class Backpropagation extends AbstractBackpropagation {

    public Backpropagation(List<DatasetEntry> trainingSet, List<DatasetEntry> validationSet,
                           double learningRate, long maxIteration, double desiredError,
                           double desiredPrecision, INeuralNetwork neuralNetwork, int batchSize) {

        super(trainingSet, validationSet, learningRate, maxIteration,
                desiredError, desiredPrecision, neuralNetwork, batchSize);
    }

    @Override
    public ISolution<double[]> run() {
        List<DatasetEntry>[] batches = createBatches(batchSize, trainingSet);
        RealMatrix[] layerOutputs = new RealMatrix[neuralNetwork.getNumberOfLayers()];

        while (currentIteration <= maxIteration) {
            if (isStopped.get()) {
                break;
            }

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
                    trainingMse = trainingMse.add(outputDeltaMatrix.getRowVector(k)
                            .ebeMultiply(outputDeltaMatrix.getRowVector(k)));
                }
                doBackpropagation(neuralNetwork, outputDeltaMatrix, layerOutputs);
            }
            trainingMSE = calculateSetMSE(trainingMse, trainingSet.size());

            RealVector validationMse = new ArrayRealVector(neuralNetwork.getOutputSize());
            for (DatasetEntry entry : validationSet) {
                RealVector forecast = new ArrayRealVector(neuralNetwork.forward(entry.getInput()));
                RealVector expected = new ArrayRealVector(entry.getOutput());
                RealVector delta = expected.subtract(forecast);
                validationMse = validationMse.add(delta.ebeMultiply(delta));
            }
            double validationSetMse = validationMSE;
            validationMSE = calculateSetMSE(validationMse, validationSet.size());

            double datasetError = NNErrorUtil.meanSquaredError(neuralNetwork, datasetArray);
            bestSolution.setFitness(datasetError);
            System.err.println("Iter: #" + currentIteration + ":\n\t train mse: " + trainingMSE
                    + "\n\t validation mse: " + validationMSE + "\n\t dataset mse: " + datasetError);
            if ((validationSetMse < validationMSE && currentIteration > maxIteration / 2)
                    || Math.abs(trainingMSE - desiredError) < desiredPrecision) {
                break;
            }

            notifyObservers();
            ++currentIteration;
        }
        return bestSolution;
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
        bestSolution = new SimpleSolution<>(weights);
        neuralNetwork.setWeights(weights);
    }
}
