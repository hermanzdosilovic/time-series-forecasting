package hr.fer.zemris.project.forecasting.gui.builders;

import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.IMetaheuristic;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.ga.IGeneticAlgorithm;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.ga.SimpleGA;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.ga.SimpleOSGA;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.pso.BasicPSO;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.pso.IParticleSwarmOptimization;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.pso.topologies.FullyConnectedTopology;
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
import com.dosilovic.hermanzvonimir.ecfjava.models.solutions.ISolution;
import com.dosilovic.hermanzvonimir.ecfjava.models.solutions.factories.ISolutionFactory;
import com.dosilovic.hermanzvonimir.ecfjava.models.solutions.factories.ParticleFactory;
import com.dosilovic.hermanzvonimir.ecfjava.models.solutions.factories.RealVectorFactory;
import com.dosilovic.hermanzvonimir.ecfjava.models.solutions.factories.SimpleSolutionFactory;
import com.dosilovic.hermanzvonimir.ecfjava.models.solutions.vector.RealVector;
import com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork;
import com.dosilovic.hermanzvonimir.ecfjava.neural.errors.MSEFunction;
import com.dosilovic.hermanzvonimir.ecfjava.numeric.IFunction;
import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import hr.fer.zemris.project.forecasting.gui.AlgorithmsGUI;
import hr.fer.zemris.project.forecasting.gui.forms.annForms.*;
import hr.fer.zemris.project.forecasting.nn.backpropagation.Backpropagation;

import java.util.Collection;
import java.util.List;

import static hr.fer.zemris.project.forecasting.gui.AlgorithmsGUI.INNER_FINAL_TEMP;
import static hr.fer.zemris.project.forecasting.gui.AlgorithmsGUI.INNER_INITIAL_TEMP;

public class MetaheuristicBuilder {

    public static IMetaheuristic createBackpropagation(List<DatasetEntry> trainingSet, List<DatasetEntry> validationSet,
                                                       INeuralNetwork neuralNetwork) {
        BackpropagationForm backpropagationForm = BackpropagationForm.getInstance();
        Backpropagation backpropagation = new Backpropagation(
                trainingSet,
                validationSet,
                Double.parseDouble(backpropagationForm.getLearningRate()),
                Long.parseLong(backpropagationForm.getMaxIteration()),
                Double.parseDouble(backpropagationForm.getDesiredError()),
                Double.parseDouble(backpropagationForm.getDesiredPrecision()),
                neuralNetwork,
                Integer.parseInt(backpropagationForm.getBatchSize())
        );

        return backpropagation;
    }

    public static IMetaheuristic createSimpleGA(List<DatasetEntry> dataset, INeuralNetwork neuralNetwork) {
        GeneticForm geneticForm = GeneticForm.getInstance();
        int generationSize = Integer.parseInt(geneticForm.getMaxGenerations());
        int tournamentSize = Integer.parseInt(geneticForm.getTournamentSize());
        double alpha = Double.parseDouble(geneticForm.getAlpha());
        double mutationP = Double.parseDouble(geneticForm.getMutationProbability());
        double sigma = Double.parseDouble(geneticForm.getSigma());
        boolean forceMutations = geneticForm.isForceMutation();
        boolean elitism = geneticForm.isUseElitism();
        boolean repeat = geneticForm.isAllowRepeat();
        double desiredFittnes = Double.parseDouble(geneticForm.getDesiredFitness());
        double desiredPrec = Double.parseDouble(geneticForm.getDesiredPrecision());


        IProblem<RealVector> problem = new FunctionMinimizationProblem<>(new MSEFunction<>(neuralNetwork,
                dataset.toArray(new DatasetEntry[dataset.size()])));
        ISelection<RealVector> selection = new TournamentSelection<>(
                tournamentSize,
                repeat
        );
        ICrossover<RealVector> crossover = new BLXAlphaCrossover<>(alpha);
        IMutation<RealVector> mutationValue = new RealVectorGaussianMutation<>(
                mutationP,
                forceMutations,
                sigma
        );
        SimpleGA<RealVector> simpleGA = new SimpleGA<>(elitism, generationSize, false, desiredFittnes, desiredPrec,
                problem, selection, crossover, mutationValue);


        return simpleGA;
    }

    public static Collection<ISolution<RealVector>> createSimpleGARequirements(int neuralNetworkParametersNumber) {
        GeneticForm geneticForm = GeneticForm.getInstance();
        double minComponentValue = Double.parseDouble(geneticForm.getMinComponentValue());
        double maxComponentValue = Double.parseDouble(geneticForm.getMaxComponentValue());
        int populationSize = Integer.parseInt(geneticForm.getPopulationSize());
        ISolutionFactory<RealVector> solutionFactory = new SimpleSolutionFactory<>(
                new RealVectorFactory(
                        new RealVector(neuralNetworkParametersNumber, minComponentValue, maxComponentValue)
                )
        );
        return solutionFactory.createMultipleInstances(populationSize);
    }

