package hr.fer.zemris.project.forecasting.gui;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import static hr.fer.zemris.project.forecasting.gui.Data.MAX_TABLE_WIDTH;
import static hr.fer.zemris.project.forecasting.gui.Data.lineChart;

public class NeuralNetworkUI {

    private Data data;
    public NeuralNetworkUI(Data data){
        this.data = data;
    }

    public void createUI(Pane parent){
        GridPane grid = new GridPane();

        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(30, 30, 30, 30));

        //choose neural network button
        ComboBox<String> chooseNetwork = new ComboBox<>(FXCollections.observableArrayList("<none>", "TDNN", "Elman ANN"));
        chooseNetwork.getSelectionModel().select(0);

        //change architecture button
        Button changeArch = new Button("Change architecture");

        HBox neural = new HBox(chooseNetwork, changeArch);

        //choose algorithm
        ComboBox<String> chooseAlgorithm = new ComboBox<>(FXCollections.observableArrayList("<none>", "TDNN", "Elman ANN"));
        chooseAlgorithm.getSelectionModel().select(0);

        //change algorithm parameters
        Button changeParams = new Button("Change parameters");

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
        Button start = new Button("Start");

        //Button predict
        Button predict = new Button("Predict future values");

        VBox rightSide = new VBox();

        //line chart
        XYChart.Series series = DatasetValue.getChartData(data.getDatasetValues());
        LineChart line = lineChart(series);
        Data.addChangeListener(data.getDatasetValues(), series);

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
}
