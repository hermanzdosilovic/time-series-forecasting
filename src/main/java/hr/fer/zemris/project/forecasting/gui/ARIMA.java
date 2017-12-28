package hr.fer.zemris.project.forecasting.gui;

import hr.fer.zemris.project.forecasting.util.GraphUtil;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import static hr.fer.zemris.project.forecasting.gui.Data.*;

public class ARIMA {
    private Data data;

    public ARIMA(Data data){
        this.data = data;
    }

    public static int MAX_ORDER = 5;
    public void createUI(Pane parent){
        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(30, 30, 30, 30));

        //AR slider
        Label label = new Label("AR order");
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
        XYChart.Series series = DatasetValue.getChartData(data.getDatasetValues());
        LineChart line = lineChart(series);
        Data.addChangeListener(data.getDatasetValues(), series);
        grid.add(line, 1, 0, 3, 3);

        //start button
        Button start = new Button("Start");
        grid.add(start, 3, 3);

        //predict button
        Button predict = new Button("Predict future values");
        grid.add(predict, 4, 3);

        grid.add(table, 0, 2);

        parent.getChildren().add(grid);
    }
}