    public static IMetaheuristic createSimpleOSGA(List<DatasetEntry> dataset, INeuralNetwork neuralNetwork) {
        OSGAForm osgaForm = OSGAForm.getInstance();
        int generationSize = Integer.parseInt(osgaForm.getMaxGenerations());
        int tournamentSize = Integer.parseInt(osgaForm.getTournamentSize());
        double alpha = Double.parseDouble(osgaForm.getAlpha());
        double mutationP = Double.parseDouble(osgaForm.getMutationProbability());
        double sigma = Double.parseDouble(osgaForm.getSigma());
        double maxP = Double.parseDouble(osgaForm.getMaxSelectionPressure());
        double minSuccessRatio = Double.parseDouble(osgaForm.getMinSuccessRatio());
        double maxSuccessRatio = Double.parseDouble(osgaForm.getMaxSuccessRatio());
        double minComparisonRatio = Double.parseDouble(osgaForm.getMinComparisionFactor());
        double maxComparisonRatio = Double.parseDouble(osgaForm.getMaxComparisionFactor());
        boolean forceMutations = osgaForm.isForceMutation();
        boolean elitism = osgaForm.isUseElitism();
        boolean doRepeat = osgaForm.isAllowRepeat();
        double fitness = Double.parseDouble(osgaForm.getDesiredFitness());
        double desiredPrec = Double.parseDouble(osgaForm.getDesiredPrecision());

        IFunction<RealVector> function = new MSEFunction<>(neuralNetwork, dataset.toArray(new DatasetEntry[dataset.size()]));
        IProblem<RealVector> problem = new FunctionMinimizationProblem<>(function);
        ISelection<RealVector> selection = new TournamentSelection<>(
                tournamentSize,
                doRepeat
        );
        ICrossover<RealVector> crossover = new BLXAlphaCrossover<>(alpha);
        IMutation<RealVector> mutationValue = new RealVectorGaussianMutation<>(
                mutationP,
                forceMutations,
                sigma
        );
        ICoolingSchedule successRatioSchedule = new GeometricCoolingSchedule(
                generationSize,
                minSuccessRatio,
                maxSuccessRatio
        );
        ICoolingSchedule comparisonFactorSchedule = new GeometricCoolingSchedule(
                generationSize,
                minComparisonRatio,
                maxComparisonRatio
        );
        IGeneticAlgorithm<RealVector> geneticAlgorithm = new SimpleOSGA<>(
                elitism,
                generationSize,
                false,
                fitness,
                desiredPrec,
                maxP,
                successRatioSchedule,
                comparisonFactorSchedule,
                problem,
                selection,
                crossover,
                mutationValue
        );

        return geneticAlgorithm;
    }

    public static Collection<ISolution<RealVector>> createOSGARequirements(int neuralNetworkParametersNumber) {
        OSGAForm osgaForm = OSGAForm.getInstance();
        int populationSize = Integer.parseInt(osgaForm.getPopulationSize());
        double minComponentValue = Double.parseDouble(osgaForm.getMinComponentValue());
        double maxComponentValue = Double.parseDouble(osgaForm.getMaxComponentValue());
        ISolutionFactory<RealVector> solutionFactory = new SimpleSolutionFactory<>(
                new RealVectorFactory(
                        new RealVector(neuralNetworkParametersNumber, minComponentValue, maxComponentValue)
                )
        );
        return solutionFactory.createMultipleInstances(populationSize);
    }

    public static IMetaheuristic createSimpleSA(List<DatasetEntry> dataset, INeuralNetwork neuralNetwork) {
        SAForm saForm = SAForm.getInstance();
        int outerIteration = Integer.parseInt(saForm.getOuterIterations());
        int innerIteration = Integer.parseInt(saForm.getInnerIterations());
        double outerTemperature = Double.parseDouble(saForm.getOuterInitialTemperature());
        double outerFinalTemp = Double.parseDouble(saForm.getOuterFinalTemperature());
        double mutationP = Double.parseDouble(saForm.getMutationProbability());
        double sigma = Double.parseDouble(saForm.getSigma());
        boolean forceMutations = saForm.isForceMutation();
        double fitness = Double.parseDouble(saForm.getDesiredFitness());
        double desiredPrec = Double.parseDouble(saForm.getDesiredPrecision());

        IFunction<RealVector> function = new MSEFunction<>(neuralNetwork, dataset.toArray(new DatasetEntry[dataset.size()]));
        IProblem<RealVector> problem = new FunctionMinimizationProblem<>(function);
        ICoolingSchedule outerCoolingSchedule = new GeometricCoolingSchedule(
                outerIteration,
                outerTemperature,
                outerFinalTemp
        );
        ICoolingSchedule innerCoolingSchedule = new GeometricCoolingSchedule(
                innerIteration,
                INNER_INITIAL_TEMP,
                INNER_FINAL_TEMP
        );
        IMutation<RealVector> mutationValue = new RealVectorGaussianMutation<>(mutationP, forceMutations, sigma);
        ISimulatedAnnealing<RealVector> simulatedAnnealing = new SimpleSA<>(
                desiredPrec,
                fitness,
                problem,
                mutationValue,
                outerCoolingSchedule,
                innerCoolingSchedule
        );
        return simulatedAnnealing;
    }

