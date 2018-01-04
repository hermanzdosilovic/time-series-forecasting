package hr.fer.zemris.project.forecasting.nn.functions;

import com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork;
import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;

import java.util.List;

public interface IErrorFunction {

    public double getError(INeuralNetwork neuralNetwork, List<DatasetEntry> dataset);
}
