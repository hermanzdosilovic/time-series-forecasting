package hr.fer.zemris.project.forecasting.gui;

import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import hr.fer.zemris.project.forecasting.gp.GeneticProgramming;
import hr.fer.zemris.project.forecasting.gp.gui.IListener;
import hr.fer.zemris.project.forecasting.gp.selections.Tournament;
import hr.fer.zemris.project.forecasting.gp.tree.BinaryTree;
import hr.fer.zemris.project.forecasting.gui.forms.GeneticProgrammingForm;
import hr.fer.zemris.project.forecasting.nn.util.NeuralNetworkUtil;
import hr.fer.zemris.project.forecasting.util.Pair;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static hr.fer.zemris.project.forecasting.gui.ARIMAUI.allowOneSeriesUponDatasetChangeListener;
import static hr.fer.zemris.project.forecasting.gui.Data.*;
import static hr.fer.zemris.project.forecasting.gui.DatasetValue.getChartData;

public class GeneticProgrammingUI implements IListener<BinaryTree> {

    private static int  PREDICTION_WIDTH  = 640;
    private static int  PREDICTION_HEIGHT = 480;
    private static int  MSE_WIDTH         = 640;
    private static int  MSE_HEIGHT        = 480;
    private static long PERIOD            = 1000L;

    private Data               data;
    private GeneticProgramming geneticProgramming;

    private volatile LineChart mseChart;
    private volatile LineChart predictionChart;

    private volatile XYChart.Series predictionSeries;
    private volatile XYChart.Series mseTrainSeries;
    private volatile XYChart.Series mseTestSeries;

    private Label trainMseL;
    private Label testMseL;
    private Label iteration;

    private long lastUpdated;

    public GeneticProgrammingUI(Data data) {
        this.data = data;
    }

