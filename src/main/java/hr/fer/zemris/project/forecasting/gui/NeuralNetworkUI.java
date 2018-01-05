package hr.fer.zemris.project.forecasting.gui;

import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.IMetaheuristic;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.ga.SimpleGA;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.ga.SimpleOSGA;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.pso.BasicPSO;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.pso.Particle;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.sa.SimpleSA;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.util.IObserver;
import com.dosilovic.hermanzvonimir.ecfjava.neural.ElmanNN;
import com.dosilovic.hermanzvonimir.ecfjava.neural.FeedForwardANN;
import com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork;
import com.dosilovic.hermanzvonimir.ecfjava.neural.activations.*;
import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import com.dosilovic.hermanzvonimir.ecfjava.util.RealVector;
import com.dosilovic.hermanzvonimir.ecfjava.util.Solution;
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

import static hr.fer.zemris.project.forecasting.gui.Data.*;
import static hr.fer.zemris.project.forecasting.gui.DatasetValue.getChartData;

public class NeuralNetworkUI {

    private Data data;
    private int[] architecture;
    private ObjectProperty<INeuralNetwork> neuralNetwork = new SimpleObjectProperty<>(null);
    private IActivation[] activations;
    private List<DatasetEntry> dataset;
    private ComboBox<String> chooseNetwork;
    private ComboBox<String> chooseAlgorithm;
    private Button changeParams;
    private double trainPercentage;
    private LineChart line;
    private XYChart.Series<Integer, Double> series;

    public NeuralNetworkUI(Data data) {
        this.data = data;
    }

    public void createUI(Pane parent) {
        GridPane grid = new GridPane();

        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(30, 30, 30, 30));
        //choose neural network button
        chooseNetwork = new ComboBox<>(FXCollections.observableArrayList("<none>", "TDNN", "Elman ANN"));
        chooseNetwork.getSelectionModel().select(0);

        chooseNetwork.setOnAction(changeArchitectureAction(chooseNetwork));
        //change architecture button
        Button changeArch = new Button("Change architecture");
        changeArch.setOnAction(changeArchitectureAction(chooseNetwork));

        HBox neural = new HBox(chooseNetwork, changeArch);

        //choose algorithm
        chooseAlgorithm = new ComboBox<>(FXCollections.observableArrayList(
                "<none>", "Genetic", "OSGA", "SA", "DE", "PSO", "Backpropagation"));
        chooseAlgorithm.getSelectionModel().select(0);

        changeParams = new Button("Change parameters");
        HBox params = new HBox(chooseAlgorithm, changeParams);

        //Immutable dataset
        TableView table = new TableView();
        table.setPrefWidth(MAX_TABLE_WIDTH);
        table.setMaxWidth(MAX_TABLE_WIDTH);
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(data.getDatasetValues());

        TableColumn<DatasetValue, Double> values = new TableColumn("Data Set Values");
        values.setSortable(false);
        values.setEditable(true);

        values.setCellValueFactory(new PropertyValueFactory<>("value"));

        table.getColumns().add(values);

        //start button
        Button start = new Button("Start training");
        start.setOnAction(a -> {
            start.setDisable(true);
            line.getData().remove(series);
            series = null;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    IMetaheuristic metaheuristic = AlgorithmsGUI.metaheuristic;
                    if (metaheuristic instanceof SimpleSA) {
                        RealVector metaheuristicRequirement = (RealVector) AlgorithmsGUI.metaheuristicRequirement;
                        ((SimpleSA) metaheuristic).attachObserver(new GraphRealVectorObserver(neuralNetwork.get()));
                        ((SimpleSA) metaheuristic).run(metaheuristicRequirement);
                    } else if (metaheuristic instanceof BasicPSO) {
                        Collection<Particle<RealVector>> metaheuristicRequirement =
                                (Collection<Particle<RealVector>>) AlgorithmsGUI.metaheuristicRequirement;
                        ((BasicPSO) metaheuristic).attachObserver(new GraphRealVectorObserver(neuralNetwork.get()));
                        ((BasicPSO) metaheuristic).run(metaheuristicRequirement);
                    } else if (metaheuristic instanceof Backpropagation) {
                        ((Backpropagation) metaheuristic).attachObserver(new GraphObserver(neuralNetwork.get()));
                        ((Backpropagation) metaheuristic).run();
                    } else if (metaheuristic instanceof SimpleOSGA) {
                        Collection<RealVector> metaheuristicRequirement =
                                (Collection<RealVector>) AlgorithmsGUI.metaheuristicRequirement;
                        ((SimpleOSGA) metaheuristic).attachObserver(new GraphRealVectorObserver(neuralNetwork.get()));
                        ((SimpleOSGA) metaheuristic).run(metaheuristicRequirement);
                    } else if (metaheuristic instanceof SimpleGA) {
                        Collection<RealVector> metaheuristicRequirement =
                                (Collection<RealVector>) AlgorithmsGUI.metaheuristicRequirement;
                        ((SimpleGA) metaheuristic).attachObserver(new GraphRealVectorObserver(neuralNetwork.get()));
                        ((SimpleGA) metaheuristic).run(metaheuristicRequirement);
                    } else {
                        System.err.println("wrong metaheurstic");
                    }
                    Platform.runLater(() -> start.setDisable(false));
                }
            };
            new Thread(runnable).start();

        });

        neuralNetwork.addListener((t, u, v) -> {
            if (v != null) {
                if (v instanceof ElmanNN) {
                    chooseAlgorithm = new ComboBox<>(FXCollections.observableArrayList(
                            "<none>", "Genetic", "OSGA", "SA", "DE", "PSO"));
                } else {
                    chooseAlgorithm = new ComboBox<>(FXCollections.observableArrayList(
                            "<none>", "Genetic", "OSGA", "SA", "DE", "PSO", "Backpropagation"));
                }
                chooseAlgorithm.getSelectionModel().select(0);
//                chooseAlgorithm.setDisable(false);
//                changeParams.setDisable(false);
            } else {
//                chooseAlgorithm.setDisable(true);
//                changeParams.setDisable(true);
            }
        });