    public static RealVector createSARequirements(int neuralNetworkParametersNumber) {
        SAForm saForm = SAForm.getInstance();
        double minComponentValue = Double.parseDouble(saForm.getMinComponentValue());
        double maxComponentValue = Double.parseDouble(saForm.getMaxComponentValue());
        return new RealVector(
                neuralNetworkParametersNumber,
                minComponentValue,
                maxComponentValue
        );
    }

    public static IMetaheuristic createSimplePSO(List<DatasetEntry> dataset, INeuralNetwork neuralNetwork) {
        PSOForm psoForm = PSOForm.getInstance();
        int maxIter = Integer.parseInt(psoForm.getMaxIteration());
        double minSpeed = Double.parseDouble(psoForm.getMinSpeed());
        double maxSpeed = Double.parseDouble(psoForm.getMaxSpeed());
        double individualFact = Double.parseDouble(psoForm.getIndividualFactor());
        double socialFact = Double.parseDouble(psoForm.getSocialFactor());
        double desiredFittnes = Double.parseDouble(psoForm.getDesiredFitness());
        double desiredPrec = Double.parseDouble(psoForm.getDesiredPrecision());
        boolean isFullyInformed = psoForm.isFullyFormed();

        IProblem<RealVector> problem = new FunctionMinimizationProblem<>(new MSEFunction<>(neuralNetwork,
                dataset.toArray(new DatasetEntry[dataset.size()])));

        IParticleSwarmOptimization<RealVector> particleSwarmOptimization = new BasicPSO<>(
                maxIter,
                desiredFittnes,
                desiredPrec,
                isFullyInformed,
                individualFact,
                socialFact,
                new RealVector(neuralNetwork.getNumberOfParameters(), minSpeed),
                new RealVector(neuralNetwork.getNumberOfParameters(), maxSpeed),
                problem,
                new FullyConnectedTopology<>()
        );

        return particleSwarmOptimization;
    }

    public static Object createPSORequirements(int neuralNetworkParametersNumber) {
        PSOForm psoForm = PSOForm.getInstance();
        long particlesNum = Long.parseLong(psoForm.getNumberOfParticles());
        double minValue = Double.parseDouble(psoForm.getMinValue());
        double maxValue = Double.parseDouble(psoForm.getMaxValue());
        double minSpeed = Double.parseDouble(psoForm.getMinSpeed());
        double maxSpeed = Double.parseDouble(psoForm.getMaxSpeed());
        ISolutionFactory<RealVector> particleFactory = new ParticleFactory<>(
                new SimpleSolutionFactory<>(
                        new RealVectorFactory(
                                new RealVector(neuralNetworkParametersNumber, minValue, maxValue, true)
                        )
                ),
                new RealVectorFactory(
                        new RealVector(neuralNetworkParametersNumber, minSpeed, maxSpeed)
                )
        );

        return particleFactory.createMultipleInstances((int) particlesNum);
    }

    public static void createNewInstance(IMetaheuristic metaheuristic, INeuralNetwork neuralNetwork, List<DatasetEntry> dataset) {
        if (metaheuristic instanceof Backpropagation) {
            AlgorithmsGUI.metaheuristic = createBackpropagation(((Backpropagation) metaheuristic).getTrainingSet(),
                    ((Backpropagation) metaheuristic).getValidationSet(), neuralNetwork);
        } else if (metaheuristic instanceof SimpleGA) {
            AlgorithmsGUI.metaheuristic = createSimpleGA(dataset, neuralNetwork);
            AlgorithmsGUI.metaheuristicRequirement = createSimpleGARequirements(neuralNetwork.getNumberOfParameters());
        } else if (metaheuristic instanceof SimpleOSGA) {
            AlgorithmsGUI.metaheuristic = createSimpleOSGA(dataset, neuralNetwork);
            AlgorithmsGUI.metaheuristicRequirement = createOSGARequirements(neuralNetwork.getNumberOfParameters());
        } else if (metaheuristic instanceof BasicPSO) {
            AlgorithmsGUI.metaheuristic = createSimplePSO(dataset, neuralNetwork);
            AlgorithmsGUI.metaheuristicRequirement = createPSORequirements(neuralNetwork.getNumberOfParameters());
        } else if (metaheuristic instanceof SimpleSA) {
            AlgorithmsGUI.metaheuristic = createSimpleSA(dataset, neuralNetwork);
            AlgorithmsGUI.metaheuristicRequirement = createSARequirements(neuralNetwork.getNumberOfParameters());
        }
    }
}