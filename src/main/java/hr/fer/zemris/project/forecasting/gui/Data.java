package hr.fer.zemris.project.forecasting.gui;

import hr.fer.zemris.project.forecasting.util.DataReaderUtil;
import hr.fer.zemris.project.forecasting.util.GraphUtil;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.codefx.libfx.listener.handle.ListenerHandle;
import org.codefx.libfx.listener.handle.ListenerHandles;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class Data {
    //TODO popraviti klikanje na hover
    private ObservableList<DatasetValue> datasetValues;
    private XYChart.Series series;
    private Stage primaryStage;
    private TableView table;
    private static List<ListenerHandle> listenerHandles = new LinkedList<>();
    private static List<MyListChangeListener> myListChangeListeners = new LinkedList<>();

    public static final String DEFAULT_DATASET = "datasets/exchange-rate-twi-may-1970-aug-1.csv";

    public final static double INDEX_SIZE = 0.3;

    public static ObservableList<DatasetValue> getList(String path) {
        try {
            return FXCollections.observableArrayList(DatasetValue.
                    encapsulateDoubleArray(DataReaderUtil.readDataset(path)));
        } catch (IOException e) {
            return null;
        }
    }

    private static ObservableList<DatasetValue> getList(String path, String delimiter, Integer index) {
        try {
            return FXCollections.observableArrayList(DatasetValue.
                    encapsulateDoubleArray(DataReaderUtil.readDataset(path, index - 1, delimiter)));
        } catch (IOException e) {
            return null;
        }
    }

    public Data(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.series = new XYChart.Series();
        this.table = new TableView();
        series.setName("Sample");
        datasetValues = FXCollections.observableArrayList();
        series.setData(DatasetValue.getChartData(datasetValues));
        updateSeriesOnListChangeListener(datasetValues, series);
    }

    public ObservableList<DatasetValue> getDatasetValues() {
        return datasetValues;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public final static double MAX_TABLE_WIDTH = 150;


    public void createUI(Pane parent) {

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(30, 30, 30, 30));


        //Load dataset button
        Button loadDataset = new Button("Load data");
        loadDataset.setOnAction(loadAction());

        //Save dataset button
        Button saveDataset = new Button("Save data");
        saveDataset.setDisable(true);

        HBox upperBox = new HBox(loadDataset, saveDataset);

        upperBox.setSpacing(10);

        grid.add(upperBox, 0, 0);

        //Editable dataset
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(datasetValues);
        table.setPrefWidth(MAX_TABLE_WIDTH);
        TableColumn<DatasetValue, Double> values = new TableColumn("Value");
        values.setSortable(false);
        values.setEditable(true);
        values.setCellValueFactory(new PropertyValueFactory<>("value"));

        values.setCellFactory(TextFieldTableCell.forTableColumn(new MyDoubleStringConverter()));
        values.setOnEditCommit((e) ->
                e.getTableView().getItems().set(e.getTablePosition().getRow(),
                        new DatasetValue(e.getTablePosition().getRow(), e.getNewValue())));

        TableColumn<DatasetValue, Integer> indices = new TableColumn<>("Index");
        indices.setSortable(false);
        indices.setEditable(false);

        indices.setCellValueFactory(new PropertyValueFactory<>("index"));

        table.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.PLUS) {
                addRow();
            } else if (event.getCode() == KeyCode.MINUS) {
                removeRow();
            }
        });


        table.getColumns().add(indices);
        table.getColumns().add(values);

        indices.prefWidthProperty().bind(table.widthProperty().multiply(INDEX_SIZE));
        values.prefWidthProperty().bind(table.widthProperty().multiply(1 - INDEX_SIZE));

        indices.setResizable(false);
        values.setResizable(false);

        grid.add(table, 0, 1);

        //normalize button
        Button normalize = new Button("Normalize");
        normalize.setOnAction(normalizeAction());
        normalize.setDisable(true);
        enableButtonWhenDatasetExistsListener(datasetValues, normalize);

        //clear dataset button
        Button clearDataset = new Button("Clear dataset");
        clearDataset.setOnAction((l) -> datasetValues.clear());
        clearDataset.setDisable(true);
        enableButtonWhenDatasetExistsListener(datasetValues, clearDataset);

        HBox downBox = new HBox(normalize, clearDataset);
        downBox.setSpacing(10);
        grid.add(downBox, 0, 2);

        //line chart
        LineChart line = lineChart(series, "Data");

        grid.add(line, 1, 0, 3, 3);


        parent.getChildren().add(grid);
    }

    private EventHandler<ActionEvent> loadAction() {
        return event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.setTitle("Load dataset...");

            File f = fileChooser.showOpenDialog(primaryStage);

            if (f != null) {

                Stage delimiterStage = new Stage();

                delimiterStage.initOwner(primaryStage);
                delimiterStage.initModality(Modality.WINDOW_MODAL);

                Label delLabel = new Label("Delimiter:");
                delLabel.requestFocus();

                TextField delimiterField = new TextField();
                delimiterField.setPromptText("Enter the delimiter used in the file");
                delimiterField.setTooltip(new Tooltip("If nothing is entered, the default delimiter is used."));

                Label colLabel = new Label("Column index:");
                TextField columnNumber = new TextField();
                columnNumber.setPromptText("Enter the index of the column where the data is located");
                columnNumber.setTooltip(new Tooltip("If nothing is entered, the last column is used."));

                Button ok = new Button("OK");

                ok.setOnAction(event1 -> {
                    for (ListenerHandle lh : listenerHandles) lh.detach();
                    datasetValues.clear();

                    try {
                        String delimiter = delimiterField.getText();
                        if (delimiter.equals("")) delimiter = ",";
                        int indexOfColumn = Integer.parseInt(columnNumber.getText());
                        delimiterStage.hide();
                        datasetValues.addAll(getList(f.getPath(), delimiter, indexOfColumn));
                    } catch (NumberFormatException e) {
                        delimiterStage.hide();
                        datasetValues.addAll(getList(f.getPath()));
                    }

                    for (ListenerHandle lh : listenerHandles) lh.attach();
                    for (MyListChangeListener lcl : myListChangeListeners) lcl.setDatasetValuesOnSeries();

                });

                VBox box = new VBox(delLabel, delimiterField, colLabel, columnNumber, ok);
                box.setPadding(new Insets(20, 20, 20, 20));
                box.setSpacing(10);
                box.setAlignment(Pos.CENTER);
                Scene delimScene = new Scene(box);
                delimiterStage.setScene(delimScene);

                delimiterStage.show();
            }
        };

    }

    private void addRow() {

        // get current position
        int row = table.getSelectionModel().getSelectedIndex();
        TableColumn selectedColumn = table.getFocusModel().getFocusedCell().getTableColumn();

        // clear current selection
        table.getSelectionModel().clearSelection();

        // create new record and add it to the model
        DatasetValue data = new DatasetValue(row + 2, 0.0);
        if (row < table.getItems().size() - 1) {
            table.getItems().add(row + 1, data);
            for (int i = row + 2; i < datasetValues.size(); i++) {
                datasetValues.get(i).setIndex(i + 1);
            }
        } else table.getItems().add(data);

        table.getSelectionModel().select(row + 1, selectedColumn);

        //edit cell
        table.getFocusModel().focus(row + 1, selectedColumn);
        table.edit(row + 1, selectedColumn);

        // scroll to new row
        table.scrollTo(row + 1);

    }


    private void removeRow() {
        if (table.getItems().size() == 0) return;
        int row = table.getSelectionModel().getSelectedIndex();
        datasetValues.remove(row);
        for (int i = row; i < datasetValues.size(); i++)
            datasetValues.get(i).setIndex(i + 1);
    }

    public static LineChart<Number, Number> lineChart(XYChart.Series series, String lineChartName) {
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setMinorTickVisible(false);

        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Sample Number");
        yAxis.setLabel("Sample Value");
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(lineChartName);
        lineChart.getData().add(series);
        lineChart.setCreateSymbols(true);
        lineChart.setMaxSize(GraphUtil.DEFAULT_WIDTH, GraphUtil.DEFAULT_HEIGHT);
        lineChart.setAnimated(false);
        return lineChart;
    }

    public static LineChart<Number, Number> lineChart(String lineChartName) {
        return lineChart(lineChartName, GraphUtil.DEFAULT_WIDTH, GraphUtil.DEFAULT_HEIGHT);
    }

    public static LineChart<Number, Number> lineChart(String lineChartName, int width, int height) {
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setMinorTickVisible(false);

        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Sample Number");
        yAxis.setLabel("Sample Value");
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(lineChartName);
        lineChart.setCreateSymbols(true);
        lineChart.setMaxSize(width, height);
        lineChart.setAnimated(false);
        return lineChart;
    }

    public static LineChart<Number, Number> mseLineChart(String lineChartName) {
        return mseLineChart(lineChartName, GraphUtil.MSE_DEFAULT_WIDTH, GraphUtil.MSE_DEFAULT_HEIGHT);
    }

    public static LineChart<Number, Number> mseLineChart(String lineChartName, int width, int height) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Iteration");
        xAxis.setForceZeroInRange(false);
        yAxis.setLabel("Mse value");
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(lineChartName);
        lineChart.setCreateSymbols(false);
        lineChart.setMaxSize(width, height);
        lineChart.setAnimated(false);
        return lineChart;
    }

    public static void updateSeriesOnListChangeListener(ObservableList<DatasetValue> datasetValues,
                                                        XYChart.Series<Integer, Double> series) {
        MyListChangeListener lcl = new MyListChangeListener(datasetValues, series);

        myListChangeListeners.add(lcl);

        ListenerHandle handle = ListenerHandles.createAttached(datasetValues, lcl);
        listenerHandles.add(handle);
    }

    public static void enableButtonWhenDatasetExistsListener(ObservableList<DatasetValue> datasetValues, Button b) {
        datasetValues.addListener((ListChangeListener<DatasetValue>) c -> {
            while (c.next()) {
                if (datasetValues.size() == 0) b.setDisable(true);
                else b.setDisable(false);
            }
        });
    }

    private EventHandler<ActionEvent> normalizeAction() {
        return event -> {
            Stage normalizeStage = new Stage();
            normalizeStage.setTitle("Normalize!");
            normalizeStage.initOwner(primaryStage);
            normalizeStage.initModality(Modality.WINDOW_MODAL);

            Label labelFrom = new Label("From:");
            TextField from = new TextField();


            Label labelTo = new Label("To:");
            TextField to = new TextField();

            GridPane pane = new GridPane();
            pane.add(labelFrom, 0, 0);
            pane.add(labelTo, 0, 1);
            pane.add(from, 1, 0);
            pane.add(to, 1, 1);

            pane.setVgap(10);
            pane.setHgap(10);

            Label wrongInput = new Label("Invalid input");
            wrongInput.setTextFill(Color.RED);
            wrongInput.setVisible(false);

            Button ok = new Button("OK");
            ok.setOnAction(e -> {
                try {
                    double lowerBound = Double.parseDouble(from.getText());
                    double upperBound = Double.parseDouble(to.getText());
                    new Thread(() -> {
                        normalize(lowerBound, upperBound);
                    }).run();
                    normalizeStage.hide();
                } catch (IllegalArgumentException nfe) {
                    wrongInput.setVisible(true);
                    return;
                }
            });

            HBox okBox = new HBox(ok);
            okBox.setAlignment(Pos.CENTER);

            HBox invalidBox = new HBox(wrongInput);
            invalidBox.setAlignment(Pos.CENTER);

            VBox normalizeBox = new VBox(pane, invalidBox, okBox);
            normalizeBox.setSpacing(15);
            normalizeBox.setPadding(new Insets(20, 20, 20, 20));

            Scene normalizeScene = new Scene(normalizeBox);
            normalizeStage.setScene(normalizeScene);
            normalizeStage.show();
        };
    }

    private void normalize(double from, double to) {
        if (from > to) throw new IllegalArgumentException();
        if (from == to) {
            for (int i = 0; i < datasetValues.size(); i++) {
                datasetValues.set(i, new DatasetValue(i, from));
            }
            return;
        }
        double min = Collections.min(datasetValues).getValue();
        double max = Collections.max(datasetValues).getValue();
        for (int i = 0; i < datasetValues.size(); i++) {
            double x = datasetValues.get(i).getValue();
            datasetValues.set(i, new DatasetValue(i, from + (x - min) * (to - from) / (max - min)));
        }
    }

    private static class MyDoubleStringConverter extends StringConverter<Double> {

        @Override
        public String toString(Double object) {
            if (object == null) return "";
            return Double.toString(object);
        }

        @Override
        public Double fromString(String string) {
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    private static class MyListChangeListener implements ListChangeListener<DatasetValue> {

        private ObservableList<DatasetValue> datasetValues;
        private XYChart.Series series;

        public MyListChangeListener(ObservableList<DatasetValue> datasetValues, XYChart.Series series) {
            this.datasetValues = datasetValues;
            this.series = series;
        }

        @Override
        public void onChanged(Change<? extends DatasetValue> c) {
            while (c.next()) {
                if (c.wasReplaced()) {
                    for (int i = c.getFrom(); i < c.getTo(); i++) {
                        XYChart.Data<Integer, Double> nextAddition = new XYChart.Data<>(i, datasetValues.get(i).getValue());
                        nextAddition.setNode(new DatasetValue.HoveredThresholdNode(
                                nextAddition.getXValue(), nextAddition.getYValue()));
                        series.getData().set(i, nextAddition);
                    }
                } else if (c.wasAdded()) {
                    for (int i = c.getFrom(); i < c.getTo(); i++) {
                        XYChart.Data<Integer, Double> nextAddition = new XYChart.Data<>(i, datasetValues.get(i).getValue());
                        nextAddition.setNode(new DatasetValue.HoveredThresholdNode(
                                nextAddition.getXValue(), nextAddition.getYValue()));
                        if (i < series.getData().size()) {
                            series.getData().add(i, nextAddition);
                            for (int j = i + 1; j < series.getData().size(); j++) {
                                XYChart.Data<Integer, Double> after = new XYChart.Data<>(j, datasetValues.get(j).getValue());
                                after.setNode(new DatasetValue.HoveredThresholdNode(
                                        after.getXValue(), after.getYValue()));
                                series.getData().set(j, after);
                            }
                        } else series.getData().add(nextAddition);
                    }
                } else if (c.wasRemoved()) {
                    series.getData().remove(c.getFrom());
                    for (int j = c.getFrom(); j < series.getData().size(); j++) {
                        XYChart.Data<Integer, Double> after = new XYChart.Data<>(j, datasetValues.get(j).getValue());
                        after.setNode(new DatasetValue.HoveredThresholdNode(
                                after.getXValue(), after.getYValue()));
                        series.getData().set(j, after);
                    }
                }
            }
        }

        public void setDatasetValuesOnSeries() {
            series.setData(DatasetValue.getChartData(datasetValues));
        }
    }

}
