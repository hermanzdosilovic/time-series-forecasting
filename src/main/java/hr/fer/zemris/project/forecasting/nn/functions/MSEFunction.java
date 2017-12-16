package hr.fer.zemris.project.forecasting.nn.functions;

import com.dosilovic.hermanzvonimir.ecfjava.numeric.IFunction;
import hr.fer.zemris.project.forecasting.nn.INeuralNetwork;
import hr.fer.zemris.project.forecasting.nn.util.DataEntry;
import hr.fer.zemris.project.forecasting.nn.util.NeuralNetworkUtil;
import hr.fer.zemris.project.forecasting.util.NumericErrorUtil;
import org.apache.commons.math3.linear.RealVector;

import java.util.List;

public class MSEFunction<T extends RealVector> implements IFunction<T>, IErrorFunction {

    private INeuralNetwork  neuralNetwork;
    private List<DataEntry> dataset;

    public MSEFunction(INeuralNetwork neuralNetwork, List<DataEntry> dataset) {
        this.neuralNetwork = neuralNetwork;
        this.dataset = dataset;
    }

    @Override public double getValue(T point) {
        neuralNetwork.setWeights(point.toArray());
        return getError(neuralNetwork, dataset);
    }

    @Override public double getError(
        INeuralNetwork neuralNetwork, List<DataEntry> dataset
    ) {
        double[] forecast = NeuralNetworkUtil.forward(neuralNetwork, dataset);
        double[] actual   = NeuralNetworkUtil.joinExpectedValues(dataset);
        return NumericErrorUtil.meanSquaredError(actual, forecast);
    }
}
