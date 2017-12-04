package hr.fer.zemris.project.forecasting.tdnn.example;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.fer.zemris.heuristic.IHeuristic;
import hr.fer.zemris.heuristic.metaheuristic.SimulatedAnnealing;
import hr.fer.zemris.heuristic.metaheuristic.model.AbstractCoolingSchedule;
import hr.fer.zemris.heuristic.metaheuristic.model.GeometricCoolingSchedule;
import hr.fer.zemris.model.problems.FunctionMinimizationProblem;
import hr.fer.zemris.model.problems.IOptimizationProblem;
import hr.fer.zemris.model.solution.AbstractOptimizationSolution;
import hr.fer.zemris.model.solution.RealVectorSolution;
import hr.fer.zemris.model.solution.neighbour.generator.INeighbourGenerator;
import hr.fer.zemris.model.solution.neighbour.generator.RealVectorGaussianGenerator;
import hr.fer.zemris.numeric.AbstractFunction;
import hr.fer.zemris.project.forecasting.tdnn.TDNN;
import hr.fer.zemris.project.forecasting.tdnn.model.DataEntry;
import hr.fer.zemris.project.forecasting.tdnn.model.MeanSquaredErrorFunction;
import hr.fer.zemris.project.forecasting.tdnn.util.DataUtil;
import hr.fer.zemris.project.forecasting.util.Pair;
import hr.fer.zemris.project.forecasting.util.Util;

public final class SimulatedAnnealingTrain {
    public static void main(String[] args) throws IOException {
        List<Double> dataset = Util.readDataset("./datasets/exchange-rate-twi-may-1970-aug-1.csv");
        Pair<List<Double>, List<Double>> splittedDataset = DataUtil.splitDataset(dataset, 0.8);
        List<Double> trainData = splittedDataset.getFirst();
        List<Double> testData = splittedDataset.getSecond();

        final int[] ARCHITECTURE = { 5, 4, 1 };
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
        final int     INITIAL_SEARCH_SPACE_SIZE     = 5;
        final double  SIGMA                         = 0.9;
        final double  MUTATION_PROBABILITY          = 0.1;
        final int     NUMBER_OF_ITERATIONS          = 100;
        final double  INITIAL_TEMPERATURE           = 1000;
        final double  FINAL_TEMPERATURE             = 1e-2;

        AbstractFunction function = new MeanSquaredErrorFunction(tdnn, trainSet);
        IOptimizationProblem problem = new FunctionMinimizationProblem(function);

        AbstractOptimizationSolution initialSolution = new RealVectorSolution(
            -INITIAL_SEARCH_SPACE_SIZE/2,
            INITIAL_SEARCH_SPACE_SIZE/2,
            tdnn.getNumberOfWeights()
        );

        double[] sigma = new double[tdnn.getNumberOfWeights()];
        for (int i = 0; i < sigma.length; i++) {
            sigma[i] = SIGMA;
        }
        INeighbourGenerator normalNeighbourGenerator = new RealVectorGaussianGenerator(sigma, MUTATION_PROBABILITY);

        AbstractCoolingSchedule geometricCoolingSchedule = new GeometricCoolingSchedule(
            NUMBER_OF_ITERATIONS,
            INITIAL_TEMPERATURE,
            FINAL_TEMPERATURE
        );

        IHeuristic simulatedAnnealing = new SimulatedAnnealing(problem, initialSolution, normalNeighbourGenerator, geometricCoolingSchedule);
        RealVectorSolution solution = (RealVectorSolution) simulatedAnnealing.run();

        return solution.getSolution().toArray();
    }
}
