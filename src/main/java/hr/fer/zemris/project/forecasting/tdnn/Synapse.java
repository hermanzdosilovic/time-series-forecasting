package hr.fer.zemris.project.forecasting.tdnn;

public class Synapse {
    private Neuron inputNeuron;
    private Neuron outputNeuron;
    private double weight;

    public Synapse(Neuron inputNeuron, Neuron outputNeuron, double weight) {
        this.inputNeuron = inputNeuron;
        this.outputNeuron = outputNeuron;
        this.weight = weight;
    }

    public Synapse(Neuron inputNeuron, Neuron outputNeuron) {
        this(inputNeuron,outputNeuron,1.);
    }

    public Neuron getInputNeuron() {
        return inputNeuron;
    }

    public void setInputNeuron(Neuron inputNeuron) {
        this.inputNeuron = inputNeuron;
    }

    public Neuron getOutputNeuron() {
        return outputNeuron;
    }

    public void setOutputNeuron(Neuron outputNeuron) {
        this.outputNeuron = outputNeuron;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }
}
