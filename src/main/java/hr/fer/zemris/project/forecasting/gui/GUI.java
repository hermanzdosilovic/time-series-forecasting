package hr.fer.zemris.project.forecasting.gui;

import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GUI extends Application {

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Time Series Forecasting");
        primaryStage.setResizable(false);

        Tab data = new Tab("Data");
        data.setClosable(false);
        Pane dataContent = new Pane();
        Data dataTab = new Data(primaryStage);
        dataTab.createUI(dataContent);
        data.setContent(dataContent);

        Tab arima = new Tab("ARIMA");
        arima.setClosable(false);
        Pane arimaContent = new Pane();
        ARIMAUI arimaTab = new ARIMAUI(dataTab);
        arimaTab.createUI(arimaContent);
        arima.setContent(arimaContent);
        arima.setDisable(true);

        Tab neuralNetwork = new Tab("ANN");
        neuralNetwork.setClosable(false);
        Pane neuralContent = new Pane();
        NeuralNetworkUI neuralNetworkTab = new NeuralNetworkUI(dataTab);
        neuralNetworkTab.createUI(neuralContent);
        neuralNetwork.setContent(neuralContent);
        neuralNetwork.setDisable(true);

        Tab geneticProgramming = new Tab("GP");
        geneticProgramming.setClosable(false);
        geneticProgramming.setDisable(true);

        ObservableList dataset = dataTab.getDatasetValues();
        dataset.addListener((ListChangeListener) c -> {
            while(c.next()){
                if(dataset.size() == 0){
                    arima.setDisable(true);
                    neuralNetwork.setDisable(true);
                    geneticProgramming.setDisable(true);
                }
                else{
                    arima.setDisable(false);
                    neuralNetwork.setDisable(false);
                    geneticProgramming.setDisable(false);
                }
            }
        });

        TabPane tabs = new TabPane(data, arima, neuralNetwork, geneticProgramming);
        Scene mainScene = new Scene(tabs);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }


}
