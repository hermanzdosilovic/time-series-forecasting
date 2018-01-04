package hr.fer.zemris.project.forecasting.nn.util;

import com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork;
import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import hr.fer.zemris.project.forecasting.util.GraphUtil;
import hr.fer.zemris.project.forecasting.util.Pair;

import java.util.*;

public final class NeuralNetworkUtil {

    public static double[] forward(INeuralNetwork neuralNetwork, List<DatasetEntry> tdnnDataset) {
        int      offset = 0;
        double[] output = new double[tdnnDataset.size() * neuralNetwork.getOutputSize()];

        for (DatasetEntry entry : tdnnDataset) {
            double[] results = (neuralNetwork.forward(entry.getInput()));
            for (double result : results) {
                output[offset++] = result;
            }
        }

        return output;
    }

    public static double[] forward(INeuralNetwork neuralNetwork, double[] dataset) {
        return forward(
            neuralNetwork,
            createTDNNDateset(
                dataset,
                neuralNetwork.getInputSize(),
                neuralNetwork.getOutputSize()
            )
        );
    }

    public static List<DatasetEntry> createTDNNDateset(
        double[] dataset,
        int inputSize,
        int outputSize
    ) {
        List<DatasetEntry> tdnnDataset = new ArrayList<>(dataset.length - inputSize - outputSize + 1);
        for (int i = inputSize; i <= dataset.length - outputSize; i++) {
            tdnnDataset.add(new DatasetEntry(
                Arrays.copyOfRange(dataset, i - inputSize, i),
                Arrays.copyOfRange(dataset, i, i + outputSize)
            ));
        }

        return tdnnDataset;
    }

    public static Pair<List<DataEntry>, List<DataEntry>> splitTDNNDataset(
        List<DataEntry> dataset,
        double trainPercentage
    ) {
        int trainSize = (int) (dataset.size() * trainPercentage);
        return new Pair<>(
            dataset.subList(0, trainSize),
            dataset.subList(trainSize, dataset.size())
        );
    }


    public static double[] joinExpectedValues(List<DatasetEntry> tdnnDataset) {
        int      offset       = 0;
        double[] joinedValues = new double[tdnnDataset.size() * tdnnDataset.get(0).getOutput().length];

        for (DatasetEntry dataEntry : tdnnDataset) {
            double[] entries = dataEntry.getOutput();
            for (double entry : entries) {
                joinedValues[offset++] = entry;
            }
        }

        return joinedValues;
    }

    public static void plot(String graphName, INeuralNetwork neuralNetwork, List<DatasetEntry> dataset) {
        double[] expectedValues  = NeuralNetworkUtil.joinExpectedValues(dataset);
        double[] predictedValues = NeuralNetworkUtil.forward(neuralNetwork, dataset);

        Map<String, double[]> graph = new HashMap<>();
        graph.put("Expected", expectedValues);
        graph.put("Predicted", predictedValues);

        GraphUtil.plot(graph, graphName);
    }
}
