package hr.fer.zemris.projekt.predictions.tdnn;

public class InputNeuron extends Neuron {

    private double inputValue;

    @Override
    public void calculateOutputValue() {
        outputValue = inputValue;
    }

    public void setInputValue(double inputValue) {
        this.inputValue = inputValue;
    }
}
