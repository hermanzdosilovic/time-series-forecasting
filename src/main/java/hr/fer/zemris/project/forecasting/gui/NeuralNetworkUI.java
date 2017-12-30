package hr.fer.zemris.project.forecasting.gui;

import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.IMetaheuristic;
import com.dosilovic.hermanzvonimir.ecfjava.util.RealVector;
import hr.fer.zemris.project.forecasting.nn.INeuralNetwork;
import hr.fer.zemris.project.forecasting.util.ArraysUtil;
import javafx.collections.FXCollections;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.spi.NumberFormatProvider;
import java.util.Arrays;

import static hr.fer.zemris.project.forecasting.gui.Data.MAX_TABLE_WIDTH;
import static hr.fer.zemris.project.forecasting.gui.Data.lineChart;
import static hr.fer.zemris.project.forecasting.gui.Data.updateSeriesOnListChangeListener;

public class NeuralNetworkUI {

    private Data data;
    private int[] architecture;
    private INeuralNetwork nn;
    private IMetaheuristic<RealVector> metaheuristic;

    public NeuralNetworkUI(Data data){
        this.data = data;
    }

    public void createUI(Pane parent){
        GridPane grid = new GridPane();

        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(30, 30, 30, 30));
        //TODO dodati izbor aktivacijske funkcije i splitanja dataseta
        //choose neural network button
        ComboBox<String> chooseNetwork = new ComboBox<>(FXCollections.observableArrayList("<none>", "TDNN", "Elman ANN"));
        chooseNetwork.getSelectionModel().select(0);

        chooseNetwork.setOnAction(changeArchitectureAction(chooseNetwork));
        //change architecture button
        Button changeArch = new Button("Change architecture");
        changeArch.setOnAction(changeArchitectureAction(chooseNetwork));

        HBox neural = new HBox(chooseNetwork, changeArch);

        //choose algorithm
        ComboBox<String> chooseAlgorithm = new ComboBox<>(FXCollections.observableArrayList(
                "<none>", "Genetic", "OSGA", "SA", "DE", "PSO"));
        chooseAlgorithm.getSelectionModel().select(0);

        chooseAlgorithm.setOnAction(AlgorithmsGUI.chooseAlgorithmAction(chooseAlgorithm, metaheuristic, nn, data.getPrimaryStage()));
        //change algorithm parameters
        Button changeParams = new Button("Change parameters");
        changeParams.setOnAction(AlgorithmsGUI.chooseAlgorithmAction(chooseAlgorithm, metaheuristic, nn, data.getPrimaryStage()));
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

        //Button predict
        Button predict = new Button("Predict future values");

        VBox rightSide = new VBox();

        //line chart
        XYChart.Series<Integer, Double> series = new XYChart.Series();
        series.setName("Expected");
        ObservableList<XYChart.Data<Integer, Double>> observableList = DatasetValue.getChartData(data.getDatasetValues());
        series.setData(observableList);
        updateSeriesOnListChangeListener(data.getDatasetValues(), series);
        LineChart line = lineChart(series, "Data");

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
            if(arch.getSelectionModel().getSelectedItem().equals("<none>")) return;
            System.out.println(arch.getValue());
            Stage changeArch = new Stage();
            changeArch.initOwner(data.getPrimaryStage());
            changeArch.initModality(Modality.WINDOW_MODAL);
            changeArch.setTitle("Change!");

//            Label numberOfNodes = new Label("Enter the number of nodes wanted in each layer respectively.");

            Label inputLayer = new Label("Input layer:");
            TextField input = new TextField();

            Label hiddenLayers = new Label("Hidden layers:");
            TextField hidden = new TextField();
            hidden.setTooltip(new Tooltip("Split number of nodes for each hidden layer with a comma"));

            Label outputLayer = new Label("Output layer:");
            TextField output = new TextField();

            if(architecture != null){
                input.setText(architecture[0] + "");
                output.setText(architecture[architecture.length - 1] + "");
                StringBuilder sb = new StringBuilder();
                for(int i = 1; i < architecture.length - 1; i++){
                    sb.append(architecture[i]);
                    if(i != architecture.length - 2) sb.append(", ");
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
            ok.setOnAction((e) ->{
                try{
                    String[] hiddens = hidden.getText().split(",");
                    architecture = new int[hiddens.length + 2];
                    architecture[0] = Integer.parseInt(input.getText());
                    architecture[architecture.length - 1] = Integer.parseInt(output.getText());
                    for(int i = 1; i < architecture.length - 1; i++){
                        architecture[i] = Integer.parseInt(hiddens[i - 1]);
                    }
                    System.out.println(Arrays.toString(architecture));
                    changeArch.hide();
                }catch(NumberFormatException nfe){
                    invalidInput.setVisible(true);
                }
            });

            GridPane gridPane = new GridPane();
            gridPane.setPadding(new Insets(20, 20, 20, 20));
            gridPane.setHgap(10);
            gridPane.setVgap(10);

            gridPane.add(inputLayer, 0, 0);
            gridPane.add(input, 1 , 0);
            gridPane.add(hiddenLayers, 0, 1);
            gridPane.add(hidden, 1, 1 );
            gridPane.add(outputLayer, 0, 2);
            gridPane.add(output, 1, 2);
            gridPane.add(invalidBox, 0, 3, 2, 1);
            gridPane.add(okBox, 0, 4, 2, 1);

            Scene gridScene = new Scene(gridPane);
            changeArch.setScene(gridScene);
            changeArch.show();
        };
    }

}
