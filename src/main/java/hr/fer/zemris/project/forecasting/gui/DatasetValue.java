package hr.fer.zemris.project.forecasting.gui;

import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import hr.fer.zemris.project.forecasting.nn.util.NeuralNetworkUtil;
import hr.fer.zemris.project.forecasting.util.ArraysUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class DatasetValue implements Comparable<DatasetValue>{

    private double value;

    public DatasetValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString(){
        return Double.toString(value);
    }

    public static List<DatasetValue> encapsulateDoubleArray(double[] dataset){
        List<DatasetValue> tmp = new ArrayList<>(dataset.length);
        for(int i = 0; i < dataset.length; i++){
            tmp.add(new DatasetValue(dataset[i]));
        }
        return tmp;
    }

    public static double[] getDoubleArray(List<DatasetValue> datasetValues){
        double[] tmp = new double[datasetValues.size()];
        for(int i = 0; i < tmp.length; i++){
            tmp[i] = datasetValues.get(i).getValue();
        }
        return tmp;
    }

    public static List<Double> getDoubleList(List<DatasetValue> datasetValues){
        return ArraysUtil.toList(getDoubleArray(datasetValues));
    }

    public static ObservableList<XYChart.Data<Integer, Double>> getChartData (ObservableList<DatasetValue> dataset){
        ObservableList<XYChart.Data<Integer, Double>> observableList = FXCollections.observableArrayList();
        for(int i = 0; i < dataset.size(); i++){
            observableList.add(new XYChart.Data<>(i + 1, dataset.get(i).getValue()));
            observableList.get(i).setNode(new HoveredThresholdNode(
                    observableList.get(i).getXValue(), observableList.get(i).getYValue()
            ));
        }
        return observableList;
    }

    public static List<DatasetEntry> getTrainingData(List<DatasetValue> datasetValues, int inputSize, int outputSize){
        return NeuralNetworkUtil.createTDNNDateset(getDoubleArray(datasetValues), inputSize, outputSize);
    }

//    @Override
//    public boolean equals(Object obj) {
//        if(obj instanceof DatasetValue){
//            DatasetValue d = (DatasetValue) obj;
//            return Double.compare(d.getValue(), value) == 0;
//        }
//        return false;
//    }

    @Override
    public int compareTo(DatasetValue o) {
        return Double.compare(value, o.getValue());
    }

    public static class HoveredThresholdNode extends StackPane{
        HoveredThresholdNode(int priorValue, double value) {
            setPrefSize(7, 7);
            setOpacity(0);

            final HBox label = createDataThresholdLabel(priorValue, value);

            setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    setOpacity(1);
                    getChildren().setAll(label);
                    setCursor(Cursor.NONE);
                    toFront();
                }
            });
            setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    setOpacity(0);
                    getChildren().clear();
                    setCursor(Cursor.CROSSHAIR);
                }
            });
        }
        private HBox createDataThresholdLabel(int priorValue, double value) {
            Text x = new Text(priorValue + "");
            Text y = new Text(String.format("%.2f", value));

            x.setFill(Color.RED);
            x.setFont(Font.font("Calibri", FontWeight.BOLD, 12));
            y.setFill(Color.BLUE);
            y.setFont(Font.font("Calibri", FontWeight.BOLD, 12));

            HBox b = new HBox(x, y);
            b.setSpacing(2);
            b.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
            return b;
        }
    }
}
