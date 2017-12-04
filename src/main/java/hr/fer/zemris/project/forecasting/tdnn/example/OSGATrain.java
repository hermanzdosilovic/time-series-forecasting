package hr.fer.zemris.project.forecasting.tdnn.example;

import hr.fer.zemris.heuristic.metaheuristic.ga.AbstractGeneticAlgorithm;
import hr.fer.zemris.heuristic.metaheuristic.ga.GenerationGeneticAlgorithm;
import hr.fer.zemris.heuristic.metaheuristic.ga.IGeneticAlgorithm;
import hr.fer.zemris.heuristic.metaheuristic.ga.OffspringSelectionGA;
import hr.fer.zemris.heuristic.metaheuristic.model.ICoolingSchedule;
import hr.fer.zemris.heuristic.metaheuristic.model.LinearCoolingSchedule;
import hr.fer.zemris.model.crossover.BLXAlphaCrossover;
import hr.fer.zemris.model.crossover.ICrossover;
import hr.fer.zemris.model.mutation.IMutation;
import hr.fer.zemris.model.mutation.RealVectorGaussianMutation;
import hr.fer.zemris.model.problems.FunctionMinimizationProblem;
import hr.fer.zemris.model.problems.IOptimizationProblem;
import hr.fer.zemris.model.selections.ISelection;
import hr.fer.zemris.model.selections.RouletteWheelSelection;
import hr.fer.zemris.model.selections.TournamentSelection;
import hr.fer.zemris.model.solution.BitVectorSolution;
import hr.fer.zemris.model.solution.IOptimizationSolution;
import hr.fer.zemris.model.solution.RealVectorSolution;
import hr.fer.zemris.numeric.AbstractFunction;
import hr.fer.zemris.project.forecasting.tdnn.TDNN;
import hr.fer.zemris.project.forecasting.tdnn.model.ActivationFunction;
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

public final class OSGATrain {

    public static void main(String[] args) throws IOException {
        final int[] ARCHITECTURE = {5, 4, 1};
        int tdnnInputSize = ARCHITECTURE[0];
        int tdnnOutputSize = ARCHITECTURE[ARCHITECTURE.length - 1];

        List<Double> dataset = Util.readDataset("./datasets/exchange-rate-twi-may-1970-aug-1.csv");
        List<DataEntry> tdnnDataset =
            DataUtil.createTDNNDateset(dataset, tdnnInputSize, tdnnOutputSize);
        Pair<List<DataEntry>, List<DataEntry>> splittedTDNNDataset =
            DataUtil.splitTDNNDataset(tdnnDataset, 0.8);

        List<DataEntry> trainSet = splittedTDNNDataset.getFirst();
        List<DataEntry> testSet = splittedTDNNDataset.getSecond();

        TDNN tdnn = new TDNN(ActivationFunction.RELU, ARCHITECTURE);
        double[] trainedWeights = train(tdnn, trainSet);

        tdnn.setWeights(trainedWeights);
        double trainError = DataUtil.calculateMeanSquaredError(tdnn, trainSet);
        double testError = DataUtil.calculateMeanSquaredError(tdnn, testSet);

        System.err.printf("\nTrain MSE: %f\nTest  MSE: %f\n\n", trainError, testError);

        plot(tdnn, trainSet);
        plot(tdnn, testSet);
    }

    public static void plot(TDNN tdnn, List<DataEntry> dataset) {
        List<Double> expectedValues = DataUtil.joinExpectedValues(dataset);
        List<Double> predictedValues = DataUtil.forward(tdnn, dataset);

        Map<String, List<Double>> graph = new HashMap<>();
        graph.put("Expected", expectedValues);
        graph.put("Predicted", predictedValues);

        Util.plot(graph);
    }

    public static double[] train(TDNN tdnn, List<DataEntry> trainSet) {
        final int     POPULATION_SIZE               = 1000;
        final int     INITIAL_SEARCH_SPACE_SIZE     = 5;
        final int     TOURNAMENT_SIZE               = 5;
        final boolean SELECT_WITH_REPEAT_TOURNAMENT = false;
        final double  ALPHA                         = 0.3;
        final double  SIGMA                         = 0.9;
        final double  MUTATION_PROBABILITY          = 0.1;
        final int     MAX_GENERATIONS               = 100;
        final double  MIN_COMPARISON_FACTOR         = 0.5;
        final double  MAX_COMPARISON_FACTOR         = 1;
        final double  SUCCESS_RATIO                 = 0.9;
        final double  MAX_SELECTION_PRESSURE        = 1000;
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

        ICoolingSchedule comparisonFactorSchedule = new LinearCoolingSchedule(
            MAX_GENERATIONS,
            MIN_COMPARISON_FACTOR,
            MAX_COMPARISON_FACTOR
        );

        IGeneticAlgorithm geneticAlgorithm = new OffspringSelectionGA(
            MAX_GENERATIONS,
            SUCCESS_RATIO,
            MAX_SELECTION_PRESSURE,
            comparisonFactorSchedule,
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
