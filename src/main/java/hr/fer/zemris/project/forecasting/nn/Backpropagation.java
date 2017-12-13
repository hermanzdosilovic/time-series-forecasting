package hr.fer.zemris.project.forecasting.nn;

import com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork;
import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import org.apache.commons.math3.linear.ArrayRealVector;
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


        return new double[]{};
    }

    private double[] doBackpropagation(INeuralNetwork neuralNetwork, double[] inputValue, double[] expectedValue) {
        RealVector expected = new ArrayRealVector(expectedValue);
        RealVector forecasted = new ArrayRealVector(neuralNetwork.forward(inputValue));
        RealVector deltaOutput = expected.subtract(forecasted);

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
