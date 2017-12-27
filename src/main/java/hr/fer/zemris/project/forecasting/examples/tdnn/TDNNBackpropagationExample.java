package hr.fer.zemris.project.forecasting.examples.tdnn;

import com.dosilovic.hermanzvonimir.ecfjava.neural.FeedForwardANN;
import com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork;
import com.dosilovic.hermanzvonimir.ecfjava.neural.activations.IdentityActivation;
import com.dosilovic.hermanzvonimir.ecfjava.neural.activations.ReLUActivation;
import com.dosilovic.hermanzvonimir.ecfjava.neural.activations.SigmoidActivation;
import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import hr.fer.zemris.project.forecasting.nn.Backpropagation;
import hr.fer.zemris.project.forecasting.nn.util.DataEntry;
import hr.fer.zemris.project.forecasting.nn.util.NeuralNetworkUtil;
import hr.fer.zemris.project.forecasting.util.DataReaderUtil;
import hr.fer.zemris.project.forecasting.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class TDNNBackpropagationExample {

    public static void main(String[] args) throws IOException {
        final int[] ARCHITECTURE = {5, 4, 1};
        int tdnnInputSize = ARCHITECTURE[0];
        int tdnnOutputSize = ARCHITECTURE[ARCHITECTURE.length - 1];

        double[] dataset = DataReaderUtil.readDataset("./datasets/exchange-rate-twi-may-1970-aug-1.csv");

        double[] normalizedDataset = new double[dataset.length];
        double min = Integer.MAX_VALUE;
        double max = Integer.MIN_VALUE;
        for (double d : dataset) {
            min = Math.min(min, d);
            max = Math.max(max, d);
        }
        for (int i = 0; i < dataset.length; ++i) {
            double z = (dataset[i] - min) / (max - min);
            z = z * 2. - 1.;
            normalizedDataset[i] = z;
        }

        List<DataEntry> tdnnDataset = NeuralNetworkUtil.createTDNNDateset(normalizedDataset, tdnnInputSize, tdnnOutputSize);

        Pair<List<DataEntry>, List<DataEntry>> splittedTDNNDataset =
                NeuralNetworkUtil.splitTDNNDataset(tdnnDataset, 0.11);

        List<DataEntry> trainSet = splittedTDNNDataset.getFirst();
        List<DataEntry> testSet = splittedTDNNDataset.getSecond();
        testSet = testSet.subList(0,testSet.size()/15);

        INeuralNetwork tdnn = new FeedForwardANN(ARCHITECTURE,
                SigmoidActivation.getInstance(),
                SigmoidActivation.getInstance(),
                SigmoidActivation.getInstance()
        );


        List<DatasetEntry> train = new ArrayList<>();
        trainSet.forEach(t -> train.add(new DatasetEntry(t.getInput(), t.getExpectedOutput())));
        List<DatasetEntry> test = new ArrayList<>();
        testSet.forEach(t -> test.add(new DatasetEntry(t.getInput(), t.getExpectedOutput())));
        Backpropagation bp = new Backpropagation(train, test, 0.1, 200_000, 1E-12, 1E-15);
        bp.train(tdnn);

        NeuralNetworkUtil.plot("Train", tdnn, trainSet);
        NeuralNetworkUtil.plot("Test", tdnn, testSet);
        NeuralNetworkUtil.plot("Dataset", tdnn, tdnnDataset);
    }

}
