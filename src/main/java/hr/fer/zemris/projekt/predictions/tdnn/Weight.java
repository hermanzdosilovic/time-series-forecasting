package hr.fer.zemris.projekt.predictions.tdnn;

public class Weight {
    private Neuron inputNeuron;
    private Neuron outputNeuron;
    private double weight;

    public Weight(Neuron inputNeuron, Neuron outputNeuron, double weight) {
        this.inputNeuron = inputNeuron;
        this.outputNeuron = outputNeuron;
        this.weight = weight;
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
