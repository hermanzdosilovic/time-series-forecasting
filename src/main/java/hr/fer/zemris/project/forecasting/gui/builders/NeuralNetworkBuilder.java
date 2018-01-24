package hr.fer.zemris.project.forecasting.gui.builders;

import com.dosilovic.hermanzvonimir.ecfjava.neural.ElmanNN;
import com.dosilovic.hermanzvonimir.ecfjava.neural.FeedForwardANN;
import com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork;

public class NeuralNetworkBuilder {

    public static INeuralNetwork createNeuralNetwork(INeuralNetwork nn) {
        INeuralNetwork neuralNetwork;
        if (nn instanceof FeedForwardANN) {
            neuralNetwork = new FeedForwardANN(nn.getArchitecture(), nn.getLayerActivations());
        } else {
            neuralNetwork = new ElmanNN(nn.getArchitecture(), nn.getLayerActivations());
        }
        return neuralNetwork;
    }

}
