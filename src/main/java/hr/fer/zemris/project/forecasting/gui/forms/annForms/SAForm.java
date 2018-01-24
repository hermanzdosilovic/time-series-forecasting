package hr.fer.zemris.project.forecasting.gui.forms.annForms;

public class SAForm {


    private String outerIterations = "1000";
    private String outerInitialTemperature = "100";
    private String outerFinalTemperature = "1E-4";
    private String innerIterations = "4000";
    private String desiredPenalty = "0";
    private String desiredPrecision = "1E-3";
    private String desiredFitness = "0";
    private String minComponentValue = "-5";
    private String maxComponentValue = "5";
    private String mutationProbability = "0.1";
    private String sigma = "0.9";
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
