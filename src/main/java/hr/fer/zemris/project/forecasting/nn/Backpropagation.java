package hr.fer.zemris.project.forecasting.nn;

import com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork;
import com.dosilovic.hermanzvonimir.ecfjava.neural.activations.IActivation;
import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.dosilovic.hermanzvonimir.ecfjava.neural.FeedForwardANN.LayerOutputs;

public class Backpropagation {

    private List<DatasetEntry> trainingSet;
    private List<DatasetEntry> validationSet;
    private double learningRate;
    private long maxIteration;
    private double desiredError;
    private double desiredPrecision;
    private long currentIteration;
    private List<NeuralNetworkObserver> observers = new ArrayList<>();

    Random r = new Random();

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
        double validationSetMse = 0.;
        for (int i = 0; i < maxIteration; ++i) {

            RealMatrix outputDeltaMatrix = new Array2DRowRealMatrix(trainingSet.size(), neuralNetwork.getOutputSize());
            RealMatrix[] layerOutputs = new RealMatrix[neuralNetwork.getNumberOfLayers()];
            for (int j = 0; j < neuralNetwork.getNumberOfLayers(); ++j) {
                int size = j == neuralNetwork.getNumberOfLayers() - 1 ? neuralNetwork.getArchitecture()[j] : neuralNetwork.getArchitecture()[j] + 1;
                layerOutputs[j] = new Array2DRowRealMatrix(trainingSet.size(), size);
            }

            RealVector mse = new ArrayRealVector(neuralNetwork.getOutputSize());
            for (int j = 0; j < trainingSet.size(); ++j) {
                DatasetEntry entry = trainingSet.get(j);
                RealVector forecast = new ArrayRealVector(neuralNetwork.forward(entry.getInput()));
                RealVector expected = new ArrayRealVector(entry.getOutput());
                outputDeltaMatrix.setRowVector(j, expected.subtract(forecast));
                mse = mse.add(expected.subtract(forecast));

                LayerOutputs outputsByLayer = neuralNetwork.getLayerOutputs()[0];
                for (int k = 0; k < outputsByLayer.getLayerOutputs().length; ++k) {
                    layerOutputs[k].setRowVector(j, outputsByLayer.getLayerOutputs()[k]);
                }

            }
            double[] arr = doBackpropagation(neuralNetwork, outputDeltaMatrix, layerOutputs);
            double msErr = mse.dotProduct(mse) / trainingSet.size();

            RealVector validationMse = new ArrayRealVector(neuralNetwork.getOutputSize());
            for (int j = 0; j < validationSet.size(); ++j) {
                DatasetEntry entry = validationSet.get(j);
                RealVector forecast = new ArrayRealVector(neuralNetwork.forward(entry.getInput()));
                RealVector expected = new ArrayRealVector(entry.getOutput());
                validationMse = validationMse.add(expected.subtract(forecast));
            }
            double valMse = validationMse.dotProduct(validationMse)/validationSet.size();
            System.out.println("iter: " + (i + 1) + " train mse: " + msErr+" validation mse: "+valMse);
            if(Math.abs(valMse)<validationSetMse && currentIteration > maxIteration/2) {
                System.out.println("validation set break.");
                break;
            }
            validationSetMse = valMse;

            if (Math.abs(msErr - desiredError) < desiredPrecision ) {
                break;
            }

        }
        System.out.println("Training");
        for (DatasetEntry e : trainingSet) {
            System.out.println("expected: " + e.getOutput()[0] + " -> forecast: " + neuralNetwork.forward(e.getInput())[0]);
        }
        System.out.println("Validation");
        for (DatasetEntry e : validationSet) {
            System.out.println("expected: " + e.getOutput()[0] + " -> forecast: " + neuralNetwork.forward(e.getInput())[0]);
        }

        return new double[]{};
    }

    private double[] doBackpropagation(INeuralNetwork neuralNetwork, RealMatrix outputDeltaMatrix, RealMatrix[] allLayerOutputs) {
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

        RealMatrix lastLayerWeights = neuralNetwork.getLayerWeights()[neuralNetwork.getLayerWeights().length - 1];
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
        for (int i = neuralNetwork.getLayerWeights().length - 1; i > 0; --i) {
            RealMatrix nextLayerMatrix = neuralNetwork.getLayerWeights()[i];
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

            RealMatrix currentWeights = neuralNetwork.getLayerWeights()[i - 1];
            RealMatrix currentOutput = allLayerOutputs[i - 1];
            for (int j = 0; j < currentWeights.getRowDimension(); j++) {
                for (int k = 0; k < currentWeights.getColumnDimension(); ++k) {
                    double currentWeight = currentWeights.getEntry(j, k);
                    double gradient = currentError.getColumnVector(k).dotProduct(currentOutput.getColumnVector(j));
                    currentWeights.setEntry(j, k, currentWeight + learningRate * gradient);
                }
            }
        }
        return new double[]{};
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
