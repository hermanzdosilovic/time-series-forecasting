package hr.fer.zemris.projekt.predictions.util;

import hr.fer.zemris.projekt.predictions.util.UtilException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Util {

    public static Map<String, Double> parseDataset(Path path) throws UtilException {
        List<String> dataset;
        try {
            dataset = Files.readAllLines(path);
        } catch (IOException e) {
            throw new UtilException("Error while reading file.");
        }

        TreeMap<String, Double> map = new TreeMap<>();
        for (String line : dataset) {
            String[] data = line.split(",");
            if (data.length != 2) {
                throw new UtilException("Only univariable datasets permitted.");
            }

            map.put(data[0].trim(), Double.parseDouble(data[1].trim()));
        }
        return map;
    }


    public static void plotDataset(Map<String, Double> dataset, String fileName, Path path) throws UtilException {
        final XYSeries sols = new XYSeries(fileName);
        int i = 1;
        for (Double data : dataset.values()) {
            sols.add(i++, data.doubleValue());
        }

        final XYSeriesCollection xyCollection = new XYSeriesCollection();
        xyCollection.addSeries(sols);
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                fileName,
                "Time interval",
                "Value",
                xyCollection,
                PlotOrientation.VERTICAL,
                true, true, false);

        int width = 640;   /* Width of the image */
        int height = 480;  /* Height of the image */
        File XYChart = new File(String.format("%s/%s.png", path.toAbsolutePath().toString(), fileName));
        try {
            ChartUtilities.saveChartAsPNG(XYChart, xylineChart, width, height);
        } catch (IOException e) {
            throw new UtilException("Error happened while saving image onto disk.");
        }
    }

}
