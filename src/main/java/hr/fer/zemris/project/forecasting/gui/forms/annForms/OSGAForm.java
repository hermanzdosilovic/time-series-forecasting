package hr.fer.zemris.project.forecasting.gui.forms.annForms;

public class OSGAForm {

    private String populationSize = "500";
    private String maxGenerations = "100";
    private String maxSelectionPressure = "1000";
    private String desiredFitness = "0";
    private String desiredPrecision = "1E-3";
    private String minComponentValue = "-5";
    private String maxComponentValue = "5";
    private String minSuccessRatio = "0.3";
    private String maxSuccessRatio = "1";
    private String minComparisionFactor = "0.5";
    private String maxComparisionFactor = "1";
    private String tournamentSize = "20";
    private String alpha = "0.3";
    private String mutationProbability = "0.1";
    private String sigma = "0.9";
    private boolean useElitism = true;
    private boolean allowRepeat = false;
    private boolean forceMutation = true;

    private static OSGAForm ourInstance = new OSGAForm();

    public static OSGAForm getInstance() {
        return ourInstance;
    }

    private OSGAForm() {
    }

    public void setPopulationSize(String populationSize) {
        this.populationSize = populationSize;
    }

    public void setMaxGenerations(String maxGenerations) {
        this.maxGenerations = maxGenerations;
    }

    public void setMaxSelectionPressure(String maxSelectionPressure) {
        this.maxSelectionPressure = maxSelectionPressure;
    }

    public void setDesiredFitness(String desiredFitness) {
        this.desiredFitness = desiredFitness;
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

    public void setMinSuccessRatio(String minSuccessRatio) {
        this.minSuccessRatio = minSuccessRatio;
    }

    public void setMaxSuccessRatio(String maxSuccessRatio) {
        this.maxSuccessRatio = maxSuccessRatio;
    }

    public void setMinComparisionFactor(String minComparisionFactor) {
        this.minComparisionFactor = minComparisionFactor;
    }

    public void setMaxComparisionFactor(String maxComparisionFactor) {
        this.maxComparisionFactor = maxComparisionFactor;
    }

    public void setTournamentSize(String tournamentSize) {
        this.tournamentSize = tournamentSize;
    }

    public void setAlpha(String alpha) {
        this.alpha = alpha;
    }

    public void setMutationProbability(String mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    public void setSigma(String sigma) {
        this.sigma = sigma;
    }

    public void setUseElitism(boolean useElitism) {
        this.useElitism = useElitism;
    }

    public void setAllowRepeat(boolean allowRepeat) {
        this.allowRepeat = allowRepeat;
    }

    public void setForceMutation(boolean forceMutation) {
        this.forceMutation = forceMutation;
    }

    public String getPopulationSize() {
        return populationSize;
    }

    public String getMaxGenerations() {
        return maxGenerations;
    }

    public String getMaxSelectionPressure() {
        return maxSelectionPressure;
    }

    public String getDesiredFitness() {
        return desiredFitness;
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

    public String getMinSuccessRatio() {
        return minSuccessRatio;
    }

    public String getMaxSuccessRatio() {
        return maxSuccessRatio;
    }

    public String getMinComparisionFactor() {
        return minComparisionFactor;
    }

    public String getMaxComparisionFactor() {
        return maxComparisionFactor;
    }

    public String getTournamentSize() {
        return tournamentSize;
    }

    public String getAlpha() {
        return alpha;
    }

    public String getMutationProbability() {
        return mutationProbability;
    }

    public String getSigma() {
        return sigma;
    }

    public boolean isUseElitism() {
        return useElitism;
    }

    public boolean isAllowRepeat() {
        return allowRepeat;
    }

    public boolean isForceMutation() {
        return forceMutation;
    }
}