    public void createUI(Pane parent) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(30, 30, 30, 30));

        Button start = new Button("Start");
        start.setDisable(true);

        Button predict = new Button("Predict future values");
        predict.setDisable(true);

        Button editParams = new Button("Set parameters");
        editParams.setDisable(false);
        editParams.setOnAction(createAndShowParamsGrid(start));
        grid.add(editParams, 1, 0);

        initializePredictionChart(start, predict);
        initializeMseChart();

        TableView table = generateTable();
        grid.add(table, 1, 2);

        Button stop = new Button("Stop");
        stop.setDisable(true);

        setActionForStart(start, stop, predict);
        grid.add(start, 1, 4);

        setActionForStop(start, stop);
        grid.add(stop, 1, 5);

        grid.add(predictionChart, 2, 1, 3, 3);
        grid.add(mseChart, 5, 1, 3, 3);

        trainMseL = new Label();
        testMseL = new Label();
        iteration = new Label();
        trainMseL.setFont(new Font("Arial", 20));
        testMseL.setFont(new Font("Arial", 20));
        iteration.setFont(new Font("Arial", 20));

        grid.add(trainMseL, 4, 6, 3, 1);
        grid.add(testMseL, 4, 7, 3, 1);
        grid.add(iteration, 4, 8, 3, 1);

        predict.setOnAction(predictAction(predict));
        grid.add(predict, 1, 6);

        parent.getChildren().add(grid);
    }

    private void initializePredictionChart(Button start, Button predict) {
        predictionChart = Data.lineChart("Training chart", PREDICTION_WIDTH, PREDICTION_HEIGHT);
        XYChart.Series series = new XYChart.Series("Dataset", getChartData(data.getDatasetValues()));
        predictionChart.getData().add(series);

        setInitialStateUponDatasetChange(data.getDatasetValues(), start, predict);
        updateSeriesOnListChangeListener(data.getDatasetValues(), series);
        allowOneSeriesUponDatasetChangeListener(data.getDatasetValues(), predictionChart);
    }

    private void setInitialStateUponDatasetChange(
        ObservableList<DatasetValue> datasetValues,
        Button start,
        Button predict
    ) {
        datasetValues.addListener((ListChangeListener<DatasetValue>) c -> {
            start.setDisable(true);
            predict.setDisable(true);
            mseChart.getData().clear();
            trainMseL.setText("");
            testMseL.setText("");
            iteration.setText("");
        });
    }

    private void initializeMseChart() {
        mseChart = Data.mseLineChart("Mse chart", MSE_WIDTH, MSE_HEIGHT);
        mseChart.setCreateSymbols(false);
    }

    private void setActionForStop(Button start, Button stop) {
        stop.setOnAction(
            event -> {
                start.setDisable(false);
                stop.setDisable(true);
                new Thread(() -> {
                    try {
                        geneticProgramming.stop();
                    } catch (RuntimeException r) {
                        GUIUtil.showErrorMessage(
                            String.format("Something's gone terribly wrong %s.", r.getMessage()),
                            data
                        );
                    }
                }).start();
            }
        );
    }

    private void setActionForStart(Button start, Button stop, Button predictions) {
        start.setOnAction(
            event -> {
                start.setDisable(true);
                stop.setDisable(false);
                predictions.setDisable(true);
//                predictionChart.getData().clear();
                if (predictionChart.getData().size() > 1) {
                    predictionChart.getData().remove(1);
                }
                mseChart.setTitle("Mse chart");
                mseChart.getData().clear();
                initializeMseSeries();
                mseChart.getData().add(mseTrainSeries);
                mseChart.getData().add(mseTestSeries);
                new Thread(() -> {
                    try {
                        geneticProgramming.addListener(this);
                        BinaryTree            solution = geneticProgramming.start();
                        Platform.runLater(() -> {
                            start.setDisable(false);
                            stop.setDisable(true);
                            predictions.setDisable(false);
                            trainMseL.setText(String.format("Train MSE:  %.2f", solution.getTrainFitness()));
                            testMseL.setText(String.format("Test MSE:  %.2f", solution.getTestFitness()));
                        });
                    } catch (RuntimeException r) {
                        GUIUtil.showErrorMessage(
                            String.format("Something's gone terribly wrong %s.", r.getMessage()),
                            data
                        );
                    }
                }).start();
            });
    }

    private void initializeMseSeries() {
        mseTrainSeries = new XYChart.Series();
        mseTrainSeries.setName("Mse train");
        mseTestSeries = new XYChart.Series();
        mseTestSeries.setName("Mse test");
    }

    private EventHandler<ActionEvent> createAndShowParamsGrid(Button start) {
        return event -> {
            Stage params = new Stage();
            params.initOwner(data.getPrimaryStage());
            params.initModality(Modality.WINDOW_MODAL);
            params.setTitle("Genetic programming parameters :");

            GeneticProgrammingForm gpForm = GeneticProgrammingForm.getInstance();

            Label     populationSizeL  = new Label("Population size:");
            TextField populationSizeTF = new TextField(gpForm.getPopulationSize());

            Label     maxGenerationsL  = new Label("Max generations:");
            TextField maxGenerationsTF = new TextField(gpForm.getMaxGenerations());

            Label     tournamentSizeL  = new Label("Tournament size:");
            TextField tournamentSizeTF = new TextField(gpForm.getTournamentSize());

            Label     desiredFitnessL  = new Label("Desired fitness:");
            TextField desiredFitnessTF = new TextField(gpForm.getDesiredFitness());

            Label     startDepthL  = new Label("Tree start depth:");
            TextField startDepthTF = new TextField(gpForm.getStartDepth());

            Label     maxDepthL  = new Label("Tree max depth:");
            TextField maxDepthTF = new TextField(gpForm.getMaxDepth());

            Label     maxNodesL  = new Label("Max number of nodes in a tree:");
            TextField maxNodesTF = new TextField(gpForm.getMaxNodes());

            Label     reproductionProbabilityL  = new Label("Probability of reproduction:");
            TextField reproductionProbabilityTF = new TextField(gpForm.getReproductionProbability());

            Label     mutationProbabilityL  = new Label("Probability of mutation:");
            TextField mutationProbabilityTF = new TextField(gpForm.getMutationProbability());

            Label     offsetL  = new Label("Number of past values used (offset):");
            TextField offsetTF = new TextField(gpForm.getOffset());

            CheckBox useElitismCB = new CheckBox("Use elitism?");
            useElitismCB.setSelected(gpForm.isUseElitism());

            CheckBox allowDuplicatesCB = new CheckBox("Allow duplicates?");
            allowDuplicatesCB.setSelected(gpForm.isAllowDuplicates());

            Label  datasetSplitL = new Label("Train set percentage:");
            Slider datasetSplitS = new Slider(0, 10, gpForm.getPercentage());
            datasetSplitS.setShowTickMarks(true);
            datasetSplitS.setShowTickLabels(true);
            datasetSplitS.setMajorTickUnit(1);
            datasetSplitS.setMinorTickCount(0);
            datasetSplitS.setSnapToTicks(true);

            Label invalidInput = new Label("Invalid input.");
            invalidInput.setTextFill(Color.RED);
            invalidInput.setVisible(false);

            Button ok = new Button("OK");

            GridPane grid = new GridPane();
            grid.setVgap(15);
            grid.setHgap(10);
            grid.setPadding(new Insets(20, 20, 20, 20));

            grid.add(populationSizeL, 0, 0);
            grid.add(populationSizeTF, 1, 0);
            grid.add(maxGenerationsL, 2, 0);
            grid.add(maxGenerationsTF, 3, 0);

            grid.add(tournamentSizeL, 0, 1);
            grid.add(tournamentSizeTF, 1, 1);
            grid.add(desiredFitnessL, 2, 1);
            grid.add(desiredFitnessTF, 3, 1);

            grid.add(startDepthL, 0, 2);
            grid.add(startDepthTF, 1, 2);
            grid.add(maxDepthL, 2, 2);
            grid.add(maxDepthTF, 3, 2);

            grid.add(maxNodesL, 0, 3);
            grid.add(maxNodesTF, 1, 3);
            grid.add(offsetL, 2, 3);
            grid.add(offsetTF, 3, 3);

            grid.add(reproductionProbabilityL, 0, 4);
            grid.add(reproductionProbabilityTF, 1, 4);
            grid.add(mutationProbabilityL, 2, 4);
            grid.add(mutationProbabilityTF, 3, 4);

            grid.add(datasetSplitL, 0, 5);
            grid.add(datasetSplitS, 1, 5);

            HBox invalidBox = new HBox(invalidInput);
            invalidBox.setAlignment(Pos.CENTER);

            HBox okBox = new HBox(ok);
            okBox.setAlignment(Pos.CENTER);

            HBox boxes = new HBox(useElitismCB, allowDuplicatesCB);
            boxes.setSpacing(10);
            boxes.setAlignment(Pos.CENTER);

            grid.add(boxes, 0, 6, 4, 1);
            grid.add(invalidBox, 0, 8, 4, 1);
            grid.add(okBox, 0, 9, 4, 1);

            Scene scene = new Scene(grid);
            params.setScene(scene);
            params.show();

            EventHandler<ActionEvent> buttonHandler = okEvent -> {
                try {
                    int     populationSize          = Integer.parseInt(populationSizeTF.getText());
                    int     maxGenerations          = Integer.parseInt(maxGenerationsTF.getText());
                    int     tournamentSize          = Integer.parseInt(tournamentSizeTF.getText());
                    int     maxNodes                = Integer.parseInt(maxNodesTF.getText());
                    int     maxDepth                = Integer.parseInt(maxDepthTF.getText());
                    int     startDepth              = Integer.parseInt(startDepthTF.getText());
                    int     offset                  = Integer.parseInt(offsetTF.getText());
                    double  reproductionProbability = Double.parseDouble(reproductionProbabilityTF.getText());
                    double  mutationProbability     = Double.parseDouble(mutationProbabilityTF.getText());
                    double  desiredFittnes          = Double.parseDouble(desiredFitnessTF.getText());
                    boolean elitism                 = useElitismCB.isSelected();
                    boolean duplicates              = allowDuplicatesCB.isSelected();
                    int     splitPercentage         = (int) datasetSplitS.getValue();

                    Pair<List<DatasetEntry>, List<DatasetEntry>> splittedDataEntries =
                        NeuralNetworkUtil.splitTDNNDataset(DatasetValue.getTrainingData(
                            data.getDatasetValues(),
                            offset,
                            1
                        ), splitPercentage / 10.0);

                    geneticProgramming = new GeneticProgramming(
                        maxGenerations,
                        populationSize,
                        desiredFittnes,
                        startDepth,
                        reproductionProbability,
                        mutationProbability,
                        new Tournament(tournamentSize),
                        maxDepth,
                        maxNodes,
                        duplicates,
                        elitism,
                        offset,
                        splittedDataEntries.getFirst(),
                        splittedDataEntries.getSecond()
                    );

                    gpForm.setPopulationSize(populationSizeTF.getText());
                    gpForm.setMaxGenerations(maxGenerationsTF.getText());
                    gpForm.setTournamentSize(tournamentSizeTF.getText());
                    gpForm.setReproductionProbability(reproductionProbabilityTF.getText());
                    gpForm.setMutationProbability(mutationProbabilityTF.getText());
                    gpForm.setUseElitism(useElitismCB.isSelected());
                    gpForm.setAllowDuplicates(allowDuplicatesCB.isSelected());
                    gpForm.setDesiredFitness(desiredFitnessTF.getText());
                    gpForm.setMaxDepth(maxDepthTF.getText());
                    gpForm.setStartDepth(startDepthTF.getText());
                    gpForm.setMaxNodes(maxNodesTF.getText());
                    gpForm.setOffset(offsetTF.getText());
                    gpForm.setPercentage(splitPercentage);

                    params.hide();
                } catch (RuntimeException ex) {
                    invalidInput.setVisible(true);
                }
                start.setDisable(false);
                event.consume();
            };
            ok.setOnAction(buttonHandler);
        };
    }


    private TableView generateTable() {
        TableView table = new TableView();
        table.setPrefWidth(MAX_TABLE_WIDTH);
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(data.getDatasetValues());

        TableColumn<DatasetValue, Double> values = new TableColumn("Value");
        values.setSortable(false);
        values.setEditable(true);

        values.setCellValueFactory(new PropertyValueFactory<>("value"));

        TableColumn<DatasetValue, Integer> indices = new TableColumn<>("Index");
        indices.setSortable(false);
        indices.setEditable(false);

        indices.setCellValueFactory(new PropertyValueFactory<>("index"));

        table.getColumns().add(indices);
        table.getColumns().add(values);

        indices.prefWidthProperty().bind(table.widthProperty().multiply(INDEX_SIZE));
        values.prefWidthProperty().bind(table.widthProperty().multiply(1 - INDEX_SIZE));
        indices.setResizable(false);
        values.setResizable(false);

        return table;
    }

    @Override public void newBest(BinaryTree best, Integer iter) {
        Platform.runLater(() -> {
            Map<String, double[]> data = geneticProgramming.getForecastedData(best);
            double[] forecasted = obtainForecasted(data);
            String key = "Forecasted";
            if (predictionChart.getData().size() == 1) {
                predictionSeries = new XYChart.Series(key, DatasetValue.getChartData(
                    FXCollections.observableArrayList(DatasetValue.encapsulateDoubleArray(forecasted))));
                predictionChart.getData().add(predictionSeries);
            } else {
                long currentTime = System.currentTimeMillis();
                if (currentTime > lastUpdated + PERIOD) {
                    predictionSeries.setData(DatasetValue.getChartData(FXCollections.observableArrayList(DatasetValue.encapsulateDoubleArray(
                        forecasted))));
                    lastUpdated = currentTime;
                }
            }
            mseTrainSeries.getData().add(new XYChart.Data<>(mseTrainSeries.getData().size(), best.getTrainFitness()));
            mseTestSeries.getData().add(new XYChart.Data<>(mseTestSeries.getData().size(), best.getTestFitness()));
            trainMseL.setText(String.format("Train MSE: %.2f", best.getTrainFitness()));
            testMseL.setText(String.format("Test MSE: %.2f", best.getTestFitness()));
            iteration.setText(String.format("Iter: %d", iter));
        });
    }

    private double[] obtainForecasted(Map<String, double[]> data) {
        double[] forecasted = data.get("Forecasted");
        DatasetEntry datasetEntry = geneticProgramming.returnFirst();
        return ArrayUtils.addAll(
            datasetEntry.getInput(),
            forecasted
        );
    }

    private EventHandler<ActionEvent> predictAction(Button predict) {
        return event -> {
            Stage predictStage = new Stage();
            predictStage.setTitle("Predict next values!");
            predictStage.initOwner(data.getPrimaryStage());
            predictStage.initModality(Modality.WINDOW_MODAL);

            Label     numberOfPredictions = new Label("Number of predictions: ");
            TextField predicts            = new TextField();

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
                    new Thread(() -> {
                        double[] predictions = geneticProgramming.predictNextValues(howManyPredictions);
                        ObservableList<DatasetValue> observableList = FXCollections.observableList(
                            DatasetValue.encapsulateDoubleArray(predictions));
                        XYChart.Series series = new XYChart.Series("Prediction", getChartData(observableList));
                        Platform.runLater(() -> {
                            mseChart.setTitle("Predictions chart:");
                            mseChart.getData().clear();
                            mseChart.getData().add(series);
                            predictStage.hide();
                        });
                    }).start();
                } catch (Exception e1) {
                    System.out.println(e1.getMessage());
                    invalidInput.setVisible(true);
                }
            });
        };
    }

}
