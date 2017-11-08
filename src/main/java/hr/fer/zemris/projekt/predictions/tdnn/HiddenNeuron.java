package hr.fer.zemris.projekt.predictions.tdnn;

public class HiddenNeuron extends Neuron {

    private int slope;
    private double[] timeDelay;

    public HiddenNeuron(int slope, int timeDelay) {
        if (timeDelay < 0) {
            throw new NeuronException("Time delay can't be lower than zero. Current is: " + timeDelay);
        }
        this.slope = slope;
        this.timeDelay = new double[timeDelay];
    }

    @Override
    public void calculateOutputValue() {
        double inputSum = 0.;
        for (Weight w : super.inputWeights) {
            inputSum += w.getInputNeuron().getOutputValue() * w.getWeight();
        }

        double totalSum = inputSum;
        for (int i = timeDelay.length - 1; i > 0; --i) {
            timeDelay[i] = timeDelay[i - 1];
            totalSum += timeDelay[i];
        }
        timeDelay[0] = inputSum;

        outputValue = 1. / (1. + Math.exp(-slope * totalSum));
    }
}
