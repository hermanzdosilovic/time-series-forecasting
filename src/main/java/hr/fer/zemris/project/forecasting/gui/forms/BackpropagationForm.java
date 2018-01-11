package hr.fer.zemris.project.forecasting.gui.forms;

public class BackpropagationForm {

    private String maxIteration = "";
    private String batchSize = "";
    private String learningRate = "";
    private String desiredError = "";
    private String desiredPrecision = "";

    private static BackpropagationForm ourInstance = new BackpropagationForm();

    public static BackpropagationForm getInstance() {
        return ourInstance;
    }

    private BackpropagationForm() {
    }

    public void setMaxIteration(String maxIteration) {
        this.maxIteration = maxIteration;
    }

    public void setBatchSize(String batchSize) {
        this.batchSize = batchSize;
    }

    public void setLearningRate(String learningRate) {
        this.learningRate = learningRate;
    }

    public void setDesiredError(String desiredError) {
        this.desiredError = desiredError;
    }

    public void setDesiredPrecision(String desiredPrecision) {
        this.desiredPrecision = desiredPrecision;
    }

    public String getMaxIteration() {
        return maxIteration;
    }

    public String getBatchSize() {
        return batchSize;
    }

    public String getLearningRate() {
        return learningRate;
    }

    public String getDesiredError() {
        return desiredError;
    }

    public String getDesiredPrecision() {
        return desiredPrecision;
    }
}
