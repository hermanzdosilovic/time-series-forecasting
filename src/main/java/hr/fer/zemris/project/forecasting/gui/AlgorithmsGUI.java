package hr.fer.zemris.project.forecasting.gui;

import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.IMetaheuristic;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.ga.IGeneticAlgorithm;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.ga.SimpleGA;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.ga.SimpleOSGA;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.pso.BasicPSO;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.pso.IParticleSwarmOptimization;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.pso.Particle;
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
import com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork;
import com.dosilovic.hermanzvonimir.ecfjava.numeric.IFunction;
import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import com.dosilovic.hermanzvonimir.ecfjava.util.RealVector;
import com.dosilovic.hermanzvonimir.ecfjava.util.Solution;
import hr.fer.zemris.project.forecasting.nn.Backpropagation;
import hr.fer.zemris.project.forecasting.nn.functions.MSEFunction;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AlgorithmsGUI {

    public static IMetaheuristic metaheuristic;

    //TODO dodati DE i backPropagation
    public static EventHandler<ActionEvent> chooseAlgorithmAction(ComboBox<String> comboBox, List<DatasetEntry> dataset,
                                                                  INeuralNetwork neuralNetwork, Stage primaryStage) {
        return e -> {
            if (comboBox.getValue().equals("Genetic")) genetic(dataset, neuralNetwork, primaryStage);
            else if (comboBox.getValue().equals("OSGA")) OSGA(dataset, neuralNetwork, primaryStage);
            else if (comboBox.getValue().equals("SA")) SA(dataset, neuralNetwork, primaryStage);
            else if ((comboBox.getValue().equals("PSO"))) PSO(dataset, neuralNetwork, primaryStage);
            else if ((comboBox.getValue().equals("Backpropagation")))
                backpropagation(dataset, neuralNetwork, primaryStage);
        };
    }

    private static void genetic(List<DatasetEntry> dataset, INeuralNetwork neuralNetwork, Stage primaryStage) {
        Stage geneticStage = new Stage();
        geneticStage.initOwner(primaryStage);
        geneticStage.initModality(Modality.WINDOW_MODAL);
        geneticStage.setTitle("Genetic algorithm");

        Label populationSize = new Label("Population size:");
        TextField population = new TextField();

        Label maxGenerations = new Label("Max generations:");
        TextField generations = new TextField();

        Label tournamentSize = new Label("Tournament size:");
        TextField tournament = new TextField();

        Label alpha = new Label("Alpha:");
        TextField a = new TextField();

        Label mutationProbability = new Label("Probability of mutation:");
        TextField mutation = new TextField();

        Label sigma = new Label("Sigma:");
        TextField s = new TextField();

        Label desiredFit = new Label("Desired fittnes:");
        TextField desiredFitness = new TextField();

        Label precision = new Label("Desired precision:");
        TextField desiredPrecision = new TextField();

        CheckBox useElitism = new CheckBox("Use elitism?");
        CheckBox allowRepeat = new CheckBox("Allow repeat?");
        CheckBox forceMutation = new CheckBox("Force mutation?");

        Label invalidInput = new Label("Invalid input.");
        invalidInput.setTextFill(Color.RED);
        invalidInput.setVisible(false);

        Button ok = new Button("OK");

        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        grid.add(populationSize, 0, 0);
        grid.add(population, 1, 0);
        grid.add(maxGenerations, 2, 0);
        grid.add(generations, 3, 0);

        grid.add(tournamentSize, 0, 1);
        grid.add(tournament, 1, 1);
        grid.add(alpha, 2, 1);
        grid.add(a, 3, 1);

        grid.add(mutationProbability, 0, 2);
        grid.add(mutation, 1, 2);
        grid.add(sigma, 2, 2);
        grid.add(s, 3, 2);

        grid.add(desiredFit, 0, 3);
        grid.add(desiredFitness, 1, 3);
        grid.add(precision, 2, 3);
        grid.add(desiredPrecision, 3, 3);

        HBox invalidBox = new HBox(invalidInput);
        invalidBox.setAlignment(Pos.CENTER);

        HBox okBox = new HBox(ok);
        okBox.setAlignment(Pos.CENTER);

        HBox boxes = new HBox(useElitism, allowRepeat, forceMutation);
        boxes.setSpacing(10);
        boxes.setAlignment(Pos.CENTER);

        grid.add(boxes, 0, 4, 4, 1);
        grid.add(invalidBox, 0, 5, 4, 1);
        grid.add(okBox, 0, 6, 4, 1);

        Scene scene = new Scene(grid);
        geneticStage.setScene(scene);
        geneticStage.show();

        EventHandler<ActionEvent> buttonHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    long populationSize = Long.parseLong(population.getText());
                    int generationSize = Integer.parseInt(generations.getText());
                    int tournamentSize = Integer.parseInt(tournament.getText());
                    double alpha = Double.parseDouble(a.getText());
                    double mutationP = Double.parseDouble(mutation.getText());
                    double sigma = Double.parseDouble(s.getText());
                    boolean forceMutations = forceMutation.isSelected();
                    boolean elitism = useElitism.isSelected();
                    boolean repeat = allowRepeat.isSelected();
                    double desiredFittnes = Double.parseDouble(desiredFitness.getText());
                    double desiredPrec = Double.parseDouble(desiredFitness.getText());

                    IProblem<RealVector> problem = new FunctionMinimizationProblem<>(new MSEFunction<>(neuralNetwork, dataset));
                    ISelection<RealVector> selection = new TournamentSelection<>(
                            tournamentSize,
                            repeat
                    );
                    ICrossover<RealVector> crossover = new BLXAlphaCrossover<>(alpha);
                    IMutation<RealVector> mutation = new RealVectorGaussianMutation<>(
                            mutationP,
                            forceMutations,
                            sigma
                    );
                    SimpleGA<RealVector> simpleGA = new SimpleGA<>(elitism, generationSize, desiredFittnes, desiredPrec, problem, selection, crossover, mutation);
                    metaheuristic = simpleGA;
                    geneticStage.hide();
                } catch (RuntimeException ex) {
                    invalidInput.setVisible(true);
                }
                event.consume();
            }
        };
        ok.setOnAction(buttonHandler);

    }

    private static void OSGA(List<DatasetEntry> dataset, INeuralNetwork neuralNetwork, Stage primaryStage) {
        Stage OSGAStage = new Stage();
        OSGAStage.initOwner(primaryStage);
        OSGAStage.initModality(Modality.WINDOW_MODAL);
        OSGAStage.setTitle("OSGA");

        Label populationSize = new Label("Population size:");
        TextField population = new TextField();

        Label maxGenerations = new Label("Max generations:");
        TextField generations = new TextField();

        Label tournamentSize = new Label("Tournament size:");
        TextField tournament = new TextField();

        Label alpha = new Label("Alpha:");
        TextField a = new TextField();

        Label mutationProbability = new Label("Probability of mutation:");
        TextField mutation = new TextField();

        Label sigma = new Label("Sigma:");
        TextField s = new TextField();

        Label maxSelectionPressure = new Label("Max selection pressure:");
        TextField maxPressure = new TextField();

        Label minSuccessRatio = new Label("Min success ratio:");
        TextField minSuccess = new TextField();
        Label maxSuccessRatio = new Label("Max success ratio:");
        TextField maxSuccess = new TextField();

        Label minComparisonFactor = new Label("Min comparison factor:");
        TextField minComparison = new TextField();
        Label maxComparisonFactor = new Label("Max comparison factor:");
        TextField maxComparison = new TextField();

        Label desiredFit = new Label("Desired fittnes:");
        TextField desiredFitness = new TextField();

        Label precision = new Label("Desired precision:");
        TextField desiredPrecision = new TextField();

        CheckBox useElitism = new CheckBox("Use elitism?");
        CheckBox allowRepeat = new CheckBox("Allow repeat?");
        CheckBox forceMutation = new CheckBox("Force mutation?");

        Label invalidInput = new Label("Invalid input.");
        invalidInput.setTextFill(Color.RED);
        invalidInput.setVisible(false);

        Button ok = new Button("OK");

        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        grid.add(populationSize, 0, 0);
        grid.add(population, 1, 0);
        grid.add(maxGenerations, 2, 0);
        grid.add(generations, 3, 0);

        grid.add(tournamentSize, 0, 1);
        grid.add(tournament, 1, 1);
        grid.add(alpha, 2, 1);
        grid.add(a, 3, 1);

        grid.add(mutationProbability, 0, 2);
        grid.add(mutation, 1, 2);
        grid.add(sigma, 2, 2);
        grid.add(s, 3, 2);

        grid.add(maxSelectionPressure, 0, 3);
        grid.add(maxPressure, 1, 3);
        grid.add(minSuccessRatio, 2, 3);
        grid.add(minSuccess, 3, 3);

        grid.add(maxSuccessRatio, 0, 4);
        grid.add(maxSuccess, 1, 4);
        grid.add(minComparisonFactor, 2, 4);
        grid.add(minComparison, 3, 4);

        grid.add(maxComparisonFactor, 0, 5);
        grid.add(maxComparison, 1, 5);
        grid.add(desiredFit, 2, 5);
        grid.add(desiredFitness, 3, 5);

        grid.add(precision, 0, 6);
        grid.add(desiredPrecision, 1, 6);

        HBox invalidBox = new HBox(invalidInput);
        invalidBox.setAlignment(Pos.CENTER);

        HBox okBox = new HBox(ok);
        okBox.setAlignment(Pos.CENTER);

        HBox boxes = new HBox(useElitism, allowRepeat, forceMutation);
        boxes.setSpacing(10);
        boxes.setAlignment(Pos.CENTER);

        grid.add(boxes, 0, 7, 4, 1);
        grid.add(invalidBox, 0, 8, 4, 1);
        grid.add(okBox, 0, 9, 4, 1);

        Scene scene = new Scene(grid);
        OSGAStage.setScene(scene);
        OSGAStage.show();

        EventHandler<ActionEvent> buttonHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    long populationSize = Long.parseLong(population.getText());
                    int generationSize = Integer.parseInt(generations.getText());
                    int tournamentSize = Integer.parseInt(tournament.getText());
                    double alpha = Double.parseDouble(a.getText());
                    double mutationP = Double.parseDouble(mutation.getText());
                    double sigma = Double.parseDouble(s.getText());
                    double maxP = Double.parseDouble(maxPressure.getText());
                    double minSuccessRatio = Double.parseDouble(minSuccess.getText());
                    double maxSuccessRatio = Double.parseDouble(maxSuccess.getText());
                    double minComparisonRatio = Double.parseDouble(minComparison.getText());
                    double maxComparisonRatio = Double.parseDouble(maxComparison.getText());
                    boolean forceMutations = forceMutation.isSelected();
                    boolean elitism = useElitism.isSelected();
                    boolean doRepeat = allowRepeat.isSelected();
                    double desiredFittnes = Double.parseDouble(desiredFitness.getText());
                    double desiredPrec = Double.parseDouble(desiredFitness.getText());

                    IFunction<RealVector> function = new MSEFunction<>(neuralNetwork, dataset);
                    IProblem<RealVector> problem = new FunctionMinimizationProblem<>(function);
                    ISelection<RealVector> selection = new TournamentSelection<RealVector>(
                            tournamentSize,
                            doRepeat
                    );
                    ICrossover<RealVector> crossover = new BLXAlphaCrossover<>(alpha);
                    IMutation<RealVector> mutation = new RealVectorGaussianMutation<>(
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
                            desiredFittnes,
                            desiredPrec,
                            maxP,
                            successRatioSchedule,
                            comparisonFactorSchedule,
                            problem,
                            selection,
                            crossover,
                            mutation
                    );

                    metaheuristic = geneticAlgorithm;
                    OSGAStage.hide();
                } catch (RuntimeException ex) {
                    invalidInput.setVisible(true);
                }
            }
        };
        ok.setOnAction(buttonHandler);
    }

    private static void SA(List<DatasetEntry> dataset, INeuralNetwork neuralNetwork, Stage primaryStage) {
        Stage SAStage = new Stage();
        SAStage.initOwner(primaryStage);
        SAStage.initModality(Modality.WINDOW_MODAL);
        SAStage.setTitle("SA");

        Label outerIterations = new Label("Outer iterations:");
        TextField outerIter = new TextField();

        Label outerInitialTemp = new Label("Outer initial temperature:");
        TextField outerTemp = new TextField();
        Label outerFinalTemp = new Label("Outer final temperature:");
        TextField outerFinalTemperature = new TextField();


        Label innerIterations = new Label("Inner iterations:");
        TextField innerIter = new TextField();

        Label innerInitialTemp = new Label("Inner initial temperature:");
        TextField innerTemp = new TextField();
        Label innerFinalTemp = new Label("Inner final temperature:");
        TextField innerFinalTemperature = new TextField();

        Label mutationProbability = new Label("Mutation probability:");
        TextField mutation = new TextField();

        Label sigma = new Label("Sigma:");
        TextField s = new TextField();

        Label desiredFit = new Label("Desired fittnes:");
        TextField desiredFitness = new TextField();

        Label precision = new Label("Desired precision:");
        TextField desiredPrecision = new TextField();

        CheckBox forceMutation = new CheckBox("Force mutation?");

        Label invalidInput = new Label("Invalid input.");
        invalidInput.setTextFill(Color.RED);
        invalidInput.setVisible(false);

        Button ok = new Button("OK");

        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        grid.add(outerIterations, 0, 0);
        grid.add(outerIter, 1, 0);
        grid.add(outerInitialTemp, 2, 0);
        grid.add(outerTemp, 3, 0);

        grid.add(outerFinalTemp, 0, 1);
        grid.add(outerFinalTemperature, 1, 1);
        grid.add(innerIterations, 2, 1);
        grid.add(innerIter, 3, 1);

        grid.add(innerInitialTemp, 0, 2);
        grid.add(innerTemp, 1, 2);
        grid.add(innerFinalTemp, 2, 2);
        grid.add(innerFinalTemperature, 3, 2);

        grid.add(mutationProbability, 0, 3);
        grid.add(mutation, 1, 3);
        grid.add(sigma, 2, 3);
        grid.add(s, 3, 3);

        grid.add(desiredFit, 0, 4);
        grid.add(desiredFitness, 1, 4);
        grid.add(precision, 2, 4);
        grid.add(desiredPrecision, 3, 4);

        HBox invalidBox = new HBox(invalidInput);
        invalidBox.setAlignment(Pos.CENTER);

        HBox okBox = new HBox(ok);
        okBox.setAlignment(Pos.CENTER);

        HBox forceMutBox = new HBox(forceMutation);
        forceMutBox.setAlignment(Pos.CENTER);

        grid.add(forceMutBox, 0, 5, 4, 1);
        grid.add(invalidBox, 0, 6, 4, 1);
        grid.add(okBox, 0, 7, 4, 1);

        Scene scene = new Scene(grid);
        SAStage.setScene(scene);
        SAStage.show();

        EventHandler<ActionEvent> buttonHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    int outerIteration = Integer.parseInt(outerIter.getText());
                    int innerIteration = Integer.parseInt(innerIter.getText());
                    double outerTemperature = Double.parseDouble(outerTemp.getText());
                    double outerFinalTemperature = 0;
                    double innerTemperature = Double.parseDouble(innerTemp.getText());
                    double innerFinalTemperature = 0;
                    double mutationP = Double.parseDouble(mutation.getText());
                    double sigma = Double.parseDouble(s.getText());
                    boolean forceMutations = forceMutation.isSelected();
                    double desiredFittnes = Double.parseDouble(desiredFitness.getText());
                    double desiredPrec = Double.parseDouble(desiredFitness.getText());

                    IFunction<RealVector> function = new MSEFunction<>(neuralNetwork, dataset);
                    IProblem<RealVector> problem = new FunctionMinimizationProblem<>(function);
                    ICoolingSchedule outerCoolingSchedule = new GeometricCoolingSchedule(
                            outerIteration,
                            outerTemperature,
                            outerFinalTemperature
                    );
                    ICoolingSchedule innerCoolingSchedule = new GeometricCoolingSchedule(
                            innerIteration,
                            innerTemperature,
                            innerFinalTemperature
                    );
                    IMutation<RealVector> mutation = new RealVectorGaussianMutation<>(mutationP, forceMutations, sigma);
                    ISimulatedAnnealing<RealVector> simulatedAnnealing = new SimpleSA<>(
                            desiredPrec,
                            desiredFittnes,
                            problem,
                            mutation,
                            outerCoolingSchedule,
                            innerCoolingSchedule
                    );
                    metaheuristic = simulatedAnnealing;
                    SAStage.hide();
                } catch (RuntimeException ex) {
                    invalidInput.setVisible(true);
                }
            }
        };
        ok.setOnAction(buttonHandler);
    }

    private static void PSO(List<DatasetEntry> dataset, INeuralNetwork neuralNetwork, Stage primaryStage) {
        Stage PSOStage = new Stage();
        PSOStage.initOwner(primaryStage);
        PSOStage.initModality(Modality.WINDOW_MODAL);
        PSOStage.setTitle("PSO");

        Label numberOfParticles = new Label("Number of particles:");
        TextField particles = new TextField();

        Label maxIterations = new Label("Max iterations:");
        TextField iteration = new TextField();

        Label minSpeed = new Label("Min velocity:");
        TextField minV = new TextField();

        Label maxSpeed = new Label("Max velocity:");
        TextField maxV = new TextField();

        Label maxValue = new Label("Max value:");
        TextField maxVal = new TextField();

        Label minValue = new Label("Min value:");
        TextField minVal = new TextField();

        Label individualLabel = new Label("Individual factor:");
        TextField individualFactor = new TextField();

        Label socialLabel = new Label("Social factor:");
        TextField socialFactor = new TextField();

        Label desiredFit = new Label("Desired fittnes:");
        TextField desiredFitness = new TextField();

        Label precision = new Label("Desired precision:");
        TextField desiredPrecision = new TextField();

        CheckBox isInformed = new CheckBox("Fully formed?");

        Label invalidInput = new Label("Invalid input.");
        invalidInput.setTextFill(Color.RED);
        invalidInput.setVisible(false);

        Button ok = new Button("OK");

        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        grid.add(numberOfParticles, 0, 0);
        grid.add(particles, 1, 0);
        grid.add(maxIterations, 2, 0);
        grid.add(iteration, 3, 0);

        grid.add(minSpeed, 0, 1);
        grid.add(minV, 1, 1);
        grid.add(maxSpeed, 2, 1);
        grid.add(maxV, 3, 1);

        grid.add(minValue, 0, 2);
        grid.add(minVal, 1, 2);
        grid.add(maxValue, 2, 2);
        grid.add(maxVal, 3, 2);

        grid.add(individualLabel, 0, 3);
        grid.add(individualFactor, 1, 3);
        grid.add(socialLabel, 2, 3);
        grid.add(socialFactor, 3, 3);

        grid.add(desiredFit, 0, 4);
        grid.add(desiredFitness, 1, 4);
        grid.add(precision, 2, 4);
        grid.add(desiredPrecision, 3, 4);

        grid.add(isInformed, 2, 5);

        HBox invalidBox = new HBox(invalidInput);
        invalidBox.setAlignment(Pos.CENTER);

        HBox okBox = new HBox(ok);
        okBox.setAlignment(Pos.CENTER);

        grid.add(invalidBox, 0, 6, 4, 1);
        grid.add(okBox, 0, 7, 4, 1);

        Scene scene = new Scene(grid);
        PSOStage.setScene(scene);
        PSOStage.show();

        EventHandler<ActionEvent> buttonHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    int maxIter = Integer.parseInt(iteration.getText());
                    long particlesNum = Long.parseLong(particles.getText());
                    double minSpeed = Double.parseDouble(minV.getText());
                    double maxSpeed = Double.parseDouble(maxV.getText());
                    double minValue = Double.parseDouble(minVal.getText());
                    double maxValue = Double.parseDouble(maxVal.getText());
                    double individualFact = Double.parseDouble(individualFactor.getText());
                    double socialFact = Double.parseDouble(socialFactor.getText());
                    double desiredFittnes = Double.parseDouble(desiredFitness.getText());
                    double desiredPrec = Double.parseDouble(desiredFitness.getText());
                    boolean isFullyInformed = isInformed.isSelected();

                    IProblem<RealVector> problem = new FunctionMinimizationProblem<>(new MSEFunction<>(neuralNetwork, dataset));
                    Collection<Particle<RealVector>> initialParticles = new ArrayList<>();
                    for (int i = 0; i < particlesNum; i++) {
                        initialParticles.add(new Particle<>(
                                new Solution<>(new RealVector(neuralNetwork.getNumberOfWeights(), minValue, maxValue)),
                                new RealVector(neuralNetwork.getNumberOfWeights(), minSpeed, maxSpeed)
                        ));
                    }
                    IParticleSwarmOptimization<RealVector> particleSwarmOptimization = new BasicPSO<RealVector>(
                            maxIter,
                            desiredFittnes,
                            desiredPrec,
                            isFullyInformed,
                            individualFact,
                            socialFact,
                            new RealVector(neuralNetwork.getNumberOfWeights(), minValue),
                            new RealVector(neuralNetwork.getNumberOfWeights(), maxValue),
                            new RealVector(neuralNetwork.getNumberOfWeights(), minSpeed),
                            new RealVector(neuralNetwork.getNumberOfWeights(), maxSpeed),
                            problem,
                            new FullyConnectedTopology<>()
                    );

                    metaheuristic = particleSwarmOptimization;
                    PSOStage.hide();
                } catch (RuntimeException ex) {
                    invalidInput.setText("Invalid input");
                }
            }
        };
        ok.setOnAction(buttonHandler);
    }

    private static void backpropagation(List<DatasetEntry> dataset, INeuralNetwork neuralNetwork,
                                        Stage primaryStage) {
        Stage BPStage = new Stage();
        BPStage.initOwner(primaryStage);
        BPStage.initModality(Modality.WINDOW_MODAL);
        BPStage.setTitle("Backpropagaation");

        Label maxIterations = new Label("Max iterations:");
        TextField iteration = new TextField();

        Label batch = new Label("Batch size:");
        TextField batchSize = new TextField();

        Label learning = new Label("Learning rate:");
        TextField learningRate = new TextField();

        Label desiredErr = new Label("Desired error:");
        TextField desiredError = new TextField();

        Label precision = new Label("Desired precision:");
        TextField desiredPrecision = new TextField();

        Label invalidInput = new Label("Invalid input.");
        invalidInput.setTextFill(Color.RED);
        invalidInput.setVisible(false);

        Button ok = new Button("OK");

        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        grid.add(maxIterations, 0, 0);
        grid.add(iteration, 1, 0);

        grid.add(batch, 2, 0);
        grid.add(batchSize, 3, 0);

        grid.add(learning, 0, 1);
        grid.add(learningRate, 1, 1);

        grid.add(desiredErr, 2, 1);
        grid.add(desiredError, 3, 1);

        grid.add(precision, 0, 2);
        grid.add(desiredPrecision, 1, 2);

        HBox invalidBox = new HBox(invalidInput);
        invalidBox.setAlignment(Pos.CENTER);

        HBox okBox = new HBox(ok);
        okBox.setAlignment(Pos.CENTER);

        grid.add(invalidBox, 0, 3, 4, 1);
        grid.add(okBox, 0, 4, 5, 1);

        Scene scene = new Scene(grid);
        BPStage.setScene(scene);
        BPStage.show();

        EventHandler<ActionEvent> buttonHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    long maxIter = Long.parseLong(iteration.getText());
                    double learningRate = Double.parseDouble(learning.getText());
                    double desiredErr = Double.parseDouble(desiredError.getText());
                    double desiredPrec = Double.parseDouble(desiredPrecision.getText());
                    int batch = Integer.parseInt(batchSize.getText());

                    Backpropagation bp = new Backpropagation(null, null, learningRate, maxIter, desiredErr, desiredPrec, neuralNetwork, batch);
                    metaheuristic = bp;
                    BPStage.hide();
                } catch (RuntimeException ex) {
                    invalidInput.setVisible(true);
                }
            }
        };
        ok.setOnAction(buttonHandler);
    }
}
