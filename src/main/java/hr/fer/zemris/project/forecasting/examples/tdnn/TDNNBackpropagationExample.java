//package hr.fer.zemris.project.forecasting.examples.tdnn;
//
//import com.dosilovic.hermanzvonimir.ecfjava.neural.FeedForwardANN;
//import com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork;
//import com.dosilovic.hermanzvonimir.ecfjava.neural.activations.ReLUActivation;
//import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
//import hr.fer.zemris.project.forecasting.nn.Backpropagation;
//import hr.fer.zemris.project.forecasting.nn.util.DataEntry;
//import hr.fer.zemris.project.forecasting.nn.util.NeuralNetworkUtil;
//import hr.fer.zemris.project.forecasting.util.DataReaderUtil;
//import hr.fer.zemris.project.forecasting.util.Pair;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//public final class TDNNBackpropagationExample {
//
//    public static void main(String[] args) throws IOException {
//        final int[] ARCHITECTURE = {5, 4, 1};
//        int tdnnInputSize = ARCHITECTURE[0];
//        int tdnnOutputSize = ARCHITECTURE[ARCHITECTURE.length - 1];
//
//        double[] dataset = DataReaderUtil.readDataset("./datasets/exchange-rate-twi-may-1970-aug-1.csv");
//        List<DataEntry> tdnnDataset = NeuralNetworkUtil.createTDNNDateset(dataset, tdnnInputSize, tdnnOutputSize);
//
//        Pair<List<DataEntry>, List<DataEntry>> splittedTDNNDataset =
//                NeuralNetworkUtil.splitTDNNDataset(tdnnDataset, 0.90);
//
//        List<DataEntry> trainSet = splittedTDNNDataset.getFirst();
//        List<DataEntry> testSet = splittedTDNNDataset.getSecond();
//
//        INeuralNetwork tdnn = new FeedForwardANN(ARCHITECTURE,
//                ReLUActivation.getInstance(),
//                ReLUActivation.getInstance(),
//                ReLUActivation.getInstance()
//        );
//
//
//        List<DatasetEntry> train = new ArrayList<>();
//        trainSet.forEach(t -> train.add(new DatasetEntry(t.getInput(), t.getExpectedOutput())));
//        List<DatasetEntry> test = new ArrayList<>();
//        testSet.forEach(t -> test.add(new DatasetEntry(t.getInput(), t.getExpectedOutput())));
//        Backpropagation bp = new Backpropagation(train, test, 1E-8, 300_000, 1E-12, 1E-15,tdnn, 32);
//        double startTime = System.currentTimeMillis();
//        bp.run();
//        double endTime = System.currentTimeMillis();
//        double deltaTime = (endTime - startTime) / 1000.;
//        System.err.println("Time: " + deltaTime + " s");
//        List<DataEntry> datasetEntries = new ArrayList<>(trainSet);
//        datasetEntries.addAll(testSet);
//        double mse = 0;
//        for (DataEntry e : datasetEntries) {
//            double[] forecast = tdnn.forward(e.getInput());
//            mse += Math.pow(forecast[0] - e.getExpectedOutput()[0], 2);
//        }
//        mse /= datasetEntries.size();
//        System.err.println("dataset mse: " + mse);
//        NeuralNetworkUtil.plot("Train", tdnn, trainSet);
//        NeuralNetworkUtil.plot("Test", tdnn, testSet);
//        NeuralNetworkUtil.plot("Dataset", tdnn, tdnnDataset);
//    }
//
//}
