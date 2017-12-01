package hr.fer.zemris.project.forecasting.tdnn.model;

import java.util.List;

public class DataEntry {

    private List<Double> input;
    private List<Double> expectedOutput;

    public DataEntry(List<Double> input, List<Double> expectedOutput) {
        this.input = input;
        this.expectedOutput = expectedOutput;
    }

    public List<Double> getInput() {
        return input;
    }

    public List<Double> getExpectedOutput() {
        return expectedOutput;
    }
}
