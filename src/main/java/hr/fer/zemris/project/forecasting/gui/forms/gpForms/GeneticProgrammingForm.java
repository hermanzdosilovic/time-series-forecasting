package hr.fer.zemris.project.forecasting.gui.forms.gpForms;

public class GeneticProgrammingForm {

    private String  populationSize          = "600";
    private String  maxGenerations          = "100";
    private String  desiredFitness          = "0.0";
    private String  tournamentSize          = "5";
    private String  startDepth              = "6";
    private String  maxDepth                = "20";
    private String  maxNodes                = "200";
    private String  reproductionProbability = "0.01";
    private String  mutationProbability     = "0.14";
    private String  offset                  = "5";
    private boolean useElitism              = true;
    private boolean allowDuplicates         = false;
    private int     percentage              = 9;

    private static GeneticProgrammingForm ourInstance = new GeneticProgrammingForm();

    public static GeneticProgrammingForm getInstance() {
        return ourInstance;
    }

    public String getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(String populationSize) {
        this.populationSize = populationSize;
    }

    public String getMaxGenerations() {
        return maxGenerations;
    }

    public void setMaxGenerations(String maxGenerations) {
        this.maxGenerations = maxGenerations;
    }

    public String getDesiredFitness() {
        return desiredFitness;
    }

    public void setDesiredFitness(String desiredFitness) {
        this.desiredFitness = desiredFitness;
    }

    public String getTournamentSize() {
        return tournamentSize;
    }

    public void setTournamentSize(String tournamentSize) {
        this.tournamentSize = tournamentSize;
    }

    public String getStartDepth() {
        return startDepth;
    }

    public void setStartDepth(String startDepth) {
        this.startDepth = startDepth;
    }

    public String getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(String maxDepth) {
        this.maxDepth = maxDepth;
    }

    public String getMaxNodes() {
        return maxNodes;
    }

    public void setMaxNodes(String maxNodes) {
        this.maxNodes = maxNodes;
    }

    public String getReproductionProbability() {
        return reproductionProbability;
    }

    public void setReproductionProbability(String reproductionProbability) {
        this.reproductionProbability = reproductionProbability;
    }

    public String getMutationProbability() {
        return mutationProbability;
    }

    public void setMutationProbability(String mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    public boolean isUseElitism() {
        return useElitism;
    }

    public void setUseElitism(boolean useElitism) {
        this.useElitism = useElitism;
    }

    public boolean isAllowDuplicates() {
        return allowDuplicates;
    }

    public void setAllowDuplicates(boolean allowDuplicates) {
        this.allowDuplicates = allowDuplicates;
    }


    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
