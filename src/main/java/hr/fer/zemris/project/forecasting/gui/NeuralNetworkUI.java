package hr.fer.zemris.project.forecasting.gui;

import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.IMetaheuristic;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.ga.SimpleGA;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.ga.SimpleOSGA;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.pso.BasicPSO;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.sa.SimpleSA;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.util.IObserver;
import com.dosilovic.hermanzvonimir.ecfjava.models.solutions.SimpleSolution;
import com.dosilovic.hermanzvonimir.ecfjava.models.solutions.particle.Particle;
import com.dosilovic.hermanzvonimir.ecfjava.models.solutions.vector.RealVector;
import com.dosilovic.hermanzvonimir.ecfjava.neural.ElmanNN;
import com.dosilovic.hermanzvonimir.ecfjava.neural.FeedForwardANN;
import com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork;
import com.dosilovic.hermanzvonimir.ecfjava.neural.activations.IActivation;
import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import hr.fer.zemris.project.forecasting.gui.NeuralNetworkObservers.DoubleArrayGraphObserver;
import hr.fer.zemris.project.forecasting.gui.NeuralNetworkObservers.DoubleArrayStatusbarObserver;
import hr.fer.zemris.project.forecasting.gui.NeuralNetworkObservers.RealVectorGraphObserver;
import hr.fer.zemris.project.forecasting.gui.NeuralNetworkObservers.RealVectorStatusbarObserver;
import hr.fer.zemris.project.forecasting.gui.forms.NeuralNetworkForm;
import hr.fer.zemris.project.forecasting.nn.Backpropagation;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static hr.fer.zemris.project.forecasting.gui.Data.*;
import static hr.fer.zemris.project.forecasting.gui.DatasetValue.getChartData;
import static hr.fer.zemris.project.forecasting.gui.GUIUtil.extractActivation;

public class NeuralNetworkUI {

    private Data data;
    private int[] architecture;
    private ObjectProperty<INeuralNetwork> neuralNetwork = new SimpleObjectProperty<>(null);
    private ObjectProperty<IMetaheuristic> metaheuristicProperty = new SimpleObjectProperty<>(null);
    private IActivation[] activations;
    private List<DatasetEntry> dataset;
    private ComboBox<String> chooseNetwork;
    private ComboBox<String> chooseAlgorithm;
    private Button changeParams;
    private double trainPercentage;
    private LineChart line;
    private LineChart mseChart;
    private volatile XYChart.Series<Integer, Double> series;
    private volatile XYChart.Series<Integer, Double> mseSeries;
    private Button predict;
    private Button stop;
    private Button start;
    private AtomicBoolean trainingPaused = new AtomicBoolean(false);
    private Label statusBar;

    public NeuralNetworkUI(Data data) {
        this.data = data;
    }

    public void createUI(Pane parent) {
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(30, 30, 30, 30));

        initButtons();
        initNeuralNetwork();
        initMetaheuristic();
        VBox rightSide = new VBox();

        initLineChart();
        mseChart = mseLineChart("MSE");

        GridPane rightSideGrid = new GridPane();
        rightSideGrid.setHgap(10);
        rightSideGrid.setVgap(10);
        rightSideGrid.add(start, 1, 0);
        rightSideGrid.add(predict, 1, 1);
        rightSideGrid.add(stop, 1, 2);

        rightSide.getChildren().add(rightSideGrid);

        TableView table = initTable();
        HBox neural = initChoosers();
        HBox params = initParams();

        statusBar = new Label("Iteration: / MSE: /");

