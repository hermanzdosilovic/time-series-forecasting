package hr.fer.zemris.project.forecasting.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Time Series Forecasting");

        Tab data = new Tab("Data");
        data.setClosable(false);
        Pane dataContent = new Pane();
        Data dataTab = new Data();
        dataTab.createUI(dataContent);
        data.setContent(dataContent);

        Tab arima = new Tab("ARIMA");
        arima.setClosable(false);
        Pane arimaContent = new Pane();
        ARIMA arimaTab = new ARIMA();
        arimaTab.createUI(arimaContent);
        arima.setContent(arimaContent);

        Tab neuralNetwork = new Tab("ANN");
        neuralNetwork.setClosable(false);
        Pane neuralContent = new Pane();
        NeuralNetworkUI neuralNetworkTab = new NeuralNetworkUI();
        neuralNetworkTab.createUI(neuralContent);
        neuralNetwork.setContent(neuralContent);

        Tab geneticProgramming = new Tab("GP");
        geneticProgramming.setClosable(false);

        TabPane tabs = new TabPane(data, arima, neuralNetwork, geneticProgramming);
        Scene mainScene = new Scene(tabs);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }


}
