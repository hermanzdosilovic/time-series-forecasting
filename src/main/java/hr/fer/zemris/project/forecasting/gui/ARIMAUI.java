package hr.fer.zemris.project.forecasting.gui;

import hr.fer.zemris.project.forecasting.models.AModel;
import hr.fer.zemris.project.forecasting.models.ARIMA;
import hr.fer.zemris.project.forecasting.models.arma.ARMA;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static hr.fer.zemris.project.forecasting.gui.Data.*;
import static hr.fer.zemris.project.forecasting.gui.DatasetValue.getChartData;

public class ARIMAUI {
    private Data data;
    private ARIMA arima;

    public ARIMAUI(Data data) {
        this.data = data;
    }

    public static int MAX_ORDER = 10;

    public void createUI(Pane parent) {
        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(30, 30, 30, 30));

        //AR slider
        Label arOrder = new Label("AR order");
        Slider ar = new Slider(0, MAX_ORDER, 0);

        VBox arBox = new VBox(arOrder, ar);
        arBox.setSpacing(10);

        grid.add(arBox, 0, 0);
        ar.setShowTickMarks(true);
        ar.setShowTickLabels(true);
        ar.setMajorTickUnit(1);
        ar.setMinorTickCount(0);
        ar.setSnapToTicks(true);


        //MA slider
        Label maOrder = new Label("MA order");
        Slider ma = new Slider(0, MAX_ORDER, 0);

        VBox maBox = new VBox(maOrder, ma);
        maBox.setSpacing(10);

        grid.add(maBox, 0, 1);
        ma.setShowTickMarks(true);
        ma.setShowTickLabels(true);
        ma.setMajorTickUnit(1);
        ma.setMinorTickCount(0);
        ma.setSnapToTicks(true);


        Label currentFormula = new Label();

        ar.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (Math.abs((Double) newValue - 0) < 1) {

                    currentFormula.setText("");
                    ma.setDisable(false);
                } else {

                    String latexString = "y_t = ";

                    for (int i = 1; i <= newValue.intValue(); i++) {
                        latexString += "c_" + i + " * " + "y_t-" + i + " + ";
                    }

                    latexString += "a_t";

                    currentFormula.setText(latexString);
                    ma.setDisable(true);
                }
            }
        });

        ma.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (Math.abs((Double) newValue - 0) < 1){
                    ar.setDisable(false);
                    currentFormula.setText("");
                }
                else{
                    ar.setDisable(true);

                    String latexString = "y_t = E(y) + a_t - ";

                    for (int i = 1; i <= newValue.intValue(); i++) {
                        latexString += "c_" + i + " * " + "a_t-" + i;
                        if(i != newValue.intValue()) latexString += " - ";
                    }

                    currentFormula.setText(latexString);
                    ar.setDisable(true);
                }
            }
        });

        //Immutable dataset
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


        //line chart
        XYChart.Series<Integer, Double> series = new XYChart.Series();
        series.setName("Expected");
        ObservableList<XYChart.Data<Integer, Double>> observableList = getChartData(data.getDatasetValues());
        series.setData(observableList);
        updateSeriesOnListChangeListener(data.getDatasetValues(), series);
        LineChart line = lineChart(series, "Data");

        currentFormula.setMaxWidth(line.getMaxWidth());
        currentFormula.setWrapText(true);

        HBox label = new HBox(currentFormula);
        label.setAlignment(Pos.TOP_CENTER);
        currentFormula.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        grid.add(label,1, 0, 5, 1);
        grid.add(line, 1, 1, 3, 3);

        //start button
        Button start = new Button("Start");
        grid.add(start, 3, 3);
        start.setDisable(true);
        enableButtonWhenDatasetExistsListener(data.getDatasetValues(), start);
        allowOneSeriesUponDatasetChangeListener(data.getDatasetValues(), line);
        start.setTooltip(new Tooltip("If both AR order and MA order are zero, this button does nothing."));

        //predict button
        Button predict = new Button("Predict future values");
        predict.setDisable(true);
        grid.add(predict, 4, 3);

        predict.setOnAction(predictAction());

        start.setOnAction(
                event -> {
                    if (ar.getValue() == 0 && ma.getValue() == 0) return;
                    new Thread(() -> {
                        try {
                            arima = new ARIMA((int) ar.getValue(), (int) ma.getValue(),
                                    DatasetValue.getDoubleList(data.getDatasetValues()));
                            AModel am = arima.getModel();
                            if (am instanceof ARMA) {
                                if (!ARMA.invertibleCheck(arima.getCoeffs())) {
                                    showErrorMessage("Could not compute an invertible MA model. " +
                                            "Using starting values instead.", data);
                                }
                            }
                            predict.setDisable(false);
                            double[] test = arima.testDataset();
                            XYChart.Series calculated = new XYChart.Series();
                            calculated.setName("Calculated");
                            calculated.setData(getChartData(
                                    FXCollections.observableArrayList(DatasetValue.encapsulateDoubleArray(test))));
                            Platform.runLater(() -> {
                                if (line.getData().size() > 1) line.getData().remove(1);
                                line.getData().add(calculated);
                            });
                        } catch (RuntimeException r) {
                            showErrorMessage("Unable to compute the given model:" +
                                    " matrix for the given dataset is singular.", data);
                        }
                    }).run();

                });

        grid.add(table, 0, 2);

        parent.getChildren().add(grid);
    }

    protected static void allowOneSeriesUponDatasetChangeListener(ObservableList<DatasetValue> datasetValues, LineChart line) {
        datasetValues.addListener((ListChangeListener<DatasetValue>) c -> {
            if (line.getData().size() > 1) line.getData().remove(1);
        });
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
                    new Thread(() -> {
                        double[] predictions = arima.computeNextValues(howManyPredictions);
                        ObservableList<DatasetValue> observableList = FXCollections.observableList(
                                DatasetValue.encapsulateDoubleArray(predictions));
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

    public static void showErrorMessage(String message, Data data) {
        Platform.runLater(() -> {
            Stage notInvertible = new Stage();
            notInvertible.initOwner(data.getPrimaryStage());
            notInvertible.initModality(Modality.WINDOW_MODAL);

            Label l = new Label(message);

            l.setPadding(new Insets(30, 30, 30, 30));

            Scene scene = new Scene(l);
            notInvertible.setScene(scene);
            notInvertible.show();
        });
    }
}
