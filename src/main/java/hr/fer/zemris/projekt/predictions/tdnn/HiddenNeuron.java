package hr.fer.zemris.projekt.predictions.tdnn;

public class HiddenNeuron extends Neuron {

    @Override
    public void calculateOutputValue() {
        double net = 0.;
        for (Synapse s : inputSynapses) {
            net += s.getInputNeuron().getOutputValue() * s.getWeight();
        }

        outputValue = 1. / (1. + Math.exp(-net));
    }
}
