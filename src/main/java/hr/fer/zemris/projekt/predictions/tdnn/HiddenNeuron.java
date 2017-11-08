package hr.fer.zemris.projekt.predictions.tdnn;

public class HiddenNeuron extends Neuron {

    private double[] timeDelay;

    public HiddenNeuron(int timeDelay) {
        if (timeDelay < 0) {
            throw new NeuronException("Time delay can't be lower than zero. Current is: " + timeDelay);
        }
        this.timeDelay = new double[timeDelay];
    }

    @Override
    public void calculateOutputValue() {
        double inputSum = 0.;
        for (Synapse s : super.inputSynapses) {
            inputSum += s.getInputNeuron().getOutputValue() * s.getWeight();
        }

        double net = inputSum;
        for (int i = timeDelay.length - 1; i > 0; --i) {
            timeDelay[i] = timeDelay[i - 1];
            net += timeDelay[i];
        }
        timeDelay[0] = inputSum;

        outputValue = 1. / (1. + Math.exp(-net));
    }
}
