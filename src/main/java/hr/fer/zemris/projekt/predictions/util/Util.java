package hr.fer.zemris.projekt.predictions.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class Util {

    public static List<Double> parseDataset(Path path, Integer indexOfValue, String delimiter) throws UtilException {
        List<String> dataset;
        try {
            dataset = Files.readAllLines(path);
        } catch (IOException e) {
            throw new UtilException("Error while reading file.");
        }

        List<Double> result = new ArrayList<>();
        for (String line : dataset) {
            String[] data = line.trim().split(delimiter);
            result.add(Double.parseDouble(data[indexOfValue].trim()));
        }
        return result;
    }

    public static List<Double> parseDataset(Path path, Integer indexOfValue) throws UtilException {
        return parseDataset(path, indexOfValue, ",");
    }


    public static void datasetToPNG(Graph graph, Path path, String nameOfFile, Integer width, Integer height) throws UtilException {
        JFreeChart xylineChart = plotDataset(graph);
        File XYChart = new File(String.format("%s/%s.png", path.toAbsolutePath().toString(), nameOfFile));
        try {
            ChartUtilities.saveChartAsPNG(XYChart, xylineChart, width, height);
        } catch (IOException e) {
            throw new UtilException("Error happened while saving image onto disk.");
        }
    }

    public static void datasetToPNG(Graph graph, Path path, String nameOfFile) throws UtilException {
        datasetToPNG(graph, path, nameOfFile, 640, 480);
    }

    public static JFreeChart plotDataset(Graph graph) throws UtilException {
        int size = graph.size();
        if (size < 1) {
            throw new UtilException("There needs to be at least one dataset in given graph.");
        }

        final XYSeriesCollection xyCollection = new XYSeriesCollection();
        for (int i = 0; i < size; i++) {
            final XYSeries series = new XYSeries(graph.getName(i));

            List<Double> pom = graph.getData(i);
            for (int j = 0; j < pom.size(); j++) {
                series.add(j, pom.get(j));
            }
            xyCollection.addSeries(series);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Graph",
                "Time interval",
                "Value",
                xyCollection,
                PlotOrientation.VERTICAL,
                true, true, false);

        final XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        for (int i = 0; i < size; i++) {
            renderer.setSeriesPaint(i, getColor(i));
            renderer.setSeriesStroke(i, new BasicStroke(2f));
            renderer.setSeriesShapesVisible(i, false);
        }
        plot.setRenderer(renderer);

        return chart;
    }

    private static Color getRandomColor() {
        Random rand = new Random();
        return new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
    }

    private static Color getColor(int index) {
        switch (index) {
            case 0:
                return Color.RED;
            case 1:
                return Color.BLUE;
            case 2:
                return Color.GREEN;
            default:
                return getRandomColor();
        }
    }

    public static JPanel graphAsPanel(Graph graph, Integer width, Integer height) throws UtilException {
        JFreeChart chart = plotDataset(graph);
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(width, height));
        return panel;
    }

    public static JPanel graphAsPanel(Graph graph) throws UtilException {
        return graphAsPanel(graph, 640, 480);
    }


}
