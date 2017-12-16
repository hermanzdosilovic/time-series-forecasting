package hr.fer.zemris.project.forecasting.examples.tdnn;

import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.pso.BasicPSO;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.pso.IParticleSwarmOptimization;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.pso.Particle;
import com.dosilovic.hermanzvonimir.ecfjava.models.problems.FunctionMinimizationProblem;
import com.dosilovic.hermanzvonimir.ecfjava.models.problems.IProblem;
import com.dosilovic.hermanzvonimir.ecfjava.util.RealVector;
import com.dosilovic.hermanzvonimir.ecfjava.util.Solution;
import hr.fer.zemris.project.forecasting.nn.ActivationFunction;
import hr.fer.zemris.project.forecasting.nn.INeuralNetwork;
import hr.fer.zemris.project.forecasting.nn.TDNN;
import hr.fer.zemris.project.forecasting.nn.functions.MSEFunction;
import hr.fer.zemris.project.forecasting.nn.util.DataEntry;
import hr.fer.zemris.project.forecasting.nn.util.NeuralNetworkUtil;
import hr.fer.zemris.project.forecasting.util.DataReaderUtil;
import hr.fer.zemris.project.forecasting.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class BasicPSOTrain {

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

        INeuralNetwork tdnn           = new TDNN(ActivationFunction.RELU, ARCHITECTURE);
        double[]       trainedWeights = train(tdnn, trainSet);

        tdnn.setWeights(trainedWeights);

        NeuralNetworkUtil.plot("Train", tdnn, trainSet);
        NeuralNetworkUtil.plot("Test", tdnn, testSet);
        NeuralNetworkUtil.plot("Dataset", tdnn, tdnnDataset);
    }

    public static double[] train(INeuralNetwork neuralNetwork, List<DataEntry> trainSet) {
        final int    NUMBER_OF_PARTICLES = 100;
        final int    MAX_ITERATIONS      = 2000;
        final double DESIRED_FITNESS     = 0;
        final double DESIRED_PRECISION   = 1e-2;
        final double INDIVIDUAL_FACTOR   = 2.05;
        final double SOCIAL_FACTOR       = 2.05;
        final double MIN_VALUE           = -20;
        final double MAX_VALUE           = 20;
        final double MIN_SPEED           = -0.1;
        final double MAX_SPEED           = 0.1;

        IProblem<RealVector> problem = new FunctionMinimizationProblem<>(new MSEFunction<>(neuralNetwork, trainSet));

        Collection<Particle<RealVector>> initialParticles = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_PARTICLES; i++) {
            initialParticles.add(new Particle<>(
                new Solution<>(new RealVector(neuralNetwork.getNumberOfWeights(), MIN_VALUE, MAX_VALUE)),
                new RealVector(neuralNetwork.getNumberOfWeights(), MIN_SPEED, MAX_SPEED)
            ));
        }

        IParticleSwarmOptimization<RealVector> iParticleSwarmOptimization = new BasicPSO<>(
            MAX_ITERATIONS,
            DESIRED_FITNESS,
            DESIRED_PRECISION,
            INDIVIDUAL_FACTOR,
            SOCIAL_FACTOR,
            new RealVector(neuralNetwork.getNumberOfWeights(), MIN_VALUE),
            new RealVector(neuralNetwork.getNumberOfWeights(), MAX_VALUE),
            new RealVector(neuralNetwork.getNumberOfWeights(), MIN_SPEED),
            new RealVector(neuralNetwork.getNumberOfWeights(), MAX_SPEED),
            problem
        );

        RealVector solution = iParticleSwarmOptimization.run(initialParticles);

        return solution.toArray();
    }
}
