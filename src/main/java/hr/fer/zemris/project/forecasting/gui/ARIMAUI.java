package hr.fer.zemris.project.forecasting.gui;

import hr.fer.zemris.project.forecasting.models.arma.ARMA;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import static hr.fer.zemris.project.forecasting.gui.Data.*;

public class ARIMAUI {
    private Data data;
    private ARMA arima;

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
        Slider ar = new Slider(0, MAX_ORDER, 0);
        grid.add(ar, 0, 0);
        ar.setShowTickMarks(true);
        ar.setShowTickLabels(true);
        ar.setMajorTickUnit(1);
        ar.setMinorTickCount(0);
        ar.setSnapToTicks(true);


        //MA slider
        Slider ma = new Slider(0, MAX_ORDER, 0);
        grid.add(ma, 0, 1);
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
        ObservableList<XYChart.Data<Integer, Double>> observableList = DatasetValue.getChartData(data.getDatasetValues());
        series.setData(observableList);
        updateSeriesOnListChangeListener(data.getDatasetValues(), series);
        LineChart line = lineChart(series, "Data");
        grid.add(line, 1, 0, 3, 3);

        //start button
        Button start = new Button("Start");
        grid.add(start, 3, 3);
        start.setDisable(true);
        enableButtonWhenDatasetExistsListener(data.getDatasetValues(), start);

        //predict button
        Button predict = new Button("Predict future values");
        predict.setDisable(true);
        grid.add(predict, 4, 3);

        start.setOnAction(
                event -> new Thread(() -> {
                    arima = new ARMA((int) ar.getValue(), (int) ma.getValue(),
                            DatasetValue.getDoubleArray(data.getDatasetValues()), false);
                    predict.setDisable(false);
                }).run());

        grid.add(table, 0, 2);

        parent.getChildren().add(grid);
    }

}
