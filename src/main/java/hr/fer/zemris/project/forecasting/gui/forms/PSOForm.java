package hr.fer.zemris.project.forecasting.gui.forms;

public class PSOForm {

    private String numberOfParticles = "";
    private String maxIteration = "";
    private String desiredFitness = "";
    private String desiredPrecision = "";
    private String individualFactor = "";
    private String socialFactor = "";
    private String minValue = "";
    private String maxValue = "";
    private String minSpeed = "";
    private String maxSpeed = "";


    private static PSOForm ourInstance = new PSOForm();

    public static PSOForm getInstance() {
        return ourInstance;
    }

    public void setNumberOfParticles(String numberOfParticles) {
        this.numberOfParticles = numberOfParticles;
    }

    public void setMaxIteration(String maxIteration) {
        this.maxIteration = maxIteration;
    }

    public void setDesiredFitness(String desiredFitness) {
        this.desiredFitness = desiredFitness;
    }

    public void setDesiredPrecision(String desiredPrecision) {
        this.desiredPrecision = desiredPrecision;
    }

    public void setIndividualFactor(String individualFactor) {
        this.individualFactor = individualFactor;
    }

    public void setSocialFactor(String socialFactor) {
        this.socialFactor = socialFactor;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public void setMinSpeed(String minSpeed) {
        this.minSpeed = minSpeed;
    }

    public void setMaxSpeed(String maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getNumberOfParticles() {
        return numberOfParticles;
    }

    public String getMaxIteration() {
        return maxIteration;
    }

    public String getDesiredFitness() {
        return desiredFitness;
    }

    public String getDesiredPrecision() {
        return desiredPrecision;
    }

    public String getIndividualFactor() {
        return individualFactor;
    }

    public String getSocialFactor() {
        return socialFactor;
    }

    public String getMinValue() {
        return minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public String getMinSpeed() {
        return minSpeed;
    }

    public String getMaxSpeed() {
        return maxSpeed;
    }
}
