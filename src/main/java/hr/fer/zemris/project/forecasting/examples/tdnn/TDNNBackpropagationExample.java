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
        final int[] ARCHITECTURE   = {5, 1};
        int         tdnnInputSize  = ARCHITECTURE[0];
        int         tdnnOutputSize = ARCHITECTURE[ARCHITECTURE.length - 1];

        double[] dataset = DataReaderUtil.readDataset("./datasets/exchange-rate-twi-may-1970-aug-1.csv");

        List<DataEntry> tdnnDataset = NeuralNetworkUtil.createTDNNDateset(dataset, tdnnInputSize, tdnnOutputSize);

        Pair<List<DataEntry>, List<DataEntry>> splittedTDNNDataset =
            NeuralNetworkUtil.splitTDNNDataset(tdnnDataset, 0.8);

        List<DataEntry> trainSet = splittedTDNNDataset.getFirst();
        List<DataEntry> testSet  = splittedTDNNDataset.getSecond();

        INeuralNetwork tdnn           = new FeedForwardANN(ARCHITECTURE,
                SigmoidActivation.getInstance(),
                ReLUActivation.getInstance()
                );



        List<DatasetEntry> train = new ArrayList<>();
        trainSet.forEach(t -> train.add(new DatasetEntry(t.getInput(),t.getExpectedOutput())));
        List<DatasetEntry> test = new ArrayList<>();
        testSet.forEach(t -> test.add(new DatasetEntry(t.getInput(),t.getExpectedOutput())));
        Backpropagation bp = new Backpropagation(train,test,0.1,50,1E-6,1E-8);
        bp.train(tdnn);

        for(int i=0; i<trainSet.size();++i) {
          System.out.println(i+" expected: "+trainSet.get(i).getExpectedOutput()[0]+" forecast: "+tdnn.forward(trainSet.get(i).getInput())[0]);
        }

        NeuralNetworkUtil.plot("Train", tdnn, trainSet);
        NeuralNetworkUtil.plot("Test", tdnn, testSet);
        NeuralNetworkUtil.plot("Dataset", tdnn, tdnnDataset);
    }

}
