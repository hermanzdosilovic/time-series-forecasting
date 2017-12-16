package hr.fer.zemris.project.forecasting.nn.functions;

import com.dosilovic.hermanzvonimir.ecfjava.neural.INeuralNetwork;
import hr.fer.zemris.project.forecasting.nn.util.DataEntry;


import java.util.List;

public interface IErrorFunction {

    public double getError(INeuralNetwork neuralNetwork, List<DataEntry> dataset);
}
