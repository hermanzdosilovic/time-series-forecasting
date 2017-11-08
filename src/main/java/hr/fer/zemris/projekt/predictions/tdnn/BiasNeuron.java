package hr.fer.zemris.projekt.predictions.tdnn;

public class BiasNeuron extends Neuron{

    @Override
    public void calculateOutputValue() {
        outputValue = 1.;
    }

    private static BiasNeuron biasNeuron = new BiasNeuron();

    public static BiasNeuron getInstance() {
        return biasNeuron;
    }

    private BiasNeuron() {
    }
}