//        chooseAlgorithm.setDisable(true);
//        changeParams.setDisable(true);

        //Button predict
        Button predict = new Button("Predict future values");

        VBox rightSide = new VBox();

        //line chart
        XYChart.Series<Integer, Double> series = new XYChart.Series();
        series.setName("Expected");
        ObservableList<XYChart.Data<Integer, Double>> observableList = DatasetValue.getChartData(data.getDatasetValues());
        series.setData(observableList);
        updateSeriesOnListChangeListener(data.getDatasetValues(), series);
        line = lineChart(series, "Data");

        GridPane rightSideGrid = new GridPane();
        rightSideGrid.setHgap(10);
        rightSideGrid.setVgap(10);
        rightSideGrid.add(start, 1, 0);
        rightSideGrid.add(predict, 1, 1);
        rightSide.getChildren().add(rightSideGrid);

        grid.add(neural, 0, 0);
        grid.add(params, 0, 1);
        grid.add(table, 0, 2);
        grid.add(line, 1, 0, 3, 3);
        grid.add(rightSide, 1, 4);

        parent.getChildren().add(grid);
    }

    private EventHandler<ActionEvent> changeArchitectureAction(ComboBox arch) {
        return event -> {
            if (arch.getSelectionModel().getSelectedItem().equals("<none>")) return;
            System.out.println(arch.getValue());
            Stage changeArch = new Stage();
            changeArch.initOwner(data.getPrimaryStage());
            changeArch.initModality(Modality.WINDOW_MODAL);
            changeArch.setTitle("Change!");

            Label inputLayer = new Label("Input layer:");
            TextField input = new TextField();
            Label inputLayerActivation = new Label("Input layer activation:");
            ComboBox<String> inputActivation = new ComboBox<>(FXCollections.observableArrayList(
                    "Sigmoid", "Binary Step", "Identity", "ReLU", "TanH"));
            inputActivation.getSelectionModel().select(0);

            Label hiddenLayers = new Label("Hidden layers:");
            TextField hidden = new TextField();
            hidden.setTooltip(new Tooltip("Split number of nodes for each hidden layer with a comma"));
            Label hiddenLayerActivation = new Label("Hidden layers activations:");
            ComboBox<String> hiddenActivation = new ComboBox<>(FXCollections.observableArrayList(
                    "Sigmoid", "Binary Step", "Identity", "ReLU", "TanH"));
            hiddenActivation.getSelectionModel().select(0);

            Label outputLayer = new Label("Output layer:");
            TextField output = new TextField();
            Label outputLayerActivation = new Label("Output layer activation:");
            ComboBox<String> outputActivation = new ComboBox<>(FXCollections.observableArrayList(
                    "Sigmoid", "Binary Step", "Identity", "ReLU", "TanH"));
            outputActivation.getSelectionModel().select(0);

            Label datasetLabel = new Label("Train set percentage:");
            Slider dataSlider = new Slider(0, 100, 90);
            dataSlider.setShowTickMarks(true);
            dataSlider.setShowTickLabels(true);
            dataSlider.setMajorTickUnit(10);
            dataSlider.setMinorTickCount(0);
            dataSlider.setSnapToTicks(true);


            if (architecture != null) {
                input.setText(architecture[0] + "");
                output.setText(architecture[architecture.length - 1] + "");
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < architecture.length - 1; i++) {
                    sb.append(architecture[i]);
                    if (i != architecture.length - 2) sb.append(", ");
                }
                hidden.setText(sb.toString());
            }
            Label invalidInput = new Label("Invalid input");
            invalidInput.setTextFill(Color.RED);
            invalidInput.setVisible(false);

            Button ok = new Button("OK");

            HBox okBox = new HBox(ok);
            okBox.setAlignment(Pos.CENTER);

            HBox invalidBox = new HBox(invalidInput);
            invalidBox.setAlignment(Pos.CENTER);
            ok.setOnAction((e) -> {
                try {
                    String[] hiddens = hidden.getText().split(",");
                    architecture = new int[hiddens.length + 2];
                    architecture[0] = Integer.parseInt(input.getText());
                    architecture[architecture.length - 1] = Integer.parseInt(output.getText());
                    for (int i = 1; i < architecture.length - 1; i++) {
                        architecture[i] = Integer.parseInt(hiddens[i - 1]);
                    }
                    System.out.println(Arrays.toString(architecture));

                    trainPercentage = dataSlider.getValue() / 100.;

                    activations = new IActivation[architecture.length];
                    activations[0] = extractActivation(inputActivation.getValue());
                    for (int i = 1; i < activations.length - 1; ++i) {
                        activations[i] = extractActivation(hiddenActivation.getValue());
                    }
                    activations[activations.length - 1] = extractActivation(outputActivation.getValue());
                    if (chooseNetwork.getValue().equals("Elman ANN")) {
                        neuralNetwork.setValue(new ElmanNN(architecture, activations));
                        dataset = DatasetValue.getTrainingData(data.getDatasetValues(), neuralNetwork.get().getInputSize(),
                                neuralNetwork.get().getOutputSize());
                    } else if (chooseNetwork.getValue().equals("TDNN")) {
                        neuralNetwork.setValue(new FeedForwardANN(architecture, activations));
                        dataset = DatasetValue.getTrainingData(data.getDatasetValues(),
                                neuralNetwork.get().getInputSize(), neuralNetwork.get().getOutputSize());
                    } else {
                        //TODO: a sta tu
                    }
                    chooseAlgorithm.setOnAction(AlgorithmsGUI.chooseAlgorithmAction(chooseAlgorithm, dataset, trainPercentage,
                            neuralNetwork.get(), data.getPrimaryStage()));
                    changeParams.setOnAction(AlgorithmsGUI.chooseAlgorithmAction(chooseAlgorithm, dataset, trainPercentage,
                            neuralNetwork.get(), data.getPrimaryStage()));

                    changeArch.hide();
                } catch (NumberFormatException nfe) {
                    invalidInput.setVisible(true);
                }
            });

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
            changeArch.setScene(gridScene);
            changeArch.show();
        };
    }

    private static IActivation extractActivation(String activation) {
        switch (activation) {
            case "Sigmoid":
                return SigmoidActivation.getInstance();
            case "Binary Step":
                return BinaryStepActivation.getInstance();
            case "Identity":
                return IdentityActivation.getInstance();
            case "ReLU":
                return ReLUActivation.getInstance();
            case "TanH":
                return TanHActivation.getInstance();
            default:
                return null;
        }
    }

    private class GraphObserver implements IObserver<double[]> {
        private INeuralNetwork nn;

        private long iteration;

        public GraphObserver(INeuralNetwork nn) {
            this.nn = nn instanceof ElmanNN ? new ElmanNN(nn.getArchitecture(), nn.getLayerActivations()) :
                    new FeedForwardANN(nn.getArchitecture(), nn.getLayerActivations());
        }

        @Override
        public void update(Solution<double[]> solution) {
            ++GraphObserver.this.iteration;
            if (iteration % 10 != 0) {
                return;
            }
            Runnable plot = new Runnable() {
                @Override
                public void run() {
                    if (NeuralNetworkUI.this.series == null) {
                        NeuralNetworkUI.this.series = new XYChart.Series();
                        NeuralNetworkUI.this.series.setName("Forecast");
                        line.getData().add(NeuralNetworkUI.this.series);
                    }
                    double[] weights = solution.getRepresentative();
                    nn.setWeights(weights);

                    ObservableList<XYChart.Data<Integer, Double>> observableList = FXCollections.observableArrayList();
                    for (int i = 0; i < dataset.size(); i++) {
                        double[] forecast = nn.forward(dataset.get(i).getInput());
                        observableList.add(new XYChart.Data<>(i + 1, forecast[0]));
                        observableList.get(i).setNode(new DatasetValue.HoveredThresholdNode(
                                observableList.get(i).getXValue(), observableList.get(i).getYValue()
                        ));
                    }
                    NeuralNetworkUI.this.series.setData(observableList);
                }
            };
            Platform.runLater(plot);
        }
    }

    private class GraphRealVectorObserver implements IObserver<RealVector> {

        IObserver<double[]> graphObserver;

        private GraphRealVectorObserver(INeuralNetwork nn) {
            graphObserver = new GraphObserver(nn);
        }

        @Override
        public void update(Solution<RealVector> solution) {
            graphObserver.update(new Solution<>(solution.getRepresentative().toArray()));
        }
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
                        for (int i = 0; i < predictions.length-nnInputSize; ++i) {
                            double[] input = Arrays.copyOfRange(predictions,i,i+nnInputSize);
                             double[] expected = neuralNetwork.get().forward(input);
                            predictions[i+nnInputSize+1] = expected[0];
                        }
//                        double[] predictions = arima.computeNextValues(howManyPredictions);
                        ObservableList<DatasetValue> observableList = FXCollections.observableList(
                                DatasetValue.encapsulateDoubleArray(Arrays.copyOfRange(predictions,nnInputSize,predictions.length)));
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
                    }).run();
                } catch (Exception e1) {
                    invalidInput.setVisible(true);
                }
            });
        };
    }
}