package hr.fer.zemris.project.forecasting.models;

import hr.fer.zemris.project.forecasting.models.arma.ARMA;
import hr.fer.zemris.project.forecasting.util.ArraysUtil;

import java.util.Arrays;
import java.util.List;

public class ARIMA extends AModel {

    private AModel model;

    private Stationary stat;

    public ARIMA(int p, int q, List<Double> dataset) {
        setModel(p, q, dataset);
        stat = new Stationary(dataset);
    }

    private void setModel(int p, int q, List<Double> dataset) {
        if (p == 0) {
            model = new ARMA(0, q, ArraysUtil.toPrimitiveArray(dataset));
        } else if (q == 0) {
            model = new AR(p, dataset);
        } else {
            throw new IllegalArgumentException("ARMA model currently not supported.");
        }
    }

    public double computeNextValue() {
        double value = model.computeNextValue();
        List<Double> tmp = stat.getDatasetAsList();
        tmp.add(value);
        return stat.accumulateAndReturnLast(tmp);
    }

    @Override public double[] computeNextValues(int n) {
        double[] values = model.computeNextValues(n);
        List<Double> tmp = stat.getDatasetAsList();
        for (int i = 0; i < values.length; i++) {
            tmp.add(values[i]);
        }

        double[] accumulatedData = stat.accumulate(ArraysUtil.toPrimitiveArray(tmp));
        return Arrays
            .copyOfRange(accumulatedData, accumulatedData.length - n, accumulatedData.length);

    }
}
