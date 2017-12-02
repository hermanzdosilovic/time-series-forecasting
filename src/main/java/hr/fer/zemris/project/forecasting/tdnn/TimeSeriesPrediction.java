package hr.fer.zemris.project.forecasting.tdnn;

import hr.fer.zemris.heuristic.metaheuristic.ga.AbstractGeneticAlgorithm;
import hr.fer.zemris.heuristic.metaheuristic.ga.GenerationGeneticAlgorithm;
import hr.fer.zemris.model.crossover.BLXAlphaCrossover;
import hr.fer.zemris.model.crossover.ICrossover;
import hr.fer.zemris.model.mutation.IMutation;
import hr.fer.zemris.model.mutation.RealVectorGaussianMutation;
import hr.fer.zemris.model.problems.FunctionMinimizationProblem;
import hr.fer.zemris.model.problems.IOptimizationProblem;
import hr.fer.zemris.model.selections.ISelection;
import hr.fer.zemris.model.selections.TournamentSelection;
import hr.fer.zemris.model.solution.IOptimizationSolution;
import hr.fer.zemris.model.solution.RealVectorSolution;
import hr.fer.zemris.numeric.AbstractFunction;
import hr.fer.zemris.project.forecasting.tdnn.model.DataEntry;
import hr.fer.zemris.project.forecasting.tdnn.model.MeanSquaredErrorFunction;
import hr.fer.zemris.project.forecasting.tdnn.util.DataUtil;
import hr.fer.zemris.project.forecasting.util.Pair;
import hr.fer.zemris.project.forecasting.util.Util;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeSeriesPrediction {

    private static final int[] ARCHITECTURE = {5, 4, 4, 1};

    public static final int POPULATION_SIZE = 100;
    public static final int TOURNAMENT_SIZE = 10;
    public static final double ALPHA = 0.5;
    public static final double MUTATION_PROBABILITY = 0.1;
    public static final int INITIAL_BOUND = 5;
    public static final int MAX_GENERATIONS = 1000;
    public static final double SIGMA = 0.9;

    public static void main(String[] args) throws IOException {
        List<Double> rawData = Util.readDataset("./datasets/exchange-rate-twi-may-1970-aug-1.csv");

        Pair<List<Double>, List<Double>> splittedRawData = DataUtil.splitRawData(rawData, 0.8);
        List<Double> rawTrainData = splittedRawData.getFirst();
        List<Double> rawTestData = splittedRawData.getSecond();

        int inputSize = ARCHITECTURE[0];
        int outputSize = ARCHITECTURE[ARCHITECTURE.length - 1];

        List<DataEntry> trainSet = DataUtil.createDataset(rawTrainData, inputSize, outputSize);
        List<DataEntry> testSet = DataUtil.createDataset(rawTestData, inputSize, outputSize);

        TimeDelayNN tdnn = new TimeDelayNN(ARCHITECTURE);

        AbstractFunction function = new MeanSquaredErrorFunction(tdnn, trainSet);
        IOptimizationProblem problem = new FunctionMinimizationProblem(function);
        List<IOptimizationSolution> initialPopulation = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            initialPopulation.add(new RealVectorSolution(-INITIAL_BOUND, INITIAL_BOUND,
                function.getNumberOfVariables()));
        }
        ISelection selection = new TournamentSelection(TOURNAMENT_SIZE, false);
        ICrossover crossover = new BLXAlphaCrossover(ALPHA);
        double[] sigma = new double[function.getNumberOfVariables()];
        for (int i = 0; i < sigma.length; i++) {
            sigma[i] = SIGMA;
        }
        IMutation mutation = new RealVectorGaussianMutation(sigma, MUTATION_PROBABILITY);
        AbstractGeneticAlgorithm geneticAlgorithm =
            new GenerationGeneticAlgorithm(true, MAX_GENERATIONS, 0, 1e-5, problem, selection,
                crossover, mutation);

        geneticAlgorithm.setInitialPopulation(initialPopulation);
        RealVectorSolution solution = (RealVectorSolution) geneticAlgorithm.run();

        tdnn.setWeights(solution.getSolution().toArray());
        List<Double> actualValues = new ArrayList<>();
        for (DataEntry dataEntry : testSet) {
            actualValues.addAll(tdnn.forward(dataEntry.getInput()));
        }

        List<Double> expectedValues = DataUtil.joinExpectedValues(testSet);

        Map<String, List<Double>> graph = new HashMap<>();
        graph.put("Expected", expectedValues);
        graph.put("Predicted", actualValues);

        Util.plot(graph);
    }

}
