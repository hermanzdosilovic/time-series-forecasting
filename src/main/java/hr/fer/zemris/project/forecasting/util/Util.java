package hr.fer.zemris.project.forecasting.util;

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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Util {

    private static final int    LAST_VALUE_INDICATOR = -1;
    private static final String DEFAULT_DELIMITER    = ",";
    private static final int    DEFAULT_WIDTH        = 640;
    private static final int    DEFAULT_HEIGHT       = 480;
    private static final String DEFAULT_GRAPH_NAME   = "";
    private static final Random RAND                 = new Random();

    public static double[] readDataset(Path path, Integer indexOfValue, String delimiter) throws IOException {
        List<String> dataset = Files.readAllLines(path);
        double[]     result  = new double[dataset.size()];

        for (int i = 0, n = dataset.size(); i < n; ++i) {
            String[] data  = dataset.get(i).trim().split(String.format("[%s]", delimiter));
            int      index = (indexOfValue == LAST_VALUE_INDICATOR ? data.length - 1 : indexOfValue);

            result[i] = Double.parseDouble(data[index].trim());
        }

        return result;
    }

    public static double[] readDataset(Path path, Integer indexOfValue) throws IOException {
        return readDataset(path, indexOfValue, DEFAULT_DELIMITER);
    }

    public static double[] readDataset(Path path, String delimiter) throws IOException {
        return readDataset(path, LAST_VALUE_INDICATOR, delimiter);
    }

    public static double[] readDataset(Path path) throws IOException {
        return readDataset(path, LAST_VALUE_INDICATOR);
    }

    public static double[] readDataset(String path, Integer indexOfValue, String delimiter) throws IOException {
        return readDataset(Paths.get(path), indexOfValue, delimiter);
    }

    public static double[] readDataset(String path, Integer indexOfValue) throws IOException {
        return readDataset(Paths.get(path), indexOfValue);
    }

    public static double[] readDataset(String path, String delimiter) throws IOException {
        return readDataset(Paths.get(path), delimiter);
    }

    public static double[] readDataset(String path) throws IOException {
        return readDataset(Paths.get(path));
    }

    public static void saveAsPNG(
        Path path,
        String graphName,
        int width,
        int height,
        Map<String, double[]> data
    ) throws IOException {
        JFreeChart xylineChart = dataAsChart(graphName, data);

        int i = 1;
        while (Files.exists(path)) {
            String   name     = path.getFileName().toString();
            String[] tmpArray = name.split("[.]");

            int    indexOfBracket = tmpArray[0].indexOf("(");
            String filename;
            if (indexOfBracket != -1) {
                filename = tmpArray[0].substring(0, indexOfBracket);
            } else {
                filename = tmpArray[0];
            }

            name = String.format("%s(%d).%s", filename, i++, tmpArray[1]);
            path = path.getParent();
            path = path.resolve(name);
        }

        ChartUtilities.saveChartAsPNG(path.toFile(), xylineChart, width, height);
    }

    public static void saveAsPNG(Path path, String graphName, Map<String, double[]> data) throws IOException {
        saveAsPNG(path, graphName, DEFAULT_WIDTH, DEFAULT_HEIGHT, data);
    }

    public static void saveAsPNG(Path path, Map<String, double[]> data) throws IOException {
        saveAsPNG(path, DEFAULT_GRAPH_NAME, DEFAULT_WIDTH, DEFAULT_HEIGHT, data);
    }

    public static void saveAsPNG(
        String path,
        String graphName,
        int width,
        int height,
        Map<String, double[]> data
    ) throws IOException {
        saveAsPNG(Paths.get(path), graphName, width, height, data);
    }

    public static void saveAsPNG(String path, String graphName, Map<String, double[]> data) throws IOException {
        saveAsPNG(Paths.get(path), graphName, data);
    }

    public static void saveAsPNG(String path, Map<String, double[]> data) throws IOException {
        saveAsPNG(Paths.get(path), data);
    }

    public static void plot(Map<String, double[]> data, String graphName, int width, int height) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            JPanel panel = dataAsPanel(data, graphName, width, height);
            frame.add(panel, BorderLayout.CENTER);

            frame.pack();
            frame.setVisible(true);
        });
    }

    public static void plot(Map<String, double[]> data, String graphName) {
        plot(data, graphName, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public static void plot(Map<String, double[]> data, int width, int height) {
        plot(data, DEFAULT_GRAPH_NAME, width, height);
    }

    public static void plot(Map<String, double[]> data) {
        plot(data, DEFAULT_GRAPH_NAME);
    }

    public static JFreeChart dataAsChart(String graphName, Map<String, double[]> data) {
        XYSeriesCollection     xyCollection = new XYSeriesCollection();
        XYLineAndShapeRenderer renderer     = new XYLineAndShapeRenderer();

        int i = 0;
        for (Map.Entry<String, double[]> dataset : data.entrySet()) {
            final XYSeries series = new XYSeries(dataset.getKey());

            double[] tmpList = dataset.getValue();
            for (int j = 0; j < tmpList.length; j++) {
                series.add(j, tmpList[j]);
            }
            xyCollection.addSeries(series);

            renderer.setSeriesPaint(i, getColor(i));
            renderer.setSeriesStroke(i, new BasicStroke(2f));
            renderer.setSeriesShapesVisible(i, false);

            i++;
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
            graphName,
            "Time interval",
            "Value",
            xyCollection,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        XYPlot plot = chart.getXYPlot();
        plot.setRenderer(renderer);

        return chart;
    }

    public static JFreeChart dataAsChart(Map<String, double[]> data) {
        return dataAsChart(DEFAULT_GRAPH_NAME, data);
    }

    public static JPanel dataAsPanel(Map<String, double[]> data, String graphName, int width, int height) {
        JFreeChart chart = dataAsChart(graphName, data);
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(width, height));
        return panel;
    }

    public static JPanel dataAsPanel(Map<String, double[]> data, String graphName) {
        return dataAsPanel(data, graphName, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public static JPanel dataAsPanel(Map<String, double[]> data, int width, int height) {
        return dataAsPanel(data, DEFAULT_GRAPH_NAME, width, height);
    }

    public static JPanel dataAsPanel(Map<String, double[]> data) {
        return dataAsPanel(data, DEFAULT_GRAPH_NAME);
    }

    private static Color getRandomColor() {
        return new Color(RAND.nextFloat(), RAND.nextFloat(), RAND.nextFloat());
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
}
