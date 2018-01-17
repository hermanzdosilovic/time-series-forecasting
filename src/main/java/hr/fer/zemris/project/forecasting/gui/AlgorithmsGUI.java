package hr.fer.zemris.project.forecasting.gui;

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
import com.dosilovic.hermanzvonimir.ecfjava.models.solutions.SimpleSolution;
import com.dosilovic.hermanzvonimir.ecfjava.models.solutions.factories.ISolutionFactory;
import com.dosilovic.hermanzvonimir.ecfjava.models.solutions.factories.ParticleFactory;
import com.dosilovic.hermanzvonimir.ecfjava.models.solutions.factories.RealVectorFactory;
import com.dosilovic.hermanzvonimir.ecfjava.models.solutions.factories.SimpleSolutionFactory;
import com.dosilovic.hermanzvonimir.ecfjava.models.solutions.particle.Particle;
import com.dosilovic.hermanzvonimir.ecfjava.models.solutions.vector.RealVector;
import com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork;
import com.dosilovic.hermanzvonimir.ecfjava.neural.errors.MSEFunction;
import com.dosilovic.hermanzvonimir.ecfjava.numeric.IFunction;
import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import hr.fer.zemris.project.forecasting.gui.forms.*;
import hr.fer.zemris.project.forecasting.nn.Backpropagation;
import javafx.beans.property.ObjectProperty;
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
    public static Object metaheuristicRequirement;

    //TODO dodati DE i backPropagation
    public static EventHandler<ActionEvent> chooseAlgorithmAction(ComboBox<String> comboBox, List<DatasetEntry> dataset,
                                                                  double trainPercentage, INeuralNetwork neuralNetwork,
                                                                  Stage primaryStage,
                                                                  ObjectProperty<IMetaheuristic> metaheuristicProperty) {
        return e -> {
            if (comboBox.getValue().equals("Genetic"))
                genetic(dataset, neuralNetwork, primaryStage, metaheuristicProperty);
            else if (comboBox.getValue().equals("OSGA"))
                OSGA(dataset, neuralNetwork, primaryStage, metaheuristicProperty);
            else if (comboBox.getValue().equals("SA")) SA(dataset, neuralNetwork, primaryStage, metaheuristicProperty);
            else if ((comboBox.getValue().equals("PSO")))
                PSO(dataset, neuralNetwork, primaryStage, metaheuristicProperty);
            else if ((comboBox.getValue().equals("Backpropagation")))
                backpropagation(dataset, trainPercentage, neuralNetwork, primaryStage, metaheuristicProperty);
        };
    }

    private static void genetic(List<DatasetEntry> dataset, INeuralNetwork neuralNetwork, Stage primaryStage,
                                ObjectProperty<IMetaheuristic> metaheuristicProperty) {
        Stage geneticStage = new Stage();
        geneticStage.initOwner(primaryStage);
        geneticStage.initModality(Modality.WINDOW_MODAL);
        geneticStage.setTitle("Genetic algorithm");

        GeneticForm geneticForm = GeneticForm.getInstance();

        Label populationSize = new Label("Population size:");
        TextField population = new TextField();
        population.setText(geneticForm.getPopulationSize());

        Label maxGenerations = new Label("Max generations:");
        TextField generations = new TextField();
        generations.setText(geneticForm.getMaxGenerations());

        Label tournamentSize = new Label("Tournament size:");
        TextField tournament = new TextField();
        tournament.setText(geneticForm.getTournamentSize());

        Label alpha = new Label("Alpha:");
        TextField a = new TextField();
        a.setText(geneticForm.getAlpha());

        Label mutationProbability = new Label("Probability of mutation:");
        TextField mutation = new TextField();
        mutation.setText(geneticForm.getMutationProbability());

        Label sigma = new Label("Sigma:");
        TextField s = new TextField();
        s.setText(geneticForm.getSigma());

        Label desiredFit = new Label("Desired fitness:");
        TextField desiredFitness = new TextField();
        desiredFitness.setText(geneticForm.getDesiredFitness());

        Label precision = new Label("Desired precision:");
        TextField desiredPrecision = new TextField();
        desiredPrecision.setText(geneticForm.getDesiredPrecision());

        Label minComp = new Label("Min component value:");
        TextField minCompValue = new TextField();
        minCompValue.setText(geneticForm.getMinComponentValue());

        Label maxComp = new Label("Max component value:");
        TextField maxCompValue = new TextField();
        maxCompValue.setText(geneticForm.getMaxComponentValue());

        CheckBox useElitism = new CheckBox("Use elitism?");
        useElitism.setSelected(geneticForm.isUseElitism());
        CheckBox allowRepeat = new CheckBox("Unique tournament?");
        allowRepeat.setSelected(geneticForm.isAllowRepeat());
        CheckBox forceMutation = new CheckBox("Force mutation?");
        forceMutation.setSelected(geneticForm.isForeceMutation());

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

        grid.add(minComp, 0, 4);
        grid.add(minCompValue, 1, 4);
        grid.add(maxComp, 2, 4);
        grid.add(maxCompValue, 3, 4);

        HBox invalidBox = new HBox(invalidInput);
        invalidBox.setAlignment(Pos.CENTER);

        HBox okBox = new HBox(ok);
        okBox.setAlignment(Pos.CENTER);

        HBox boxes = new HBox(useElitism, allowRepeat, forceMutation);
        boxes.setSpacing(10);
        boxes.setAlignment(Pos.CENTER);

        grid.add(boxes, 0, 5, 4, 1);
        grid.add(invalidBox, 0, 6, 4, 1);
        grid.add(okBox, 0, 7, 4, 1);

        Scene scene = new Scene(grid);
        geneticStage.setScene(scene);
        geneticStage.show();

        EventHandler<ActionEvent> buttonHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    int populationSize = Integer.parseInt(population.getText());
                    int generationSize = Integer.parseInt(generations.getText());
                    int tournamentSize = Integer.parseInt(tournament.getText());
                    double alpha = Double.parseDouble(a.getText());
                    double mutationP = Double.parseDouble(mutation.getText());
                    double sigma = Double.parseDouble(s.getText());
                    boolean forceMutations = forceMutation.isSelected();
                    boolean elitism = useElitism.isSelected();
                    boolean repeat = allowRepeat.isSelected();
                    double desiredFittnes = Double.parseDouble(desiredFitness.getText());
                    double desiredPrec = Double.parseDouble(desiredPrecision.getText());
                    double minComponentValue = Double.parseDouble(minCompValue.getText());
                    double maxComponentValue = Double.parseDouble(maxCompValue.getText());

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
                    metaheuristic = simpleGA;
                    metaheuristicProperty.setValue(metaheuristic);

                    ISolutionFactory<RealVector> solutionFactory = new SimpleSolutionFactory<>(
                      new RealVectorFactory(
                              new RealVector(neuralNetwork.getNumberOfParameters(), minComponentValue, maxComponentValue)
                      )
                    );
                    metaheuristicRequirement = solutionFactory.createMultipleInstances(populationSize);

                    geneticForm.setPopulationSize(population.getText());
                    geneticForm.setMaxGenerations(generations.getText());
                    geneticForm.setTournamentSize(tournament.getText());
                    geneticForm.setAlpha(a.getText());
                    geneticForm.setMutationProbability(mutation.getText());
                    geneticForm.setSigma(s.getText());
                    geneticForm.setForeceMutation(forceMutation.isSelected());
                    geneticForm.setUseElitism(useElitism.isSelected());
                    geneticForm.setAllowRepeat(allowRepeat.isSelected());
                    geneticForm.setDesiredFitness(desiredFitness.getText());
                    geneticForm.setDesiredPrecision(desiredPrecision.getText());
                    geneticForm.setMinComponentValue(minCompValue.getText());
                    geneticForm.setMaxComponentValue(maxCompValue.getText());

                    geneticStage.hide();
                } catch (RuntimeException ex) {
                    invalidInput.setVisible(true);
                }
                event.consume();
            }
        };
        ok.setOnAction(buttonHandler);

    }

    private static void OSGA(List<DatasetEntry> dataset, INeuralNetwork neuralNetwork, Stage primaryStage,
                             ObjectProperty<IMetaheuristic> metaheuristicProperty) {
        Stage OSGAStage = new Stage();
        OSGAStage.initOwner(primaryStage);
        OSGAStage.initModality(Modality.WINDOW_MODAL);
        OSGAStage.setTitle("OSGA");

        OSGAForm osgaForm = OSGAForm.getInstance();

        Label populationSize = new Label("Population size:");
        TextField population = new TextField();
        population.setText(osgaForm.getPopulationSize());

        Label maxGenerations = new Label("Max generations:");
        TextField generations = new TextField();
        generations.setText(osgaForm.getMaxGenerations());

        Label tournamentSize = new Label("Tournament size:");
        TextField tournament = new TextField();
        tournament.setText(osgaForm.getTournamentSize());

        Label alpha = new Label("Alpha:");
        TextField a = new TextField();
        a.setText(osgaForm.getAlpha());

        Label mutationProbability = new Label("Probability of mutation:");
        TextField mutation = new TextField();
        mutation.setText(osgaForm.getMutationProbability());

        Label sigma = new Label("Sigma:");
        TextField s = new TextField();
        s.setText(osgaForm.getSigma());

        Label maxSelectionPressure = new Label("Max selection pressure:");
        TextField maxPressure = new TextField();
        maxPressure.setText(osgaForm.getMaxSelectionPressure());

        Label minSuccessRatio = new Label("Min success ratio:");
        TextField minSuccess = new TextField();
        minSuccess.setText(osgaForm.getMinSuccessRatio());

        Label maxSuccessRatio = new Label("Max success ratio:");
        TextField maxSuccess = new TextField();
        maxSuccess.setText(osgaForm.getMaxSuccessRatio());

        Label minComparisonFactor = new Label("Min comparison factor:");
        TextField minComparison = new TextField();
        minComparison.setText(osgaForm.getMinComparisionFactor());

        Label maxComparisonFactor = new Label("Max comparison factor:");
        TextField maxComparison = new TextField();
        maxComparison.setText(osgaForm.getMaxComparisionFactor());

        Label desiredFit = new Label("Desired fitness:");
        TextField desiredFitness = new TextField();
        desiredFitness.setText(osgaForm.getDesiredFitness());

        Label precision = new Label("Desired precision:");
        TextField desiredPrecision = new TextField();
        desiredPrecision.setText(osgaForm.getDesiredPrecision());

        Label minComp = new Label("Min component value:");
        TextField minCompValue = new TextField();
        minCompValue.setText(osgaForm.getMinComponentValue());

        Label maxComp = new Label("Max component value:");
        TextField maxCompValue = new TextField();
        maxCompValue.setText(osgaForm.getMaxComponentValue());

        CheckBox useElitism = new CheckBox("Use elitism?");
        useElitism.setSelected(osgaForm.isUseElitism());

        CheckBox allowRepeat = new CheckBox("Unique tournament?");
        allowRepeat.setSelected(osgaForm.isAllowRepeat());

        CheckBox forceMutation = new CheckBox("Force mutation?");
        forceMutation.setSelected(osgaForm.isForceMutation());

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
        grid.add(minComp, 2, 6);
        grid.add(minCompValue, 3, 6);

        grid.add(maxComp, 1, 7);
        grid.add(maxCompValue, 2, 7);

        HBox invalidBox = new HBox(invalidInput);
        invalidBox.setAlignment(Pos.CENTER);

        HBox okBox = new HBox(ok);
        okBox.setAlignment(Pos.CENTER);

        HBox boxes = new HBox(useElitism, allowRepeat, forceMutation);
        boxes.setSpacing(10);
        boxes.setAlignment(Pos.CENTER);

        grid.add(boxes, 0, 8, 4, 1);
        grid.add(invalidBox, 0, 9, 4, 1);
        grid.add(okBox, 0, 10, 4, 1);

        Scene scene = new Scene(grid);
        OSGAStage.setScene(scene);
        OSGAStage.show();

        EventHandler<ActionEvent> buttonHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    int populationSize = Integer.parseInt(population.getText());
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
                    double fitness = Double.parseDouble(desiredFitness.getText());
                    double desiredPrec = Double.parseDouble(desiredPrecision.getText());
                    double minComponentValue = Double.parseDouble(minCompValue.getText());
                    double maxComponentValue = Double.parseDouble(maxCompValue.getText());

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

                    metaheuristic = geneticAlgorithm;
                    metaheuristicProperty.setValue(metaheuristic);
                    ISolutionFactory<RealVector> solutionFactory = new SimpleSolutionFactory<>(
                            new RealVectorFactory(
                                    new RealVector(neuralNetwork.getNumberOfParameters(), minComponentValue, maxComponentValue)
                            )
                    );
                    metaheuristicRequirement = solutionFactory.createMultipleInstances(populationSize);

                    osgaForm.setPopulationSize(population.getText());
                    osgaForm.setMaxGenerations(generations.getText());
                    osgaForm.setTournamentSize(tournament.getText());
                    osgaForm.setAlpha(a.getText());
                    osgaForm.setMutationProbability(mutation.getText());
                    osgaForm.setSigma(s.getText());
                    osgaForm.setMaxSelectionPressure(maxPressure.getText());
                    osgaForm.setMinSuccessRatio(minSuccess.getText());
                    osgaForm.setMaxSuccessRatio(maxSuccess.getText());
                    osgaForm.setMinComparisionFactor(minComparison.getText());
                    osgaForm.setMaxComparisionFactor(maxComparison.getText());
                    osgaForm.setForceMutation(forceMutation.isSelected());
                    osgaForm.setUseElitism(useElitism.isSelected());
                    osgaForm.setAllowRepeat(allowRepeat.isSelected());
                    osgaForm.setDesiredFitness(desiredFitness.getText());
                    osgaForm.setDesiredPrecision(desiredPrecision.getText());
                    osgaForm.setMinComponentValue(minCompValue.getText());
                    osgaForm.setMaxComponentValue(maxCompValue.getText());

                    OSGAStage.hide();
                } catch (RuntimeException ex) {
                    invalidInput.setVisible(true);
                }
            }
        };
        ok.setOnAction(buttonHandler);
    }

    private static void SA(List<DatasetEntry> dataset, INeuralNetwork neuralNetwork, Stage primaryStage,
                           ObjectProperty<IMetaheuristic> metaheuristicProperty) {
        Stage SAStage = new Stage();
        SAStage.initOwner(primaryStage);
        SAStage.initModality(Modality.WINDOW_MODAL);
        SAStage.setTitle("SA");

        SAForm saForm = SAForm.getInstance();

        Label outerIterations = new Label("Outer iterations:");
        TextField outerIter = new TextField();
        outerIter.setText(saForm.getOuterIterations());

        Label outerInitialTemp = new Label("Outer initial temperature:");
        TextField outerTemp = new TextField();
        outerTemp.setText(saForm.getOuterInitialTemperature());

        Label outerFinalTemp = new Label("Outer final temperature:");
        TextField outerFinalTemperature = new TextField();
        outerFinalTemperature.setText(saForm.getOuterFinalTemperature());

        Label innerIterations = new Label("Inner iterations:");
        TextField innerIter = new TextField();
        innerIter.setText(saForm.getInnerIterations());

        Label mutationProbability = new Label("Mutation probability:");
        TextField mutation = new TextField();
        mutation.setText(saForm.getMutationProbability());

        Label sigma = new Label("Sigma:");
        TextField s = new TextField();
        s.setText(saForm.getSigma());

        Label desiredFit = new Label("Desired fitness:");
        TextField desiredFitness = new TextField();
        desiredFitness.setText(saForm.getDesiredFitness());

        Label precision = new Label("Desired precision:");
        TextField desiredPrecision = new TextField();
        desiredPrecision.setText(saForm.getDesiredPrecision());

        Label minComp = new Label("Min component value:");
        TextField minCompValue = new TextField();
        minCompValue.setText(saForm.getMinComponentValue());

        Label maxComp = new Label("Max component value:");
        TextField maxCompValue = new TextField();
        maxCompValue.setText(saForm.getMaxComponentValue());

        CheckBox forceMutation = new CheckBox("Force mutation?");
        forceMutation.setSelected(saForm.isForceMutation());

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

        grid.add(mutationProbability, 0, 2);
        grid.add(mutation, 1, 2);
        grid.add(sigma, 2, 2);
        grid.add(s, 3, 2);

        grid.add(desiredFit, 0, 3);
        grid.add(desiredFitness, 1, 3);
        grid.add(precision, 2, 3);
        grid.add(desiredPrecision, 3, 3);

        grid.add(minComp, 0, 4);
        grid.add(minCompValue, 1, 4);
        grid.add(maxComp, 2, 4);
        grid.add(maxCompValue, 3, 4);

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
                    double outerFinalTemp = Double.parseDouble(outerFinalTemperature.getText());
                    double mutationP = Double.parseDouble(mutation.getText());
                    double sigma = Double.parseDouble(s.getText());
                    boolean forceMutations = forceMutation.isSelected();
                    double fitness = Double.parseDouble(desiredFitness.getText());
                    double desiredPrec = Double.parseDouble(desiredPrecision.getText());
                    double minComponentValue = Double.parseDouble(minCompValue.getText());
                    double maxComponentValue = Double.parseDouble(maxCompValue.getText());

                    IFunction<RealVector> function = new MSEFunction<>(neuralNetwork, dataset.toArray(new DatasetEntry[dataset.size()]));
                    IProblem<RealVector> problem = new FunctionMinimizationProblem<>(function);
                    ICoolingSchedule outerCoolingSchedule = new GeometricCoolingSchedule(
                            outerIteration,
                            outerTemperature,
                            outerFinalTemp
                    );
                    ICoolingSchedule innerCoolingSchedule = new GeometricCoolingSchedule(
                            innerIteration,
                            1000,
                            1E-3
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
                    metaheuristic = simulatedAnnealing;
                    metaheuristicProperty.setValue(metaheuristic);
                    metaheuristicRequirement = new RealVector(
                            neuralNetwork.getNumberOfParameters(),
                            minComponentValue,
                            maxComponentValue
                    );

                    saForm.setOuterIterations(outerIter.getText());
                    saForm.setInnerIterations(innerIter.getText());
                    saForm.setOuterInitialTemperature(outerTemp.getText());
                    saForm.setOuterFinalTemperature(outerFinalTemperature.getText());
                    saForm.setMutationProbability(mutation.getText());
                    saForm.setSigma(s.getText());
                    saForm.setForceMutation(forceMutation.isSelected());
                    saForm.setDesiredFitness(desiredFitness.getText());
                    saForm.setDesiredPrecision(desiredPrecision.getText());
                    saForm.setMinComponentValue(minCompValue.getText());
                    saForm.setMaxComponentValue(maxCompValue.getText());

                    SAStage.hide();
                } catch (RuntimeException ex) {
                    invalidInput.setVisible(true);
                }
            }
        };
        ok.setOnAction(buttonHandler);
    }

    private static void PSO(List<DatasetEntry> dataset, INeuralNetwork neuralNetwork, Stage primaryStage,
                            ObjectProperty<IMetaheuristic> metaheuristicProperty) {
        Stage PSOStage = new Stage();
        PSOStage.initOwner(primaryStage);
        PSOStage.initModality(Modality.WINDOW_MODAL);
        PSOStage.setTitle("PSO");

        PSOForm psoForm = PSOForm.getInstance();

        Label numberOfParticles = new Label("Number of particles:");
        TextField particles = new TextField();
        particles.setText(psoForm.getNumberOfParticles());

        Label maxIterations = new Label("Max iterations:");
        TextField iteration = new TextField();
        iteration.setText(psoForm.getMaxIteration());

        Label minSpeed = new Label("Min velocity:");
        TextField minV = new TextField();
        minV.setText(psoForm.getMinSpeed());

        Label maxSpeed = new Label("Max velocity:");
        TextField maxV = new TextField();
        maxV.setText(psoForm.getMaxSpeed());

        Label maxValue = new Label("Max value:");
        TextField maxVal = new TextField();
        maxVal.setText(psoForm.getMaxValue());

        Label minValue = new Label("Min value:");
        TextField minVal = new TextField();
        minVal.setText(psoForm.getMinValue());

        Label individualLabel = new Label("Individual factor:");
        TextField individualFactor = new TextField();
        individualFactor.setText(psoForm.getIndividualFactor());

        Label socialLabel = new Label("Social factor:");
        TextField socialFactor = new TextField();
        socialFactor.setText(psoForm.getSocialFactor());

        Label desiredFit = new Label("Desired fitness:");
        TextField desiredFitness = new TextField();
        desiredFitness.setText(psoForm.getDesiredFitness());

        Label precision = new Label("Desired precision:");
        TextField desiredPrecision = new TextField();
        desiredPrecision.setText(psoForm.getDesiredPrecision());

        CheckBox isInformed = new CheckBox("Fully informed?");
        isInformed.setSelected(psoForm.isFullyFormed());

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
                    double desiredPrec = Double.parseDouble(desiredPrecision.getText());
                    boolean isFullyInformed = isInformed.isSelected();

                    IProblem<RealVector> problem = new FunctionMinimizationProblem<>(new MSEFunction<>(neuralNetwork,
                            dataset.toArray(new DatasetEntry[dataset.size()])));
                    Collection<ISolution<RealVector>> initialParticles = new ArrayList<>();

                    ISolutionFactory<RealVector> particleFactory = new ParticleFactory<>(
                            new SimpleSolutionFactory<>(
                                    new RealVectorFactory(
                                            new RealVector(neuralNetwork.getNumberOfParameters(), minValue, maxValue, true)
                                    )
                            ),
                            new RealVectorFactory(
                                    new RealVector(neuralNetwork.getNumberOfParameters(), minSpeed, maxSpeed)
                            )
                    );

                    initialParticles = particleFactory.createMultipleInstances((int) particlesNum);

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

                    metaheuristic = particleSwarmOptimization;
                    metaheuristicProperty.setValue(metaheuristic);
                    metaheuristicRequirement = initialParticles;

                    psoForm.setMaxIteration(iteration.getText());
                    psoForm.setNumberOfParticles(particles.getText());
                    psoForm.setMinSpeed(minV.getText());
                    psoForm.setMaxSpeed(maxV.getText());
                    psoForm.setMinValue(minVal.getText());
                    psoForm.setMaxValue(maxVal.getText());
                    psoForm.setIndividualFactor(individualFactor.getText());
                    psoForm.setSocialFactor(socialFactor.getText());
                    psoForm.setDesiredFitness(desiredFitness.getText());
                    psoForm.setDesiredPrecision(desiredPrecision.getText());
                    psoForm.setFullyFormed(isInformed.isSelected());

                    PSOStage.hide();
                } catch (RuntimeException ex) {
                    invalidInput.setText("Invalid input");
                }
            }
        };
        ok.setOnAction(buttonHandler);
    }

    private static void backpropagation(List<DatasetEntry> dataset, double trainPercentage, INeuralNetwork neuralNetwork,
                                        Stage primaryStage, ObjectProperty<IMetaheuristic> metaheuristicProperty) {
        BackpropagationForm backpropagationForm = BackpropagationForm.getInstance();

        Stage BPStage = new Stage();
        BPStage.initOwner(primaryStage);
        BPStage.initModality(Modality.WINDOW_MODAL);
        BPStage.setTitle("Backpropagaation");

        Label maxIterations = new Label("Max iterations:");
        TextField iteration = new TextField();
        iteration.setText(backpropagationForm.getMaxIteration());

        Label batch = new Label("Batch size:");
        TextField batchSize = new TextField();
        batchSize.setText(backpropagationForm.getBatchSize());

        Label learning = new Label("Learning rate:");
        TextField learningRate = new TextField();
        learningRate.setText(backpropagationForm.getLearningRate());

        Label desiredErr = new Label("Desired error:");
        TextField desiredError = new TextField();
        desiredError.setText(backpropagationForm.getDesiredError());

        Label precision = new Label("Desired precision:");
        TextField desiredPrecision = new TextField();
        desiredPrecision.setText(backpropagationForm.getDesiredPrecision());

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

        grid.add(precision, 1, 2);
        grid.add(desiredPrecision, 2, 2);

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
                    double learningRt = Double.parseDouble(learningRate.getText());
                    double desiredErr = Double.parseDouble(desiredError.getText());
                    double desiredPrec = Double.parseDouble(desiredPrecision.getText());
                    int batch = Integer.parseInt(batchSize.getText());
                    int index = (int) Math.ceil(dataset.size() * trainPercentage);
                    Backpropagation bp = new Backpropagation(dataset.subList(0, index),
                            dataset.subList(index, dataset.size()), learningRt, maxIter,
                            desiredErr, desiredPrec, neuralNetwork, batch);
                    metaheuristic = bp;
                    metaheuristicProperty.setValue(metaheuristic);

                    backpropagationForm.setMaxIteration(iteration.getText());
                    backpropagationForm.setLearningRate(learningRate.getText());
                    backpropagationForm.setDesiredError(desiredError.getText());
                    backpropagationForm.setDesiredPrecision(desiredPrecision.getText());
                    backpropagationForm.setBatchSize(batchSize.getText());

                    BPStage.hide();
                } catch (RuntimeException ex) {
                    invalidInput.setVisible(true);
                }
            }
        };
        ok.setOnAction(buttonHandler);
    }
}
