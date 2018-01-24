package hr.fer.zemris.project.forecasting.gui.forms.annForms;

public class GeneticForm {

    private String populationSize = "100";
    private String maxGenerations = "1000";
    private String desiredFitness = "0";
    private String desiredPrecision = "1E-3";
    private String tournamentSize = "5";
    private String minComponentValue = "-5";
    private String maxComponentValue = "5";
    private String alpha = "0.9";
    private String mutationProbability = "0.1";
    private String sigma = "0.9";
    private boolean useElitism = true;
    private boolean allowRepeat = true;
    private boolean forceMutation = true;

    private static GeneticForm ourInstance = new GeneticForm();

    public static GeneticForm getInstance() {
        return ourInstance;
    }

    private GeneticForm(){
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

    public boolean isForceMutation() {
        return forceMutation;
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

    public void setForceMutation(boolean forceMutation) {
        this.forceMutation = forceMutation;
    }
}
