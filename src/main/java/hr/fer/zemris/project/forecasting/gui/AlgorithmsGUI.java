package hr.fer.zemris.project.forecasting.gui;

import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.IMetaheuristic;
import hr.fer.zemris.project.forecasting.nn.INeuralNetwork;
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

public abstract class AlgorithmsGUI {
    //TODO dodati DE i backPropagation
    public static EventHandler<ActionEvent> chooseAlgorithmAction(ComboBox<String> comboBox, IMetaheuristic metaheuristic,
                                                                  INeuralNetwork neuralNetwork, Stage primaryStage) {
        return e -> {
            if (comboBox.getValue().equals("Genetic")) genetic(metaheuristic, neuralNetwork, primaryStage);
            else if (comboBox.getValue().equals("OSGA")) OSGA(metaheuristic, neuralNetwork, primaryStage);
            else if(comboBox.getValue().equals("SA")) SA(metaheuristic, neuralNetwork, primaryStage);
            else if((comboBox.getValue().equals("PSO"))) PSO(metaheuristic, neuralNetwork, primaryStage);
        };
    }

    private static void genetic(IMetaheuristic metaheuristic, INeuralNetwork neuralNetwork, Stage primaryStage) {
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

        HBox invalidBox = new HBox(invalidInput);
        invalidBox.setAlignment(Pos.CENTER);

        HBox okBox = new HBox(ok);
        okBox.setAlignment(Pos.CENTER);

        HBox boxes = new HBox(useElitism, allowRepeat, forceMutation);
        boxes.setSpacing(10);
        boxes.setAlignment(Pos.CENTER);

        grid.add(boxes, 0, 3, 4, 1);
        grid.add(invalidBox, 0, 4, 4, 1);
        grid.add(okBox, 0, 5, 4, 1);

        Scene scene = new Scene(grid);
        geneticStage.setScene(scene);
        geneticStage.show();
    }

    private static void OSGA(IMetaheuristic metaheuristic, INeuralNetwork neuralNetwork, Stage primaryStage) {
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
    }

    private static void SA(IMetaheuristic metaheuristic, INeuralNetwork neuralNetwork, Stage primaryStage) {
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
    }

    private static void PSO(IMetaheuristic metaheuristic, INeuralNetwork neuralNetwork, Stage primaryStage) {
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

        Label maxSpeed  = new Label("Max velocity:");
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
    }
}
