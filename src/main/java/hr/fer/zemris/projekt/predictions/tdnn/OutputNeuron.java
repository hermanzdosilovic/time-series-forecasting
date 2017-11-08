package hr.fer.zemris.projekt.predictions.tdnn;

public class OutputNeuron extends Neuron {

    private int slope;

    public OutputNeuron(int slope) {
        this.slope = slope;
    }

    @Override
    public void calculateOutputValue() {
        double sum = 0.;
        for (Weight w : super.inputWeights) {
            sum += w.getInputNeuron().getOutputValue() * w.getWeight();
        }
        outputValue = 1. / (1. + Math.exp(-slope * sum));
    }
}
