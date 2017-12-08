package hr.fer.zemris.project.forecasting.examples.tdnn;

import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.sa.ISimulatedAnnealing;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.sa.SimpleSA;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.sa.cooling.GeometricCoolingSchedule;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.sa.cooling.ICoolingSchedule;
import com.dosilovic.hermanzvonimir.ecfjava.models.mutations.IMutation;
import com.dosilovic.hermanzvonimir.ecfjava.models.mutations.RealVectorGaussianMutation;
import com.dosilovic.hermanzvonimir.ecfjava.models.problems.FunctionMinimizationProblem;
import com.dosilovic.hermanzvonimir.ecfjava.models.problems.IProblem;
import com.dosilovic.hermanzvonimir.ecfjava.numeric.IFunction;
import com.dosilovic.hermanzvonimir.ecfjava.util.RealVector;
import hr.fer.zemris.project.forecasting.nn.ActivationFunction;
import hr.fer.zemris.project.forecasting.nn.TDNN;
import hr.fer.zemris.project.forecasting.nn.functions.MSEFunction;
import hr.fer.zemris.project.forecasting.nn.util.DataEntry;
import hr.fer.zemris.project.forecasting.nn.util.NeuralNetworkUtil;
import hr.fer.zemris.project.forecasting.util.DataReaderUtil;
import hr.fer.zemris.project.forecasting.util.GraphUtil;
import hr.fer.zemris.project.forecasting.util.Pair;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class SimulatedAnnealingTrain {
    public static void main(String[] args) throws IOException {
        final int[] ARCHITECTURE   = {5, 4, 1};
        int         tdnnInputSize  = ARCHITECTURE[0];
        int         tdnnOutputSize = ARCHITECTURE[ARCHITECTURE.length - 1];

        double[] dataset = DataReaderUtil.readDataset("./datasets/exchange-rate-twi-may-1970-aug-1.csv");

        List<DataEntry> tdnnDataset =
            NeuralNetworkUtil.createTDNNDateset(dataset, tdnnInputSize, tdnnOutputSize);
        Pair<List<DataEntry>, List<DataEntry>> splittedTDNNDataset =
            NeuralNetworkUtil.splitTDNNDataset(tdnnDataset, 0.8);

        List<DataEntry> trainSet = splittedTDNNDataset.getFirst();
        List<DataEntry> testSet  = splittedTDNNDataset.getSecond();

        TDNN     tdnn           = new TDNN(ActivationFunction.RELU, ARCHITECTURE);
        double[] trainedWeights = train(tdnn, trainSet);

        tdnn.setWeights(trainedWeights);

        plot(tdnn, trainSet);
        plot(tdnn, testSet);
    }

    public static void plot(TDNN tdnn, List<DataEntry> dataset) {
        double[] expectedValues  = NeuralNetworkUtil.joinExpectedValues(dataset);
        double[] predictedValues = NeuralNetworkUtil.forward(tdnn, dataset);

        Map<String, double[]> graph = new HashMap<>();
        graph.put("Expected", expectedValues);
        graph.put("Predicted", predictedValues);

        GraphUtil.plot(graph);
    }

    public static double[] train(TDNN tdnn, List<DataEntry> trainSet) {
        final double  MIN_COMPONENT_VALUE  = -10;
        final double  MAX_COMPONENT_VALUE  = 10;
        final int     OUTER_ITERATIONS     = 4000;
        final double  OUTER_INITIAL_TEMP   = 1000;
        final double  OUTER_FINAL_TEMP     = 1e-4;
        final int     INNER_ITERATIONS     = 1000;
        final double  INNER_INITIAL_TEMP   = 500;
        final double  INNER_FINAL_TEMP     = 1e-4;
        final double  MUTATION_PROBABILITY = 0.2;
        final boolean FORCE_MUTATION       = true;
        final double  SIGMA                = 0.9;
        final double  DESIRED_PENALTY      = 0;
        final double  DESIRED_PRECISION    = 1e-3;

        IFunction<RealVector> function = new MSEFunction<>(tdnn, trainSet);
        IProblem<RealVector>  problem  = new FunctionMinimizationProblem<>(function);
        ICoolingSchedule outerCoolingSchedule = new GeometricCoolingSchedule(
            OUTER_ITERATIONS,
            OUTER_INITIAL_TEMP,
            OUTER_FINAL_TEMP
        );
        ICoolingSchedule innerCoolingSchedule = new GeometricCoolingSchedule(
            INNER_ITERATIONS,
            INNER_INITIAL_TEMP,
            INNER_FINAL_TEMP
        );
        IMutation<RealVector> mutation = new RealVectorGaussianMutation<>(MUTATION_PROBABILITY, FORCE_MUTATION, SIGMA);
        ISimulatedAnnealing<RealVector> simulatedAnnealing = new SimpleSA<>(
            DESIRED_PENALTY,
            DESIRED_PRECISION,
            problem,
            mutation,
            outerCoolingSchedule,
            innerCoolingSchedule
        );

        ArrayRealVector solution = simulatedAnnealing.run(createInitialSolution(
            tdnn.getNumberOfWeights(),
            MIN_COMPONENT_VALUE,
            MAX_COMPONENT_VALUE
        ));

        return solution.toArray();
    }

    public static RealVector createInitialSolution(
        int numberOfComponents,
        double minComponentValue,
        double maxComponentValue
    ) {
        Random rand = new Random();

        RealVector vector = new RealVector(numberOfComponents);
        for (int i = 0; i < numberOfComponents; i++) {
            vector.setEntry(i, minComponentValue + rand.nextDouble() * (maxComponentValue - minComponentValue));
        }
        return vector;
    }
}
