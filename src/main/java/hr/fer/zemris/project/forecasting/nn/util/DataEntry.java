package hr.fer.zemris.project.forecasting.nn.util;

public class DataEntry {

    private double[] input;
    private double[] expectedOutput;

    public DataEntry(double[] input, double[] expectedOutput) {
        this.input = input;
        this.expectedOutput = expectedOutput;
    }

    public double[] getInput() {
        return input;
    }

    public double[] getExpectedOutput() {
        return expectedOutput;
    }
}
