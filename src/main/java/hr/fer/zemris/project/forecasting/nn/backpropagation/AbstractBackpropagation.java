package hr.fer.zemris.project.forecasting.nn.backpropagation;

import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.AbstractMetaheuristic;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.util.IObserver;
import com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork;
import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBackpropagation extends AbstractMetaheuristic<double[]> {

    protected List<DatasetEntry> trainingSet;
    protected List<DatasetEntry> validationSet;
    protected double learningRate;
    protected long maxIteration;
    protected double desiredError;
    protected double desiredPrecision;
    protected long currentIteration = 1;
    protected double trainingMSE;
    protected double validationMSE;
    protected INeuralNetwork neuralNetwork;
    protected int batchSize;
    protected List<IObserver<double[]>> observers = new ArrayList<>();
    protected DatasetEntry[] datasetArray;

    public AbstractBackpropagation(List<DatasetEntry> trainingSet, List<DatasetEntry> validationSet,
                           double learningRate, long maxIteration, double desiredError,
                           double desiredPrecision, INeuralNetwork neuralNetwork, int batchSize) {
        this.trainingSet = trainingSet;
        this.validationSet = validationSet;
        this.learningRate = learningRate;
        this.maxIteration = maxIteration;
        this.desiredError = desiredError;
        this.desiredPrecision = desiredPrecision;
        this.neuralNetwork = neuralNetwork;
        this.batchSize = batchSize;
        datasetArray = new DatasetEntry[trainingSet.size() + validationSet.size()];
        List<DatasetEntry> dataset = new ArrayList<>(trainingSet);
        dataset.addAll(trainingSet);
        datasetArray = dataset.toArray(datasetArray);
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
}
