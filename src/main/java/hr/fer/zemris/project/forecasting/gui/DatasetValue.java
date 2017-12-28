package hr.fer.zemris.project.forecasting.gui;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;

public class DatasetValue {

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

    public static XYChart.Series getChartData (ObservableList<DatasetValue> dataset){
        ObservableList<XYChart.Data<Integer, Double>> observableList = FXCollections.observableArrayList();
        for(int i = 0; i < dataset.size(); i++){
            observableList.add(new XYChart.Data<>(i, dataset.get(i).getValue()));
        }
        XYChart.Series series = new XYChart.Series();
        series.setName("Expected Value");
        series.setData(observableList);
        return series;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof DatasetValue){
            DatasetValue d = (DatasetValue) obj;
            return Double.compare(d.getValue(), value) == 0;
        }
        return false;
    }
}
