package hr.fer.zemris.project.forecasting.nn.functions;

import com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork;
import com.dosilovic.hermanzvonimir.ecfjava.numeric.IFunction;
import com.dosilovic.hermanzvonimir.ecfjava.util.RealVector;
import hr.fer.zemris.project.forecasting.nn.util.DataEntry;
import hr.fer.zemris.project.forecasting.nn.util.NeuralNetworkUtil;
import hr.fer.zemris.project.forecasting.util.NumericErrorUtil;

import java.util.List;

public class MPEFunction<T extends RealVector> implements IFunction<T>, IErrorFunction {



    private INeuralNetwork neuralNetwork;
    private List<DataEntry> dataset;

    public MPEFunction(INeuralNetwork neuralNetwork, List<DataEntry> dataset) {
        this.neuralNetwork = neuralNetwork;
        this.dataset = dataset;
    }

    @Override public double getValue(T point) {
        neuralNetwork.setWeights(point.toArray());
        return getError(neuralNetwork, dataset);
    }


    @Override
    public double getError(com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork neuralNetwork, List<DataEntry> dataset) {
        return 0;
    }
}
