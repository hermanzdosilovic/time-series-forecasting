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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

import static hr.fer.zemris.project.forecasting.gui.Data.*;
import static hr.fer.zemris.project.forecasting.gui.DatasetValue.getChartData;

public class ARIMAUI {
    private Data data;
    private ARIMA arima;

    //TODO pokazati kako ARMA pogadja nakon što se pokrene
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

        ar.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(Math.abs((Double) newValue - 0) < 1) ma.setDisable(false);
                else ma.setDisable(true);
            }
        });

        ma.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(Math.abs((Double) newValue - 0) < 1) ar.setDisable(false);
                else ar.setDisable(true);
            }
        });

        //Immutable dataset
        TableView table = new TableView();

        table.setPrefWidth(MAX_TABLE_WIDTH);
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(data.getDatasetValues());

        TableColumn<DatasetValue, Double> values = new TableColumn("Data Set Values");
        values.setSortable(false);
        values.setEditable(true);

        values.setCellValueFactory(new PropertyValueFactory<>("value"));

        table.getColumns().add(values);

        //line chart
        XYChart.Series<Integer, Double> series = new XYChart.Series();
        series.setName("Expected");
        ObservableList<XYChart.Data<Integer, Double>> observableList = getChartData(data.getDatasetValues());
        series.setData(observableList);
        updateSeriesOnListChangeListener(data.getDatasetValues(), series);
        LineChart line = lineChart(series, "Data");
        grid.add(line, 1, 0, 3, 3);

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

        //TODO promijeniti seriju koju se dobije s novim datasetom
        start.setOnAction(
                event -> {
                    if(ar.getValue() == 0 && ma.getValue() == 0) return;
                    new Thread(() -> {
                            arima = new ARIMA((int) ar.getValue(), (int) ma.getValue(),
                                    DatasetValue.getDoubleList(data.getDatasetValues()));
                            AModel am = arima.getModel();
                            if(am instanceof  ARMA) {
                                if (!ARMA.invertibleCheck(arima.getCoeffs())) {
                                    Platform.runLater(() -> {
                                        Stage notInvertible = new Stage();
                                        notInvertible.initOwner(data.getPrimaryStage());
                                        notInvertible.initModality(Modality.WINDOW_MODAL);

                                        Label l = new Label("Could not compute an invertible model. Using " +
                                                "starting values instead.");

                                        l.setPadding(new Insets(30, 30, 30, 30));

                                        Scene scene = new Scene(l);
                                        notInvertible.setScene(scene);
                                        notInvertible.show();
                                    });
                                }
                            }

                        predict.setDisable(false);
                    }).run();
                    if(line.getData().size() > 1) line.getData().remove(1);
                });

        grid.add(table, 0, 2);

        parent.getChildren().add(grid);
    }

    private static void allowOneSeriesUponDatasetChangeListener(ObservableList<DatasetValue> datasetValues, LineChart line) {
        datasetValues.addListener((ListChangeListener<DatasetValue>) c -> {
            if(line.getData().size() > 1) line.getData().remove(1);
        });
    }

    private EventHandler<ActionEvent> predictAction(){
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

            ok.setOnAction((e) ->{
                try{
                    int howManyPredictions = Integer.parseInt(predicts.getText());
                    new Thread(() -> {
                        double[] predictions = arima.computeNextValues(howManyPredictions);
                        ObservableList<DatasetValue> observableList = FXCollections.observableList(
                                DatasetValue.encapsulateDoubleArray(predictions));
                        Platform.runLater(() ->{
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
                }catch(Exception e1){
                    invalidInput.setVisible(true);
                }
            });
        };
    }

}
