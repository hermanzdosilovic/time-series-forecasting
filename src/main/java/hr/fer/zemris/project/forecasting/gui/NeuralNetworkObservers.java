package hr.fer.zemris.project.forecasting.gui;

import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.IMetaheuristic;
import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.sa.SimpleSA;
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
import javafx.scene.control.Label;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static hr.fer.zemris.project.forecasting.gui.NeuralNetworkObservers.GraphObserver.MAX_MSE;

public class NeuralNetworkObservers {
    public static class GraphObserver {
        public static final int MSE_GRAPH_SIZE = 1000;
        public static final long PERIOD = 1000L;
        public static final double MAX_MSE = 1E8;
        public static final double MAX_PLOTTING_VALUE = 1E15;

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
        private ISolution lastSeenSolution;

        public GraphObserver(INeuralNetwork nn, List<DatasetEntry> dataset, XYChart.Series<Integer, Double> series,
                             XYChart.Series<Integer, Double> mseSeries, LineChart line, LineChart mseChart) {
            this.nn = nn instanceof ElmanNN ? new ElmanNN(nn.getArchitecture(), nn.getLayerActivations()) :
                    new FeedForwardANN(nn.getArchitecture(), nn.getLayerActivations());

            for (int i = 0; i < dataset.size() + nn.getInputSize(); i++) {
                outputList.add(new XYChart.Data<>(i, 0.));
            }

            this.dataset = dataset;
            this.mseSeries = mseSeries;
            this.series = series;
            this.mseChart = mseChart;
            this.line = line;
        }

        public void update(ISolution<double[]> solution, boolean lastCall) {
            if (solution != null) {
                lastSeenSolution = solution;
            } else {
                solution = lastSeenSolution;
            }

            ++iteration;
            boolean lastIteration = iteration == AlgorithmsGUI.maxIterations;

            double fitness = Math.abs(solution.getFitness()) > MAX_MSE ? MAX_MSE : Math.abs(solution.getFitness());
            mseList.add(new XYChart.Data<>((int) iteration, fitness));
            if (mseList.size() > MSE_GRAPH_SIZE) {
                int fromIndex = mseList.size() - MSE_GRAPH_SIZE;
                int toIndex = mseList.size();
                mseList = mseList.subList(fromIndex, toIndex);
            }

            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastPlottingTime < PERIOD || Arrays.equals(lastSeenWeights, solution.getRepresentative()))
                    && !lastIteration && iteration != 0 && !lastCall) {
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
                mseSeries.setName("MSE");
            }
            double[] weights = solution.getRepresentative();
            nn.setParameters(weights);

            for (int i = 0; i < nn.getInputSize(); ++i) {
                outputList.get(i).setYValue(dataset.get(0).getInput()[i]);
                HoveredThresholdNode node = new HoveredThresholdNode(i, (int) dataset.get(0).getInput()[i]);
                outputList.get(i).setNode(node);
            }
            int offset = nn.getInputSize();
            for (int i = 0; i < dataset.size(); i++) {
                double[] forecast = nn.forward(dataset.get(i).getInput());
                forecast[0] = forecast[0] > MAX_PLOTTING_VALUE || Double.isNaN(forecast[0])
                        || Double.isInfinite(forecast[0]) ? MAX_PLOTTING_VALUE : forecast[0];
                forecast[0] = forecast[0] < -MAX_PLOTTING_VALUE || Double.isNaN(forecast[0])
                        || Double.isInfinite(forecast[0]) ? -MAX_PLOTTING_VALUE : forecast[0];
                outputList.get(offset).setYValue(forecast[0]);
                HoveredThresholdNode node = new HoveredThresholdNode(offset,
                        forecast[0]);
                node.setValue(forecast[0]);
                node.setPriorValue(offset);
                outputList.get(offset).setNode(node);
                offset++;
            }

            Runnable plot = () -> {
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
            };

            Platform.runLater(plot);
        }
    }

    public static class RealVectorGraphObserver implements IObserver<RealVector> {

        private GraphObserver graphObserver;

        public RealVectorGraphObserver(INeuralNetwork nn, List<DatasetEntry> dataset, XYChart.Series<Integer, Double> series,
                                       XYChart.Series<Integer, Double> mseSeries, LineChart line, LineChart mseChart) {
            graphObserver = new GraphObserver(nn, dataset, series, mseSeries, line, mseChart);
        }

        @Override
        public void update(IMetaheuristic<RealVector> metaheuristic) {
            if (metaheuristic instanceof SimpleSA) {
                double penalty = metaheuristic.getBestSolution().getPenalty();
                metaheuristic.getBestSolution().setFitness(penalty);
            }
            ISolution<double[]> solution = new SimpleSolution<>(metaheuristic.getBestSolution().getRepresentative().toArray());
            solution.setFitness(metaheuristic.getBestSolution().getFitness());
            graphObserver.update(solution, false);
        }

        public GraphObserver getGraphObserver() {
            return graphObserver;
        }
    }

    public static class DoubleArrayGraphObserver implements IObserver<double[]> {

        protected GraphObserver graphObserver;

        public DoubleArrayGraphObserver(INeuralNetwork nn, List<DatasetEntry> dataset, XYChart.Series<Integer, Double> series,
                                        XYChart.Series<Integer, Double> mseSeries, LineChart line, LineChart mseChart) {
            graphObserver = new GraphObserver(nn, dataset, series, mseSeries, line, mseChart);
        }

        @Override
        public void update(IMetaheuristic<double[]> metaheuristic) {
            graphObserver.update(metaheuristic.getBestSolution(),false);
        }

        public GraphObserver getGraphObserver() {
            return graphObserver;
        }
    }

    public static class DoubleArrayStatusbarObserver implements IObserver<double[]> {

        private StatusBarUpdater statusBarUpdater;

        public DoubleArrayStatusbarObserver(Label statusBar) {
            statusBarUpdater = new StatusBarUpdater(statusBar);
        }

        @Override
        public void update(IMetaheuristic<double[]> metaheuristic) {
            statusBarUpdater.update(metaheuristic.getBestSolution().getFitness());
        }
    }

    public static class RealVectorStatusbarObserver implements IObserver<RealVector> {

        protected StatusBarUpdater statusBarUpdater;

        public RealVectorStatusbarObserver(Label statusBar) {
            statusBarUpdater = new StatusBarUpdater(statusBar);
        }

        @Override
        public void update(IMetaheuristic<RealVector> metaheuristic) {
            if (metaheuristic instanceof SimpleSA) {
                double penalty = metaheuristic.getBestSolution().getPenalty();
                metaheuristic.getBestSolution().setFitness(penalty);
            }
            statusBarUpdater.update(metaheuristic.getBestSolution().getFitness());
        }

    }


    private static class StatusBarUpdater {
        private Label statusBar;
        private long iteration = 0;

        public StatusBarUpdater(Label statusBar) {
            this.statusBar = statusBar;
        }

        public void update(double currentMSE) {
            currentMSE = Math.abs(currentMSE);
            ++iteration;
            String mseText = currentMSE > MAX_MSE ? "too large" : String.format("%7.2f", currentMSE);
            String text = String.format("Iteration: %10d Current mse: %s", iteration, mseText);
            Platform.runLater(() -> statusBar.setText(text));
        }
    }
}
