package hr.fer.zemris.project.forecasting.nn.functions;

import com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork;
import com.dosilovic.hermanzvonimir.ecfjava.numeric.IFunction;
import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import com.dosilovic.hermanzvonimir.ecfjava.util.RealVector;
import hr.fer.zemris.project.forecasting.nn.util.DataEntry;
import hr.fer.zemris.project.forecasting.nn.util.NeuralNetworkUtil;
import hr.fer.zemris.project.forecasting.util.NumericErrorUtil;

import java.util.List;

public class MPEFunction<T extends RealVector> implements IFunction<T>, IErrorFunction {



    private INeuralNetwork neuralNetwork;
    private List<DatasetEntry> dataset;

    public MPEFunction(INeuralNetwork neuralNetwork, List<DatasetEntry> dataset) {
        this.neuralNetwork = neuralNetwork;
        this.dataset = dataset;
    }

    @Override public double getValue(T point) {
        neuralNetwork.setWeights(point.toArray());
        return getError(neuralNetwork, dataset);
    }


    @Override
    public double getError(INeuralNetwork neuralNetwork, List<DatasetEntry> dataset) {
        return 0;
    }
}
