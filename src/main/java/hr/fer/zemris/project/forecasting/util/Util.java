package hr.fer.zemris.project.forecasting.util;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
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
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class Util {

    public static List<Double> readDataset(Path path, Integer indexOfValue, String delimiter) throws IOException {
        List<String> dataset = Files.readAllLines(path);
        List<Double> result = new ArrayList<>();
        for (String line : dataset) {
            String[] data = line.trim().split(String.format("[%s]", delimiter));
            int index = indexOfValue == -1 ? data.length - 1 : indexOfValue;
            result.add(Double.parseDouble(data[index].trim()));
        }
        return result;
    }

    public static List<Double> readDataset(Path path, Integer indexOfValue) throws IOException {
        return readDataset(path, indexOfValue, ",");
    }

    public static List<Double> readDataset(Path path, String delimiter) throws IOException {
        return readDataset(path, -1, delimiter);
    }

    public static List<Double> readDataset(Path path) throws IOException {
        return readDataset(path, -1, ",");
    }


    public static void datasetToPNG(Path path, Integer width, Integer height, Map<String, List<Double>> data) throws IOException {
        JFreeChart xylineChart = plotDataset(data);

        int i = 1;
        while (Files.exists(path)) {
            String name = path.getFileName().toString();
            String[] tmpArray = name.split("[.]");

            int indexOfBracket = tmpArray[0].indexOf("(");
            String filename;
            if (indexOfBracket != -1) {
                filename = tmpArray[0].substring(0, indexOfBracket);
            }else {
                filename = tmpArray[0];
            }

            name = String.format("%s(%d).%s", filename, i++, tmpArray[1]);
            path = path.getParent();
            path = path.resolve(name);
        }

        ChartUtilities.saveChartAsPNG(path.toFile(), xylineChart, width, height);
    }

    public static void datasetToPNG(Path path, Map<String, List<Double>> data) throws IOException {
        datasetToPNG(path, 640, 480, data);
    }

    public static void datasetToPNG(Path path, List<List<Double>> data) throws IOException {
        datasetToPNG(path, listOfListsToMap(data));
    }

    public static void datasetToPNGOne(Path path, List<Double> data) throws IOException {
        datasetToPNG(path, listToListOfLists(data));
    }

    public static void datasetToPNG(Path path, List<Double>... data) throws IOException {
        datasetToPNG(path, Arrays.asList(data));
    }


    public static void plot(Map<String, List<Double>> data) {
        SwingUtilities.invokeLater(() -> plotAndShowData(data));
    }

    public static void plot(List<List<Double>> data) {
        plot(listOfListsToMap(data));
    }

    public static void plot(List<Double>... data) {
        plot(Arrays.asList(data));
    }

    public static void plotOne(List<Double> data) {
        plot(listToListOfLists(data));
    }


    public static JFreeChart plotDataset(Map<String, List<Double>> data) {
        final XYSeriesCollection xyCollection = new XYSeriesCollection();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        int i = 0;
        for (Map.Entry<String, List<Double>> dataset : data.entrySet()) {
            final XYSeries series = new XYSeries(dataset.getKey());

            List<Double> tmpList = dataset.getValue();
            for (int j = 0; j < tmpList.size(); j++) {
                series.add(j, tmpList.get(j));
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

    private static void plotAndShowData(Map<String, List<Double>> data) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panel = graphAsPanel(data);
        frame.add(panel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    private static Map<String, List<Double>> listOfListsToMap(List<List<Double>> data) {
        Map<String, List<Double>> tmpMap = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            tmpMap.put(String.format("Data %d", i + 1), data.get(i));
        }
        return tmpMap;
    }

    private static List<List<Double>> listToListOfLists(List<Double> data) {
        List<List<Double>> tmpList = new ArrayList<>();
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

    public static JPanel graphAsPanel(Map<String, List<Double>> data, Integer width, Integer height) {
        JFreeChart chart = plotDataset(data);
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(width, height));
        return panel;
    }

    public static JPanel graphAsPanel(Map<String, List<Double>> data) {
        return graphAsPanel(data, 640, 480);
    }


    public static double[] listToArray(List<Double> list) {
        return list.stream().mapToDouble(d -> d).toArray();
    }

    public static List<Double> arrayToList(double[] array) {
        return DoubleStream.of(array).boxed().collect(Collectors.toList());
    }

    public static double computeMean(double[] array) {
        return new Mean().evaluate(array);
    }

    public static double computeMean(List<Double> data) {
        return computeMean(Util.listToArray(data));
    }
}
