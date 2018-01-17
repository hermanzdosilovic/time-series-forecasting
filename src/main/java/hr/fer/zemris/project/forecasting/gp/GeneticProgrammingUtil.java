package hr.fer.zemris.project.forecasting.gp;

import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import hr.fer.zemris.project.forecasting.gp.tree.BinaryTree;
import hr.fer.zemris.project.forecasting.util.GraphUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneticProgrammingUtil {

    public static void plot(String graphName, BinaryTree tree, List<DatasetEntry> testData) {
        double[] forecastedOutput = new double[testData.size()];
        double[] actualOutput     = new double[testData.size()];

        tree.forward(forecastedOutput, actualOutput, testData);

        Map<String, double[]> graph = new HashMap<>();
        graph.put("Expected", actualOutput);
        graph.put("Forecasted", forecastedOutput);

        GraphUtil.plot(graph, graphName);
    }
}
