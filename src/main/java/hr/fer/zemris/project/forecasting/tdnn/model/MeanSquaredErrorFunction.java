package hr.fer.zemris.project.forecasting.tdnn.model;

import hr.fer.zemris.numeric.AbstractFunction;
import hr.fer.zemris.project.forecasting.tdnn.TimeDelayNN;
import hr.fer.zemris.project.forecasting.tdnn.util.DataUtil;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.List;

public class MeanSquaredErrorFunction extends AbstractFunction {

    private TimeDelayNN tdnn;
    private List<DataEntry> trainSet;

    public MeanSquaredErrorFunction(TimeDelayNN tdnn, List<DataEntry> trainSet) {
        super(tdnn.getNumberOfWeights());
        this.tdnn = tdnn;
        this.trainSet = trainSet;
    }

    @Override public double getValue(RealVector realVector) {
        tdnn.setWeights(realVector.toArray());
        return DataUtil.calculateMeanSquaredError(tdnn, trainSet);
    }

    @Override public RealVector getGradient(RealVector realVector) {
        return null; // ignorable
    }

    @Override public RealMatrix getHessianMatrix(RealVector realVector) {
        return null; // ignorable
    }
}