        grid.add(neural, 0, 0);
        grid.add(params, 0, 1);
        grid.add(table, 0, 2);
        grid.add(line, 1, 0, 3, 3);
        grid.add(rightSide, 1, 4);
        grid.add(mseChart, 5, 0, 3, 3);
        grid.add(statusBar, 0, 7);
        parent.getChildren().add(grid);
    }

    private void initButtons() {
        predict = new Button("Predict future values");
        predict.setOnAction(predictAction());
        predict.setDisable(true);

        stop = new Button("Stop training");
        stop.setOnAction(a -> {
            metaheuristicProperty.get().stop();
            predict.setDisable(false);
            stop.setDisable(true);
            trainingPaused.set(true);
        });
        stop.setDisable(true);

        start = new Button("Start training");
        start.setOnAction(startButtonAction());
        start.setDisable(true);
    }

    private HBox initChoosers() {
        chooseNetwork = new ComboBox<>(FXCollections.observableArrayList("<none>", "TDNN", "Elman ANN"));
        chooseNetwork.getSelectionModel().select(0);
        chooseNetwork.setOnAction(changeArchitectureAction(chooseNetwork));
        Button changeArchitectureButton = new Button("Change architecture");
        changeArchitectureButton.setOnAction(changeArchitectureAction(chooseNetwork));
        HBox neural = new HBox(chooseNetwork, changeArchitectureButton);
        return neural;
    }

    private HBox initParams() {
        chooseAlgorithm = new ComboBox<>(FXCollections.observableArrayList(
                "<none>"));
        chooseAlgorithm.getSelectionModel().select(0);
        changeParams = new Button("Change parameters");
        chooseAlgorithm.setDisable(true);
        changeParams.setDisable(true);
        HBox params = new HBox(chooseAlgorithm, changeParams);
        return params;
    }

    private void initLineChart() {
        XYChart.Series<Integer, Double> series = new XYChart.Series();
        series.setName("Expected");
        ObservableList<XYChart.Data<Integer, Double>> observableList = DatasetValue.getChartData(data.getDatasetValues());
        series.setData(observableList);
        updateSeriesOnListChangeListener(data.getDatasetValues(), series);
        line = lineChart(series, "Data");
    }

    private void initNeuralNetwork() {
        neuralNetwork.addListener((t, u, v) -> {
            trainingPaused.set(false);
            if (v != null) {
                dataset = DatasetValue.getTrainingData(data.getDatasetValues(), neuralNetwork.get().getInputSize(),
                        neuralNetwork.get().getOutputSize());
                chooseAlgorithm.setOnAction(null);
                if (v instanceof ElmanNN) {
                    chooseAlgorithm.getItems().clear();
                    chooseAlgorithm.getItems().addAll(Arrays.asList("<none>", "Genetic", "OSGA", "SA", "PSO"));
                } else {
                    chooseAlgorithm.getItems().clear();
                    chooseAlgorithm.getItems().addAll(Arrays.asList("<none>", "Genetic", "OSGA", "SA", "PSO", "Backpropagation"));
                }
                chooseAlgorithm.setOnAction(AlgorithmsGUI.chooseAlgorithmAction(chooseAlgorithm, dataset, trainPercentage,
                        neuralNetwork.get(), data.getPrimaryStage(), metaheuristicProperty));
                chooseAlgorithm.getSelectionModel().select(0);
                chooseAlgorithm.setDisable(false);
                changeParams.setOnAction(AlgorithmsGUI.chooseAlgorithmAction(chooseAlgorithm, dataset, trainPercentage,
                        neuralNetwork.get(), data.getPrimaryStage(), metaheuristicProperty));
                changeParams.setDisable(false);
            } else {
                chooseAlgorithm.setDisable(true);
                changeParams.setDisable(true);
            }

            if (metaheuristicProperty.get() == null || v == null) {
                start.setDisable(true);
            } else {
                start.setDisable(false);
            }
        });
    }

    private TableView initTable() {
        TableView table = new TableView();
        table.setPrefWidth(MAX_TABLE_WIDTH);
        table.setMaxWidth(MAX_TABLE_WIDTH);
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(data.getDatasetValues());


        TableColumn<DatasetValue, Integer> indices = new TableColumn<>("Index");
        indices.setSortable(false);
        indices.setEditable(false);
        indices.setCellValueFactory(new PropertyValueFactory<>("index"));
        table.getColumns().add(indices);

        TableColumn<DatasetValue, Double> values = new TableColumn("Value");
        values.setSortable(false);
        values.setEditable(true);
        values.setCellValueFactory(new PropertyValueFactory<>("value"));
        table.getColumns().add(values);

        indices.prefWidthProperty().bind(table.widthProperty().multiply(INDEX_SIZE));
        values.prefWidthProperty().bind(table.widthProperty().multiply(1 - INDEX_SIZE));

        indices.setResizable(false);
        values.setResizable(false);
        return table;
    }

    private void initMetaheuristic() {
        metaheuristicProperty.addListener((t, u, v) -> {
            trainingPaused.set(false);
            if (v == null || neuralNetwork.get() == null) {
                start.setDisable(true);
            } else {
                start.setDisable(false);
            }
        });
    }

    private EventHandler<ActionEvent> startButtonAction() {
        return event -> {
            start.setDisable(true);
            stop.setDisable(false);
            predict.setDisable(true);
            if (trainingPaused.get()) {
                trainingPaused.set(false);
                new Thread(() -> {
                    metaheuristicProperty.get().run();
                }).start();
                return;
            }

            if (line.getData().size() == 2 && mseChart.getData().size() == 1) {
                line.getData().remove(1);
                mseChart.getData().remove(0);
            }
            series = null;
            mseSeries = null;
            Runnable training = new Runnable() {
                @Override
                public void run() {
                    IMetaheuristic metaheuristic = AlgorithmsGUI.metaheuristic;
                    IObserver realVectorGraphObserver = new RealVectorGraphObserver(neuralNetwork.get(), dataset, series,
                            mseSeries, line, mseChart);
                    IObserver realVectorStatusBarObserver = new RealVectorStatusbarObserver(statusBar);
                    if (metaheuristic instanceof SimpleSA) {
                        RealVector metaheuristicRequirement = (RealVector) AlgorithmsGUI.metaheuristicRequirement;
                        SimpleSA simpleSA = (SimpleSA) metaheuristic;
                        simpleSA.attachObserver(realVectorGraphObserver);
                        simpleSA.attachObserver(realVectorStatusBarObserver);
                        simpleSA.run(new SimpleSolution(metaheuristicRequirement));
                    } else if (metaheuristic instanceof BasicPSO) {
                        Collection<Particle<RealVector>> metaheuristicRequirement =
                                (Collection<Particle<RealVector>>) AlgorithmsGUI.metaheuristicRequirement;
                        BasicPSO basicPSO = (BasicPSO) metaheuristic;
                        basicPSO.attachObserver(realVectorGraphObserver);
                        basicPSO.attachObserver(realVectorStatusBarObserver);
                        basicPSO.run(metaheuristicRequirement);
                    } else if (metaheuristic instanceof Backpropagation) {
                        Backpropagation backpropagation = (Backpropagation) metaheuristic;
                        IObserver graphObserver = new DoubleArrayGraphObserver(neuralNetwork.get(), dataset, series,
                                mseSeries, line, mseChart);
                        IObserver doubleArrayStatusBarObserver = new DoubleArrayStatusbarObserver(statusBar);
                        backpropagation.attachObserver(graphObserver);
                        backpropagation.attachObserver(doubleArrayStatusBarObserver);
                        backpropagation.run();
                    } else if (metaheuristic instanceof SimpleOSGA) {
                        Collection<RealVector> metaheuristicRequirement =
                                (Collection<RealVector>) AlgorithmsGUI.metaheuristicRequirement;
                        SimpleOSGA simpleOSGA = (SimpleOSGA) metaheuristic;
                        simpleOSGA.attachObserver(realVectorGraphObserver);
                        simpleOSGA.attachObserver(realVectorStatusBarObserver);
                        simpleOSGA.run(metaheuristicRequirement);
                    } else if (metaheuristic instanceof SimpleGA) {
                        Collection<RealVector> metaheuristicRequirement =
                                (Collection<RealVector>) AlgorithmsGUI.metaheuristicRequirement;
                        SimpleGA simpleGA = (SimpleGA) metaheuristic;
                        simpleGA.attachObserver(realVectorGraphObserver);
                        simpleGA.attachObserver(realVectorStatusBarObserver);
                        simpleGA.run(metaheuristicRequirement);
                    } else {
                        System.err.println("wrong metaheuristic");
                    }

                    Platform.runLater(() -> {
                        start.setDisable(false);
                        predict.setDisable(false);
                        stop.setDisable(true);
                    });
                }
            };
            new Thread(training).start();
        };
    }

    private EventHandler<ActionEvent> predictAction() {
        return event -> {
            Stage predictStage = new Stage();
            predictStage.setTitle("Predict!");
            predictStage.initOwner(data.getPrimaryStage());
            predictStage.initModality(Modality.WINDOW_MODAL);

            Label numberOfPredictions = new Label("Number of predictions: ");
            TextField predicts = new TextField();

            Label invalidInput = new Label("Invalid input");
            invalidInput.setTextFill(Color.RED);
            invalidInput.setVisible(false);

            Button ok = new Button("OK");

            HBox okBox = new HBox(ok);
            okBox.setAlignment(Pos.CENTER);

            VBox predictBox = new VBox(numberOfPredictions, predicts, invalidInput, okBox);
            predictBox.setSpacing(10);
            predictBox.setPadding(new Insets(20, 20, 20, 20));

            Scene predictScene = new Scene(predictBox);
            predictStage.setScene(predictScene);
            predictStage.show();

            ok.setOnAction((e) -> {
                try {
                    int howManyPredictions = Integer.parseInt(predicts.getText());
                    if (howManyPredictions < 1) {
                        invalidInput.setVisible(true);
                        return;
                    }
                    new Thread(() -> {
                        int nnInputSize = neuralNetwork.get().getInputSize();
                        double[] predictions = new double[nnInputSize + howManyPredictions];
                        System.arraycopy(dataset.get(dataset.size() - 1).getInput(), 0,
                                predictions, 0, nnInputSize);
                        for (int i = 0; i < predictions.length - nnInputSize; ++i) {
                            double[] input = Arrays.copyOfRange(predictions, i, i + nnInputSize);
                            double[] expected = neuralNetwork.get().forward(input);
                            predictions[i + nnInputSize] = expected[0];
                        }
                        ObservableList<DatasetValue> observableList = FXCollections.observableList(
                                DatasetValue.encapsulateDoubleArray(Arrays.copyOfRange(predictions, nnInputSize, predictions.length)));
                        Platform.runLater(() -> {
                            Stage stage = new Stage();
                            stage.setTitle("Predictions");
                            stage.initOwner(data.getPrimaryStage());
                            stage.initModality(Modality.WINDOW_MODAL);

                            NumberAxis xAxis = new NumberAxis();
                            xAxis.setLabel("Sample number");

                            NumberAxis yAxis = new NumberAxis();
                            yAxis.setLabel("Predicted value");

                            XYChart.Series series = new XYChart.Series();
                            series.setName("Prediction");

                            series.setData(getChartData(observableList));

                            LineChart line = new LineChart(xAxis, yAxis);
                            line.getData().add(series);

                            Scene scene = new Scene(line);
                            stage.setScene(scene);

                            stage.show();
                            predictStage.hide();
                        });
                    }).start();
                } catch (Exception e1) {
                    invalidInput.setVisible(true);
                }
            });
        };
    }

    private EventHandler<ActionEvent> changeArchitectureAction(ComboBox arch) {
        return event -> {
            if (arch.getSelectionModel().getSelectedItem().equals("<none>")) {
                neuralNetwork.setValue(null);
                start.setDisable(true);
                return;
            }
            System.out.println(arch.getValue());
            Stage changeArchitecture = new Stage();
            changeArchitecture.initOwner(data.getPrimaryStage());
            changeArchitecture.initModality(Modality.WINDOW_MODAL);
            changeArchitecture.setTitle("Change!");

            NeuralNetworkForm neuralNetworkForm = NeuralNetworkForm.getInstance();
            List<String> activationsList = Arrays.asList("Sigmoid", "Binary Step", "Identity", "ReLU", "TanH");

            Label inputLayer = new Label("Input layer:");
            TextField input = new TextField();
            input.setText(neuralNetworkForm.getInputLayer());

            Label inputLayerActivation = new Label("Input layer activation:");
            ComboBox<String> inputActivation = new ComboBox<>(FXCollections.observableArrayList(
                    "Sigmoid", "Binary Step", "Identity", "ReLU", "TanH"));
            inputActivation.getSelectionModel().select(
                    activationsList.indexOf(neuralNetworkForm.getInputLayerActivation())
            );

            Label hiddenLayers = new Label("Hidden layers(splited by comma):");
            TextField hidden = new TextField();
            hidden.setText(neuralNetworkForm.getHiddenLayers());
            hidden.setTooltip(new Tooltip("Split number of nodes for each hidden layer with a comma"));

            Label hiddenLayerActivation = new Label("Hidden layers activations:");
            ComboBox<String> hiddenActivation = new ComboBox<>(FXCollections.observableArrayList(
                    "Sigmoid", "Binary Step", "Identity", "ReLU", "TanH"));
            hiddenActivation.getSelectionModel().select(
                    activationsList.indexOf(neuralNetworkForm.getHiddenLayersActivation())
            );

            Label outputLayer = new Label("Output layer:");
            TextField output = new TextField();
            output.setText(neuralNetworkForm.getOutputLayer());

            Label outputLayerActivation = new Label("Output layer activation:");
            ComboBox<String> outputActivation = new ComboBox<>(FXCollections.observableArrayList(
                    "Sigmoid", "Binary Step", "Identity", "ReLU", "TanH"));
            outputActivation.getSelectionModel().select(
                    activationsList.indexOf(neuralNetworkForm.getOutputLayerActivation())
            );

            Label datasetLabel = new Label("Train set percentage:");
            Slider dataSlider = new Slider(0, 100, 90);
            dataSlider.setValue(neuralNetworkForm.getPercentage());
            dataSlider.setShowTickMarks(true);
            dataSlider.setShowTickLabels(true);
            dataSlider.setMajorTickUnit(10);
            dataSlider.setMinorTickCount(0);
            dataSlider.setSnapToTicks(true);

            Label invalidInput = new Label("Invalid input");
            invalidInput.setTextFill(Color.RED);
            invalidInput.setVisible(false);

            Button ok = new Button("OK");

            HBox okBox = new HBox(ok);
            okBox.setAlignment(Pos.CENTER);

            HBox invalidBox = new HBox(invalidInput);
            invalidBox.setAlignment(Pos.CENTER);
            ok.setOnAction(okButtonHandler(hidden, input, output, dataSlider, inputActivation, outputActivation,
                    hiddenActivation, invalidInput, changeArchitecture));

            GridPane gridPane = new GridPane();
            gridPane.setPadding(new Insets(20, 20, 20, 20));
            gridPane.setHgap(10);
            gridPane.setVgap(10);

            gridPane.add(inputLayer, 0, 0);
            gridPane.add(input, 1, 0);
            gridPane.add(inputLayerActivation, 0, 1);
            gridPane.add(inputActivation, 1, 1);

            gridPane.add(hiddenLayers, 0, 2);
            gridPane.add(hidden, 1, 2);
            gridPane.add(hiddenLayerActivation, 0, 3);
            gridPane.add(hiddenActivation, 1, 3);

            gridPane.add(outputLayer, 0, 4);
            gridPane.add(output, 1, 4);
            gridPane.add(outputLayerActivation, 0, 5);
            gridPane.add(outputActivation, 1, 5);

            gridPane.add(datasetLabel, 0, 6);
            gridPane.add(dataSlider, 1, 6);

            gridPane.add(invalidBox, 0, 7, 2, 1);
            gridPane.add(okBox, 0, 8, 2, 1);

            Scene gridScene = new Scene(gridPane);
            changeArchitecture.setScene(gridScene);
            changeArchitecture.show();
        };
    }

    private EventHandler<ActionEvent> okButtonHandler(TextField hidden, TextField input, TextField output,
                                                      Slider dataSlider, ComboBox<String> inputActivation,
                                                      ComboBox<String> outputActivation, ComboBox<String> hiddenActivation,
                                                      Label invalidInput, Stage changeArchitecture) {
        return e -> {
            NeuralNetworkForm neuralNetworkForm = NeuralNetworkForm.getInstance();
            try {
                String[] hiddens = hidden.getText().split(",");
                architecture = new int[hiddens.length + 2];
                architecture[0] = Integer.parseInt(input.getText());
                architecture[architecture.length - 1] = Integer.parseInt(output.getText());
                for (int i = 1; i < architecture.length - 1; i++) {
                    architecture[i] = Integer.parseInt(hiddens[i - 1].trim());
                }
                System.out.println(Arrays.toString(architecture));

                trainPercentage = dataSlider.getValue() / 100.;

                neuralNetworkForm.setHiddenLayers(hidden.getText());
                neuralNetworkForm.setInputLayer(input.getText());
                neuralNetworkForm.setOutputLayer(output.getText());
                neuralNetworkForm.setPercentage((int) dataSlider.getValue());

                activations = new IActivation[architecture.length];
                activations[0] = extractActivation(inputActivation.getValue());
                for (int i = 1; i < activations.length - 1; ++i) {
                    activations[i] = extractActivation(hiddenActivation.getValue());
                }
                activations[activations.length - 1] = extractActivation(outputActivation.getValue());

                neuralNetworkForm.setOutputLayerActivation(outputActivation.getValue());
                neuralNetworkForm.setHiddenLayersActivation(hiddenActivation.getValue());
                neuralNetworkForm.setInputLayerActivation(inputActivation.getValue());

                if (chooseNetwork.getValue().equals("Elman ANN")) {
                    neuralNetwork.setValue(new ElmanNN(architecture, activations));
                    dataset = DatasetValue.getTrainingData(data.getDatasetValues(), neuralNetwork.get().getInputSize(),
                            neuralNetwork.get().getOutputSize());
                } else if (chooseNetwork.getValue().equals("TDNN")) {
                    neuralNetwork.setValue(new FeedForwardANN(architecture, activations));
                    dataset = DatasetValue.getTrainingData(data.getDatasetValues(),
                            neuralNetwork.get().getInputSize(), neuralNetwork.get().getOutputSize());
                } else {
                    neuralNetwork.setValue(null);
                }
                changeArchitecture.hide();
            } catch (NumberFormatException nfe) {
                invalidInput.setVisible(true);
            }
        };
    }
}