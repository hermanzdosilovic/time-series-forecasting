package hr.fer.zemris.project.forecasting.gui.forms.annForms;

public class PSOForm {

    private String numberOfParticles = "100";
    private String maxIteration = "1000";
    private String desiredFitness = "0";
    private String desiredPrecision = "1E-2";
    private String individualFactor = "5.05";
    private String socialFactor = "2.05";
    private String minValue = "-5";
    private String maxValue = "5";
    private String minSpeed = "-0.1";
    private String maxSpeed = "0.1";
    private boolean fullyFormed = false;

    private static PSOForm ourInstance = new PSOForm();

    public static PSOForm getInstance() {
        return ourInstance;
    }

    private PSOForm(){
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

    public void setFullyFormed(boolean fullyFormed) {
        this.fullyFormed = fullyFormed;
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

    public boolean isFullyFormed() {
        return fullyFormed;
    }
}
