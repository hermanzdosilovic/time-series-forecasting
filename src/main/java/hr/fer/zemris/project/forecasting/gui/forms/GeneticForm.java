package hr.fer.zemris.project.forecasting.gui.forms;

public class GeneticForm {

    private String populationSize = "";
    private String maxGenerations = "";
    private String desiredFitness = "";
    private String desiredPrecision = "";
    private String tournamentSize = "";
    private String minComponentValue = "";
    private String maxComponentValue = "";
    private String alpha = "";
    private String mutationProbability = "";
    private String sigma = "";
    private boolean useElitism = false;
    private boolean allowRepeat = false;
    private boolean foreceMutation = false;

    private static GeneticForm ourInstance = new GeneticForm();

    public static GeneticForm getInstance() {
        return ourInstance;
    }

    public String getPopulationSize() {
        return populationSize;
    }

    public String getMaxGenerations() {
        return maxGenerations;
    }

    public String getDesiredFitness() {
        return desiredFitness;
    }

    public String getDesiredPrecision() {
        return desiredPrecision;
    }

    public String getTournamentSize() {
        return tournamentSize;
    }

    public String getMinComponentValue() {
        return minComponentValue;
    }

    public String getMaxComponentValue() {
        return maxComponentValue;
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

    public boolean isForeceMutation() {
        return foreceMutation;
    }

    public void setPopulationSize(String populationSize) {
        this.populationSize = populationSize;
    }

    public void setMaxGenerations(String maxGenerations) {
        this.maxGenerations = maxGenerations;
    }

    public void setDesiredFitness(String desiredFitness) {
        this.desiredFitness = desiredFitness;
    }

    public void setDesiredPrecision(String desiredPrecision) {
        this.desiredPrecision = desiredPrecision;
    }

    public void setTournamentSize(String tournamentSize) {
        this.tournamentSize = tournamentSize;
    }

    public void setMinComponentValue(String minComponentValue) {
        this.minComponentValue = minComponentValue;
    }

    public void setMaxComponentValue(String maxComponentValue) {
        this.maxComponentValue = maxComponentValue;
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

    public void setForeceMutation(boolean foreceMutation) {
        this.foreceMutation = foreceMutation;
    }
}
