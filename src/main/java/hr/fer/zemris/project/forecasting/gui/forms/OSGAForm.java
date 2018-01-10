package hr.fer.zemris.project.forecasting.gui.forms;

public class OSGAForm {

    private String populationSize = "";
    private String maxGenerations = "";
    private String maxSelectionPressure = "";
    private String desiredFitness = "";
    private String desiredPrecision = "";
    private String minComponentValue = "";
    private String maxComponentValue = "";
    private String minSuccessRatio = "";
    private String maxSuccessRatio = "";
    private String minComparisionFactor = "";
    private String maxComparisionFactor = "";
    private String tournamentSize = "";
    private String alpha = "";
    private String mutationProbability = "";
    private String sigma = "";
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
