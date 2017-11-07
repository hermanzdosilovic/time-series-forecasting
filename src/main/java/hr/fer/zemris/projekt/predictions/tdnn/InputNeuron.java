package hr.fer.zemris.projekt.predictions.tdnn;

public class InputNeuron extends Neuron {

    private double inputValue;

    public InputNeuron(double inputValue) {
        this.inputValue = inputValue;
    }

    @Override
    public void calculateOutputValue() {
        super.outputValue = inputValue;
    }
}
