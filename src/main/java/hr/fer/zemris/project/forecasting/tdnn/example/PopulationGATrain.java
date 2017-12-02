package hr.fer.zemris.project.forecasting.tdnn.example;

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
import hr.fer.zemris.project.forecasting.tdnn.TDNN;
import hr.fer.zemris.project.forecasting.tdnn.model.DataEntry;
import hr.fer.zemris.project.forecasting.tdnn.model.MeanSquaredErrorFunction;
import hr.fer.zemris.project.forecasting.tdnn.util.DataUtil;
import hr.fer.zemris.project.forecasting.util.Pair;
import hr.fer.zemris.project.forecasting.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PopulationGATrain {

    public static void main(String[] args) throws IOException {
        List<Double> dataset = Util.readDataset("./datasets/exchange-rate-twi-may-1970-aug-1.csv");
        Pair<List<Double>, List<Double>> splittedDataset = DataUtil.splitDataset(dataset, 0.8);
        List<Double> trainData = splittedDataset.getFirst();
        List<Double> testData = splittedDataset.getSecond();

        final int[] ARCHITECTURE = { 5, 4, 4, 1 };
        int tdnnInputSize = ARCHITECTURE[0];
        int tdnnOutputSize = ARCHITECTURE[ARCHITECTURE.length - 1];

        List<DataEntry> trainSet = DataUtil.createTDNNDateset(trainData, tdnnInputSize, tdnnOutputSize);
        List<DataEntry> testSet = DataUtil.createTDNNDateset(testData, tdnnInputSize, tdnnOutputSize);

        TDNN tdnn = new TDNN(ARCHITECTURE);
        double[] trainedWeights = train(tdnn, trainSet);

        tdnn.setWeights(trainedWeights);
        double trainError = DataUtil.calculateMeanSquaredError(tdnn, trainSet);
        double testError = DataUtil.calculateMeanSquaredError(tdnn, testSet);

        System.err.printf("\nTrain MSE: %f\nTest  MSE: %f\n\n", trainError, testError);

        List<Double> expectedValues = DataUtil.joinExpectedValues(testSet);
        List<Double> predictedValues   = DataUtil.forward(tdnn, testSet);

        Map<String, List<Double>> graph = new HashMap<>();
        graph.put("Expected", expectedValues);
        graph.put("Predicted", predictedValues);

        Util.plot(graph);
    }

    public static double[] train(TDNN tdnn, List<DataEntry> trainSet) {
        final int     POPULATION_SIZE               = 200;
        final int     INITIAL_SEARCH_SPACE_SIZE     = 10;
        final int     TOURNAMENT_SIZE               = 5;
        final boolean SELECT_WITH_REPEAT_TOURNAMENT = false;
        final double  ALPHA                         = 0.3;
        final double  SIGMA                         = 0.9;
        final double  MUTATION_PROBABILITY          = 0.1;
        final boolean ENABLE_ELITISM                = true;
        final int     MAX_GENERATIONS               = 1000;
        final double  DESIRED_FITNESS               = 0;
        final double  PRECISION                     = 1e-3;

        AbstractFunction function = new MeanSquaredErrorFunction(tdnn, trainSet);
        IOptimizationProblem problem = new FunctionMinimizationProblem(function);

        List<IOptimizationSolution> initialPopulation = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            initialPopulation.add(
                new RealVectorSolution(
                    -INITIAL_SEARCH_SPACE_SIZE/2,
                    INITIAL_SEARCH_SPACE_SIZE/2,
                    tdnn.getNumberOfWeights()
                )
            );
        }

        ISelection selection = new TournamentSelection(TOURNAMENT_SIZE, SELECT_WITH_REPEAT_TOURNAMENT);

        ICrossover crossover = new BLXAlphaCrossover(ALPHA);

        double[] sigma = new double[tdnn.getNumberOfWeights()];
        for (int i = 0; i < sigma.length; i++) {
            sigma[i] = SIGMA;
        }
        IMutation mutation = new RealVectorGaussianMutation(sigma, MUTATION_PROBABILITY);

        AbstractGeneticAlgorithm geneticAlgorithm = new GenerationGeneticAlgorithm(
            ENABLE_ELITISM,
            MAX_GENERATIONS,
            DESIRED_FITNESS,
            PRECISION,
            problem,
            selection,
            crossover,
            mutation
        );

        geneticAlgorithm.setInitialPopulation(initialPopulation);
        RealVectorSolution solution = (RealVectorSolution) geneticAlgorithm.run();

        return solution.getSolution().toArray();
    }
}
