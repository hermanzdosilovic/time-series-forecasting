package hr.fer.zemris.project.forecasting.gui.forms;

public class SAForm {


    private String outerIterations = "";
    private String outerInitialTemperature = "";
    private String outerFinalTemperature = "";
    private String innerIterations = "";
    private String innerInitialTemperature = "";
    private String innerFinalTemperature = "";
    private String desiredPenalty = "";
    private String desiredPrecision = "";
    private String desiredFitness = "";
    private String minComponentValue = "";
    private String maxComponentValue = "";
    private String mutationProbability = "";
    private String sigma = "";
    private boolean forceMutation = true;

    private static SAForm ourInstance = new SAForm();

    public static SAForm getInstance() {
        return ourInstance;
    }

    private SAForm() {
    }

    public void setDesiredFitness(String desiredFitness) {
        this.desiredFitness = desiredFitness;
    }

    public void setOuterIterations(String outerIterations) {
        this.outerIterations = outerIterations;
    }

    public void setOuterInitialTemperature(String outerInitialTemperature) {
        this.outerInitialTemperature = outerInitialTemperature;
    }

    public void setOuterFinalTemperature(String outerFinalTemperature) {
        this.outerFinalTemperature = outerFinalTemperature;
    }

    public void setInnerIterations(String innerIterations) {
        this.innerIterations = innerIterations;
    }

    public void setInnerInitialTemperature(String innerInitialTemperature) {
        this.innerInitialTemperature = innerInitialTemperature;
    }

    public void setInnerFinalTemperature(String innerFinalTemperature) {
        this.innerFinalTemperature = innerFinalTemperature;
    }

    public void setDesiredPenalty(String desiredPenalty) {
        this.desiredPenalty = desiredPenalty;
    }

    public void setDesiredPrecision(String desiredPrecision) {
        this.desiredPrecision = desiredPrecision;
    }

    public void setMinComponentValue(String minComponentValue) {
        this.minComponentValue = minComponentValue;
    }

    public void setMaxComponentValue(String maxComponentValue) {
        this.maxComponentValue = maxComponentValue;
    }

    public void setMutationProbability(String mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    public void setSigma(String sigma) {
        this.sigma = sigma;
    }

    public void setForceMutation(boolean forceMutation) {
        this.forceMutation = forceMutation;
    }

    public String getOuterIterations() {
        return outerIterations;
    }

    public String getOuterInitialTemperature() {
        return outerInitialTemperature;
    }

    public String getOuterFinalTemperature() {
        return outerFinalTemperature;
    }

    public String getInnerIterations() {
        return innerIterations;
    }

    public String getInnerInitialTemperature() {
        return innerInitialTemperature;
    }

    public String getInnerFinalTemperature() {
        return innerFinalTemperature;
    }

    public String getDesiredPenalty() {
        return desiredPenalty;
    }

    public String getDesiredPrecision() {
        return desiredPrecision;
    }

    public String getMinComponentValue() {
        return minComponentValue;
    }

    public String getMaxComponentValue() {
        return maxComponentValue;
    }

    public String getMutationProbability() {
        return mutationProbability;
    }

    public String getSigma() {
        return sigma;
    }

    public boolean isForceMutation() {
        return forceMutation;
    }

    public String getDesiredFitness() {
        return desiredFitness;
    }
}
