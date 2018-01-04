//package hr.fer.zemris.project.forecasting.examples.tdnn;
//
//import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.ga.IGeneticAlgorithm;
//import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.ga.SimpleOSGA;
//import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.sa.cooling.GeometricCoolingSchedule;
//import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.sa.cooling.ICoolingSchedule;
//import com.dosilovic.hermanzvonimir.ecfjava.models.crossovers.BLXAlphaCrossover;
//import com.dosilovic.hermanzvonimir.ecfjava.models.crossovers.ICrossover;
//import com.dosilovic.hermanzvonimir.ecfjava.models.mutations.IMutation;
//import com.dosilovic.hermanzvonimir.ecfjava.models.mutations.RealVectorGaussianMutation;
//import com.dosilovic.hermanzvonimir.ecfjava.models.problems.FunctionMinimizationProblem;
//import com.dosilovic.hermanzvonimir.ecfjava.models.problems.IProblem;
//import com.dosilovic.hermanzvonimir.ecfjava.models.selections.ISelection;
//import com.dosilovic.hermanzvonimir.ecfjava.models.selections.TournamentSelection;
//import com.dosilovic.hermanzvonimir.ecfjava.numeric.IFunction;
//import com.dosilovic.hermanzvonimir.ecfjava.util.RealVector;
//import hr.fer.zemris.project.forecasting.metaheuristics.observers.AbstractDataObserver;
//import hr.fer.zemris.project.forecasting.metaheuristics.observers.FitnessObserver;
//import hr.fer.zemris.project.forecasting.nn.ActivationFunction;
//import hr.fer.zemris.project.forecasting.nn.INeuralNetwork;
//import hr.fer.zemris.project.forecasting.nn.TDNN;
//import hr.fer.zemris.project.forecasting.nn.functions.MSEFunction;
//import hr.fer.zemris.project.forecasting.nn.util.DataEntry;
//import hr.fer.zemris.project.forecasting.nn.util.NeuralNetworkUtil;
//import hr.fer.zemris.project.forecasting.util.DataReaderUtil;
//import hr.fer.zemris.project.forecasting.util.GraphUtil;
//import hr.fer.zemris.project.forecasting.util.Pair;
//
//import java.io.IOException;
//import java.util.List;
//
//public final class OSGATrain {
//
//    public static void main(String[] args) throws IOException {
//        final int[] ARCHITECTURE   = {5, 4, 1};
//        int         tdnnInputSize  = ARCHITECTURE[0];
//        int         tdnnOutputSize = ARCHITECTURE[ARCHITECTURE.length - 1];
//
//        double[] dataset = DataReaderUtil.readDataset("./datasets/exchange-rate-twi-may-1970-aug-1.csv");
//
//        List<DataEntry> tdnnDataset = NeuralNetworkUtil.createTDNNDateset(dataset, tdnnInputSize, tdnnOutputSize);
//
//        Pair<List<DataEntry>, List<DataEntry>> splittedTDNNDataset =
//            NeuralNetworkUtil.splitTDNNDataset(tdnnDataset, 0.8);
//
//        List<DataEntry> trainSet = splittedTDNNDataset.getFirst();
//        List<DataEntry> testSet  = splittedTDNNDataset.getSecond();
//
//        INeuralNetwork tdnn           = new TDNN(ActivationFunction.RELU, ARCHITECTURE);
//        double[]       trainedWeights = train(tdnn, trainSet);
//
//        tdnn.setWeights(trainedWeights);
//
//        NeuralNetworkUtil.plot("Train", tdnn, trainSet);
//        NeuralNetworkUtil.plot("Test", tdnn, testSet);
//        NeuralNetworkUtil.plot("Dataset", tdnn, tdnnDataset);
//    }
//
//    public static double[] train(INeuralNetwork neuralNetwork, List<DataEntry> trainSet) {
//        final int     POPULATION_SIZE        = 500;
//        final int     MAX_GENERATIONS        = 100;
//        final boolean USE_ELITISM            = true;
//        final double  MAX_SELECTION_PRESSURE = 1000;
//        final double  DESIRED_FITNESS        = 0;
//        final double  DESIRED_PRECISION      = 1e-3;
//        final double  MIN_COMPONENT_VALUE    = -5;
//        final double  MAX_COMPONENT_VALUE    = 5;
//        final double  MIN_SUCCESS_RATIO      = 0.3;
//        final double  MAX_SUCCESS_RATIO      = 1;
//        final double  MIN_COMPARISON_FACTOR  = 0.5;
//        final double  MAX_COMPARISON_FACTOR  = 1;
//        final int     TOURNAMENT_SIZE        = 20;
//        final boolean ALLOW_REPEAT           = false;
//        final double  ALPHA                  = 0.3;
//        final double  MUTATION_PROBABILITY   = 0.1;
//        final boolean FORCE_MUTATION         = true;
//        final double  SIGMA                  = 0.9;
//
//        IFunction<RealVector> function = new MSEFunction<>(neuralNetwork, trainSet);
//        IProblem<RealVector>  problem  = new FunctionMinimizationProblem<>(function);
//        ISelection<RealVector> selection = new TournamentSelection<>(
//            TOURNAMENT_SIZE,
//            ALLOW_REPEAT
//        );
//        ICrossover<RealVector> crossover = new BLXAlphaCrossover<>(ALPHA);
//        IMutation<RealVector> mutation = new RealVectorGaussianMutation<>(
//            MUTATION_PROBABILITY,
//            FORCE_MUTATION,
//            SIGMA
//        );
//        ICoolingSchedule successRatioSchedule = new GeometricCoolingSchedule(
//            MAX_GENERATIONS,
//            MIN_SUCCESS_RATIO,
//            MAX_SUCCESS_RATIO
//        );
//        ICoolingSchedule comparisonFactorSchedule = new GeometricCoolingSchedule(
//            MAX_GENERATIONS,
//            MIN_COMPARISON_FACTOR,
//            MAX_COMPARISON_FACTOR
//        );
//        IGeneticAlgorithm<RealVector> geneticAlgorithm = new SimpleOSGA<>(
//            USE_ELITISM,
//            MAX_GENERATIONS,
//            DESIRED_FITNESS,
//            DESIRED_PRECISION,
//            MAX_SELECTION_PRESSURE,
//            successRatioSchedule,
//            comparisonFactorSchedule,
//            problem,
//            selection,
//            crossover,
//            mutation
//        );
//
//        AbstractDataObserver fitnessObserver = new FitnessObserver();
//        geneticAlgorithm.attachObserver(fitnessObserver);
//
//        RealVector solution = geneticAlgorithm.run(
//            RealVector.createCollection(
//                POPULATION_SIZE,
//                neuralNetwork.getNumberOfWeights(),
//                MIN_COMPONENT_VALUE,
//                MAX_COMPONENT_VALUE
//            )
//        );
//
//        GraphUtil.plot("Fitness", fitnessObserver.getData());
//
//        return solution.toArray();
//    }
//}
