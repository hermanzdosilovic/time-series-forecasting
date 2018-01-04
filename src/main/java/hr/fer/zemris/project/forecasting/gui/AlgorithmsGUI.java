package hr.fer.zemris.project.forecasting.gui;

import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.IMetaheuristic;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.ga.SimpleGA;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.pso.BasicPSO;
import com.dosilovic.hermanzvonimir.ecfjava.models.crossovers.BLXAlphaCrossover;
import com.dosilovic.hermanzvonimir.ecfjava.models.crossovers.ICrossover;
import com.dosilovic.hermanzvonimir.ecfjava.models.mutations.IMutation;
import com.dosilovic.hermanzvonimir.ecfjava.models.mutations.RealVectorGaussianMutation;
import com.dosilovic.hermanzvonimir.ecfjava.models.problems.FunctionMinimizationProblem;
import com.dosilovic.hermanzvonimir.ecfjava.models.problems.IProblem;
import com.dosilovic.hermanzvonimir.ecfjava.models.selections.ISelection;
import com.dosilovic.hermanzvonimir.ecfjava.models.selections.TournamentSelection;
import com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork;
import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import com.dosilovic.hermanzvonimir.ecfjava.util.RealVector;
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
                    invalidInput.setText("Invalid input");
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

        Label minSuccessRatio = new Label("Minimal success ratio:");
        TextField minSuccess = new TextField();

        Label minComparisonFactor = new Label("Minimal comparison factor:");
        TextField minComparison = new TextField();

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

        grid.add(minComparisonFactor, 0, 4);
        grid.add(minComparison, 1, 4);

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
        OSGAStage.setScene(scene);
        OSGAStage.show();

        EventHandler<ActionEvent> buttonHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                long populationSize = Long.parseLong(population.getText());
                long generationSize = Long.parseLong(generations.getText());
                long tournamentSize = Long.parseLong(tournament.getText());
                double alpha = Double.parseDouble(a.getText());
                double mutationP = Double.parseDouble(mutation.getText());
                double sigma = Double.parseDouble(s.getText());
                double maxP = Double.parseDouble(maxPressure.getText());
                double minSuccessRatio = Double.parseDouble(minSuccess.getText());
                double minComparisonRatio = Double.parseDouble(minComparison.getText());
                boolean forceMutations = forceMutation.isSelected();
                boolean elitism = useElitism.isSelected();
                boolean repeat = allowRepeat.isSelected();

//                SimpleOSGA<double[]> osga = new SimpleOSGA<>();
                event.consume();
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

        Label innerIterations = new Label("Inner iterations:");
        TextField innerIter = new TextField();

        Label innerInitialTemp = new Label("Inner initial temperature:");
        TextField innerTemp = new TextField();

        Label mutationProbability = new Label("Mutation probability:");
        TextField mutation = new TextField();

        Label sigma = new Label("Sigma:");
        TextField s = new TextField();

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

        grid.add(innerIterations, 0, 1);
        grid.add(innerIter, 1, 1);
        grid.add(innerInitialTemp, 2, 1);
        grid.add(innerTemp, 3, 1);

        grid.add(mutationProbability, 0, 2);
        grid.add(mutation, 1, 2);
        grid.add(sigma, 2, 2);
        grid.add(s, 3, 2);

        HBox invalidBox = new HBox(invalidInput);
        invalidBox.setAlignment(Pos.CENTER);

        HBox okBox = new HBox(ok);
        okBox.setAlignment(Pos.CENTER);

        HBox forceMutBox = new HBox(forceMutation);
        forceMutBox.setAlignment(Pos.CENTER);

        grid.add(forceMutBox, 0, 3, 4, 1);
        grid.add(invalidBox, 0, 4, 4, 1);
        grid.add(okBox, 0, 5, 4, 1);

        Scene scene = new Scene(grid);
        SAStage.setScene(scene);
        SAStage.show();

        EventHandler<ActionEvent> buttonHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                long outerIteration = Long.parseLong(outerIter.getText());
                long innerIteration = Long.parseLong(innerIter.getText());
                double outerTemperature = Double.parseDouble(outerTemp.getText());
                double innerTemperature = Double.parseDouble(innerTemp.getText());
                double mutationP = Double.parseDouble(mutation.getText());
                double sigma = Double.parseDouble(s.getText());
                boolean forceMutations = forceMutation.isSelected();

//                SimpleSA<double[]> pso = new SimpleSA<>();
                event.consume();
            }
        };
        ok.setOnAction(buttonHandler);
    }

    private static void PSO(List<DatasetEntry> dataset, INeuralNetwork neuralNetwork, Stage primaryStage) {
        Stage SAStage = new Stage();
        SAStage.initOwner(primaryStage);
        SAStage.initModality(Modality.WINDOW_MODAL);
        SAStage.setTitle("SA");

        Label numberOfParticles = new Label("Number of particles:");
        TextField particles = new TextField();

        Label maxIterations = new Label("Max iterations:");
        TextField iteration = new TextField();

        Label minSpeed = new Label("Min velocity:");
        TextField minV = new TextField();

        Label maxSpeed = new Label("Max velocity:");
        TextField maxV = new TextField();

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

        HBox invalidBox = new HBox(invalidInput);
        invalidBox.setAlignment(Pos.CENTER);

        HBox okBox = new HBox(ok);
        okBox.setAlignment(Pos.CENTER);

        grid.add(invalidBox, 0, 2, 4, 1);
        grid.add(okBox, 0, 3, 4, 1);

        Scene scene = new Scene(grid);
        SAStage.setScene(scene);
        SAStage.show();

        EventHandler<ActionEvent> buttonHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                long maxIter = Long.parseLong(iteration.getText());
                long particlesNum = Long.parseLong(particles.getText());
                double minSpeed = Double.parseDouble(minV.getText());
                double maxSpeed = Double.parseDouble(maxV.getText());

                BasicPSO<RealVector> pso = null;
                event.consume();
            }
        };
        ok.setOnAction(buttonHandler);
    }

    private static void backpropagation(List<DatasetEntry> dataset, INeuralNetwork neuralNetwork,
                                        Stage primaryStage) {
        Stage SAStage = new Stage();
        SAStage.initOwner(primaryStage);
        SAStage.initModality(Modality.WINDOW_MODAL);
        SAStage.setTitle("Backpropagaation");

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
        grid.add(okBox, 0, 3, 5, 1);

        Scene scene = new Scene(grid);
        SAStage.setScene(scene);
        SAStage.show();

        EventHandler<ActionEvent> buttonHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                long maxIter = Long.parseLong(iteration.getText());
                double learningRate = Double.parseDouble(learning.getText());
                double desiredErr = Double.parseDouble(desiredError.getText());
                double desiredPrec = Double.parseDouble(desiredPrecision.getText());
                int batch = Integer.parseInt(batchSize.getText());

                Backpropagation bp = new Backpropagation(null, null, learningRate, maxIter, desiredErr, desiredPrec, neuralNetwork, batch);
                metaheuristic = bp;
                event.consume();
            }
        };
        ok.setOnAction(buttonHandler);
    }
}
