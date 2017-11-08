package hr.fer.zemris.projekt.predictions.tdnn;

import java.util.ArrayList;
import java.util.List;

public abstract class Neuron {

    protected List<Weight> inputWeights = new ArrayList<>();
    protected double outputValue;

    public double getOutputValue() {
        return outputValue;
    }

    public List<Weight> getInputWeights() {
        return inputWeights;
    }

    public void addWeight(Weight weight) {
        if (!weight.getOutputNeuron().equals(this)) {
            throw new NeuronException("Invalid output neuron");
        }
        inputWeights.add(weight);
    }

    public abstract void calculateOutputValue();
}
