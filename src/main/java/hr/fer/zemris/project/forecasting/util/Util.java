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
import java.util.*;
import java.util.List;

public class Util {

    public static double[] readDataset(Path path, Integer indexOfValue, String delimiter) throws IOException {
        List<String> dataset = Files.readAllLines(path);
        double[] result = new double[dataset.size()];
        for (int i = 0, n = dataset.size(); i < n; ++i) {
            String[] data = dataset.get(i).trim().split(String.format("[%s]", delimiter));
            int index = indexOfValue == -1 ? data.length - 1 : indexOfValue;
            result[i] = Double.parseDouble(data[index].trim());
        }
        return result;
    }

    public static double[] readDataset(Path path, Integer indexOfValue) throws IOException {
        return readDataset(path, indexOfValue, ",");
    }

    public static double[] readDataset(Path path, String delimiter) throws IOException {
        return readDataset(path, -1, delimiter);
    }

    public static double[] readDataset(Path path) throws IOException {
        return readDataset(path, -1, ",");
    }

    public static double[] readDataset(String path) throws IOException {
        return readDataset(Paths.get(path));
    }


    public static void datasetToPNG(Path path, Integer width, Integer height, Map<String, double[]> data) throws IOException {
        JFreeChart xylineChart = plotDataset(data);

        int i = 1;
        while (Files.exists(path)) {
            String name = path.getFileName().toString();
            String[] tmpArray = name.split("[.]");

            int indexOfBracket = tmpArray[0].indexOf("(");
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

    public static void datasetToPNG(Path path, Map<String, double[]> data) throws IOException {
        datasetToPNG(path, 640, 480, data);
    }

    public static void datasetToPNG(Path path, List<double[]> data) throws IOException {
        datasetToPNG(path, listOfListsToMap(data));
    }

    public static void datasetToPNGOne(Path path, double[] data) throws IOException {
        datasetToPNG(path, listToListOfLists(data));
    }

    public static void datasetToPNG(Path path, double[]... data) throws IOException {
        datasetToPNG(path, data);
    }


    public static void plot(Map<String, double[]> data) {
        SwingUtilities.invokeLater(() -> plotAndShowData(data));
    }

    public static void plot(List<double[]> data) {
        plot(listOfListsToMap(data));
    }

    public static void plot(double[]... data) {
        plot(Arrays.asList(data));
    }

    public static void plotOne(double[] data) {
        plot(listToListOfLists(data));
    }


    public static JFreeChart plotDataset(Map<String, double[]> data) {
        final XYSeriesCollection xyCollection = new XYSeriesCollection();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
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
        JFreeChart chart = ChartFactory
                .createXYLineChart("Graph", "Time interval", "Value", xyCollection,
                        PlotOrientation.VERTICAL, true, true, false);

        final XYPlot plot = chart.getXYPlot();
        plot.setRenderer(renderer);

        return chart;
    }

    private static void plotAndShowData(Map<String, double[]> data) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panel = graphAsPanel(data);
        frame.add(panel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    private static Map<String, double[]> listOfListsToMap(List<double[]> data) {
        Map<String, double[]> tmpMap = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            tmpMap.put(String.format("Data %d", i + 1), data.get(i));
        }
        return tmpMap;
    }

    private static List<double[]> listToListOfLists(double[] data) {
        List<double[]> tmpList = new ArrayList<>();
        tmpList.add(data);
        return tmpList;
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

    public static JPanel graphAsPanel(Map<String, double[]> data, Integer width, Integer height) {
        JFreeChart chart = plotDataset(data);
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(width, height));
        return panel;
    }

    public static JPanel graphAsPanel(Map<String, double[]> data) {
        return graphAsPanel(data, 640, 480);
    }
}
