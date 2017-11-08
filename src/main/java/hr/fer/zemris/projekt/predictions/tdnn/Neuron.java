package hr.fer.zemris.projekt.predictions.tdnn;

import java.util.ArrayList;
import java.util.List;

public abstract class Neuron {

    protected List<Synapse> inputSynapses = new ArrayList<>();
    protected double outputValue;

    public double getOutputValue() {
        return outputValue;
    }

    public List<Synapse> getInputSynapses() {
        return inputSynapses;
    }

    public void addWeight(Synapse synapse) {
        if (!synapse.getOutputNeuron().equals(this)) {
            throw new NeuronException("Invalid output neuron");
        }
        inputSynapses.add(synapse);
    }

    public abstract void calculateOutputValue();
}
