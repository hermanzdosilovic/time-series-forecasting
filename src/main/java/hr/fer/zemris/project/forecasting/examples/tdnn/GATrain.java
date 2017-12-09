package hr.fer.zemris.project.forecasting.examples.tdnn;

import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.ga.IGeneticAlgorithm;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.ga.SimpleGA;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.sa.ISimulatedAnnealing;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.sa.SimpleSA;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.sa.cooling.GeometricCoolingSchedule;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.sa.cooling.ICoolingSchedule;
import com.dosilovic.hermanzvonimir.ecfjava.models.crossovers.BLXAlphaCrossover;
import com.dosilovic.hermanzvonimir.ecfjava.models.crossovers.ICrossover;
import com.dosilovic.hermanzvonimir.ecfjava.models.mutations.IMutation;
import com.dosilovic.hermanzvonimir.ecfjava.models.mutations.RealVectorGaussianMutation;
import com.dosilovic.hermanzvonimir.ecfjava.models.problems.FunctionMinimizationProblem;
import com.dosilovic.hermanzvonimir.ecfjava.models.problems.IProblem;
import com.dosilovic.hermanzvonimir.ecfjava.models.selections.ISelection;
import com.dosilovic.hermanzvonimir.ecfjava.models.selections.TournamentSelection;
import com.dosilovic.hermanzvonimir.ecfjava.numeric.IFunction;
import com.dosilovic.hermanzvonimir.ecfjava.util.RealVector;
import hr.fer.zemris.project.forecasting.metaheuristics.observers.AbstractDataObserver;
import hr.fer.zemris.project.forecasting.metaheuristics.observers.FitnessObserver;
import hr.fer.zemris.project.forecasting.metaheuristics.observers.PenaltyObserver;
import hr.fer.zemris.project.forecasting.nn.ActivationFunction;
import hr.fer.zemris.project.forecasting.nn.TDNN;
import hr.fer.zemris.project.forecasting.nn.functions.MSEFunction;
import hr.fer.zemris.project.forecasting.nn.util.DataEntry;
import hr.fer.zemris.project.forecasting.nn.util.NeuralNetworkUtil;
import hr.fer.zemris.project.forecasting.util.DataReaderUtil;
import hr.fer.zemris.project.forecasting.util.GraphUtil;
import hr.fer.zemris.project.forecasting.util.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GATrain {

    public static void main(String[] args) throws IOException {
        final int[] ARCHITECTURE   = {5, 4, 1};
        int         tdnnInputSize  = ARCHITECTURE[0];
        int         tdnnOutputSize = ARCHITECTURE[ARCHITECTURE.length - 1];

        double[] dataset = DataReaderUtil.readDataset("./datasets/exchange-rate-twi-may-1970-aug-1.csv");

        List<DataEntry> tdnnDataset = NeuralNetworkUtil.createTDNNDateset(dataset, tdnnInputSize, tdnnOutputSize);

        Pair<List<DataEntry>, List<DataEntry>> splittedTDNNDataset =
            NeuralNetworkUtil.splitTDNNDataset(tdnnDataset, 0.8);

        List<DataEntry> trainSet = splittedTDNNDataset.getFirst();
        List<DataEntry> testSet  = splittedTDNNDataset.getSecond();

        TDNN     tdnn           = new TDNN(ActivationFunction.RELU, ARCHITECTURE);
        double[] trainedWeights = train(tdnn, trainSet);

        tdnn.setWeights(trainedWeights);

        plot("Train", tdnn, trainSet);
        plot("Test", tdnn, testSet);
    }

    public static void plot(String graphName, TDNN tdnn, List<DataEntry> dataset) {
        double[] expectedValues  = NeuralNetworkUtil.joinExpectedValues(dataset);
        double[] predictedValues = NeuralNetworkUtil.forward(tdnn, dataset);

        Map<String, double[]> graph = new HashMap<>();
        graph.put("Expected", expectedValues);
        graph.put("Predicted", predictedValues);

        GraphUtil.plot(graph, graphName);
    }

    public static double[] train(TDNN tdnn, List<DataEntry> trainSet) {
        final int     POPULATION_SIZE      = 500;
        final int     TOURNAMENT_SIZE      = 20;
        final boolean ALLOW_REPEAT         = false;
        final double  MIN_COMPONENT_VALUE  = -5;
        final double  MAX_COMPONENT_VALUE  = 5;
        final double  ALPHA                = 0.3;
        final double  MUTATION_PROBABILITY = 0.1;
        final boolean FORCE_MUTATION       = true;
        final double  SIGMA                = 0.9;
        final boolean USE_ELITISM          = true;
        final int     MAX_GENERATIONS      = 2000;
        final double  DESIRED_FITNESS      = 0;
        final double  DESIRED_PRECISION    = 1e-3;

        IFunction<RealVector> function = new MSEFunction<>(tdnn, trainSet);
        IProblem<RealVector>  problem  = new FunctionMinimizationProblem<>(function);
        ISelection<RealVector> selection = new TournamentSelection<>(
            TOURNAMENT_SIZE,
            ALLOW_REPEAT
        );
        ICrossover<RealVector> crossover = new BLXAlphaCrossover<>(ALPHA);
        IMutation<RealVector> mutation = new RealVectorGaussianMutation<>(
            MUTATION_PROBABILITY,
            FORCE_MUTATION,
            SIGMA
        );
        IGeneticAlgorithm<RealVector> geneticAlgorithm = new SimpleGA<>(
            USE_ELITISM,
            MAX_GENERATIONS,
            DESIRED_FITNESS,
            DESIRED_PRECISION,
            problem,
            selection,
            crossover,
            mutation
        );

        AbstractDataObserver fitnessObserver = new FitnessObserver();
        geneticAlgorithm.attachObserver(fitnessObserver);

        RealVector solution = geneticAlgorithm.run(
            RealVector.createCollection(
                POPULATION_SIZE,
                tdnn.getNumberOfWeights(),
                MIN_COMPONENT_VALUE,
                MAX_COMPONENT_VALUE
            )
        );

        GraphUtil.plot("Fitness", fitnessObserver.getData());

        return solution.toArray();
    }
}
