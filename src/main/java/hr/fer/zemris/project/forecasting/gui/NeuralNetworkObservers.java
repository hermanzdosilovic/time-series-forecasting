package hr.fer.zemris.project.forecasting.gui;

import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.IMetaheuristic;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.util.IObserver;
import com.dosilovic.hermanzvonimir.ecfjava.models.solutions.ISolution;
import com.dosilovic.hermanzvonimir.ecfjava.models.solutions.SimpleSolution;
import com.dosilovic.hermanzvonimir.ecfjava.models.solutions.vector.RealVector;
import com.dosilovic.hermanzvonimir.ecfjava.neural.ElmanNN;
import com.dosilovic.hermanzvonimir.ecfjava.neural.FeedForwardANN;
import com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork;
import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;

import hr.fer.zemris.project.forecasting.gui.DatasetValue.HoveredThresholdNode;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NeuralNetworkObservers {
    public static class GraphObserver{
        public static final int MSE_GRAPH_SIZE = 300;
        public static final long PERIOD = 1500L;

        private INeuralNetwork nn;
        private long iteration;
        private volatile ObservableList<XYChart.Data<Integer, Double>> mseObservableList;
        private volatile ObservableList<XYChart.Data<Integer, Double>> observableList;
        private long lastPlottingTime = System.currentTimeMillis();
        private volatile List<XYChart.Data<Integer, Double>> mseList = new CopyOnWriteArrayList<>();
        private volatile List<XYChart.Data<Integer, Double>> outputList = new CopyOnWriteArrayList<>();
        private double[] lastSeenWeights;
        private List<DatasetEntry> dataset;
        private volatile XYChart.Series<Integer, Double> series;
        private volatile XYChart.Series<Integer, Double> mseSeries;
        private volatile LineChart line;
        private volatile LineChart mseChart;

        public GraphObserver(INeuralNetwork nn, List<DatasetEntry> dataset, XYChart.Series<Integer, Double> series,
                             XYChart.Series<Integer, Double> mseSeries, LineChart line, LineChart mseChart) {
            this.nn = nn instanceof ElmanNN ? new ElmanNN(nn.getArchitecture(), nn.getLayerActivations()) :
                    new FeedForwardANN(nn.getArchitecture(), nn.getLayerActivations());

            for (int i = 0; i < dataset.size()+nn.getInputSize(); i++) {
                outputList.add(new XYChart.Data<>(i + 1, 0.));
//                outputList.get(i).setNode(new DatasetValue.HoveredThresholdNode(0, 0.));
            }

            this.dataset = dataset;
            this.mseSeries = mseSeries;
            this.series = series;
            this.mseChart = mseChart;
            this.line = line;
        }

        public void update(ISolution<double[]> solution) {
            ++iteration;

            mseList.add(new XYChart.Data<>((int) iteration % MSE_GRAPH_SIZE, solution.getFitness()));
            if (mseList.size() > MSE_GRAPH_SIZE) {
                int fromIndex = mseList.size() - MSE_GRAPH_SIZE;
                int toIndex = mseList.size();
                mseList = mseList.subList(fromIndex, toIndex);
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPlottingTime < PERIOD || Arrays.equals(lastSeenWeights, solution.getRepresentative())) {
                return;
            }
            lastPlottingTime = System.currentTimeMillis();
            lastSeenWeights = solution.getRepresentative();
            if (series == null) {
                series = new XYChart.Series();
                series.setName("Forecast");

            }
            if (mseSeries == null) {
                mseSeries = new XYChart.Series();
                mseSeries.setName("mse");
            }
            double[] weights = solution.getRepresentative();
            nn.setWeights(weights);

            for(int i = 0; i<nn.getInputSize(); ++i){
                outputList.get(i).setYValue(dataset.get(0).getInput()[i]);
                outputList.get(i).setXValue(i + 1);
                HoveredThresholdNode node = new HoveredThresholdNode(i+1,
                        (int)dataset.get(0).getInput()[i]);
                outputList.get(i).setNode(node);
            }
            int offset = nn.getInputSize();
            for (int i = 0; i < dataset.size(); i++) {
                double[] forecast = nn.forward(dataset.get(i).getInput());
                outputList.get(i).setYValue(forecast[0]);
                outputList.get(i).setXValue(offset + 1);
                HoveredThresholdNode node =  new HoveredThresholdNode(offset+1,
                        forecast[0]);
                node.setValue(forecast[0]);
                node.setPriorValue(offset + 1);
                outputList.get(i).setNode(node);
                offset++;
            }

            Runnable plot = new Runnable() {
                @Override
                public void run() {
                    if (line.getData().size() == 1) {
                        line.getData().add(series);
                    }
                    if (mseChart.getData().size() == 0) {
                        mseChart.getData().add(mseSeries);
                        mseObservableList = FXCollections.observableArrayList();
                        mseSeries.setData(mseObservableList);
                    }

                    observableList = FXCollections.observableArrayList();
                    series.setData(observableList);
                    observableList.addAll(outputList);
                    mseObservableList.clear();
                    mseObservableList.addAll(mseList);
                }
            };
            Platform.runLater(plot);
        }
    }

    public static class RealVectorGraphObserver implements IObserver<RealVector> {

        GraphObserver graphObserver;

        RealVectorGraphObserver(INeuralNetwork nn, List<DatasetEntry> dataset, XYChart.Series<Integer, Double> series,
                                XYChart.Series<Integer, Double> mseSeries, LineChart line, LineChart mseChart) {
            graphObserver = new GraphObserver(nn, dataset, series, mseSeries, line, mseChart);
        }

        @Override
        public void update(IMetaheuristic<RealVector> metaheuristic) {
            ISolution<double[]> solution = new SimpleSolution<>(metaheuristic.getBestSolution().getRepresentative().toArray());
            solution.setFitness(metaheuristic.getBestSolution().getFitness());
            graphObserver.update(solution);
        }
    }

    public static class DoubleArrayGraphObserver implements IObserver<double[]> {

        GraphObserver graphObserver;

         public  DoubleArrayGraphObserver(INeuralNetwork nn, List<DatasetEntry> dataset, XYChart.Series<Integer, Double> series,
                                XYChart.Series<Integer, Double> mseSeries, LineChart line, LineChart mseChart) {
            graphObserver = new GraphObserver(nn, dataset, series, mseSeries, line, mseChart);
        }

        @Override
        public void update(IMetaheuristic<double[]> metaheuristic) {
            graphObserver.update(metaheuristic.getBestSolution());
        }
    }
}
