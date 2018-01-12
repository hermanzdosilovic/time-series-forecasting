package hr.fer.zemris.project.forecasting.models;

import hr.fer.zemris.project.forecasting.models.arma.ARMA;
import hr.fer.zemris.project.forecasting.util.ArraysUtil;

import java.util.Arrays;
import java.util.List;

public class ARIMA extends AModel {

    private AModel model;

    private Stationary stat;

    public ARIMA(int p, int q, List<Double> dataset){
        stat = new Stationary(dataset);
        setModel(p, q, stat.getDatasetAsList());
    }

    private void setModel(int p, int q, List<Double> dataset) throws IllegalArgumentException{
        boolean differenced = stat.getOrder() > 0;
        if (q == 0) {
            model = new AR(p, dataset);
        } else if (p == 0) {
            model = new ARMA(0, q, ArraysUtil.toPrimitiveArray(dataset), differenced);
        } else {
            throw new IllegalArgumentException("ARMA model currently not supported.");
        }
    }

    public AModel getModel() {
        return model;
    }

    public double computeNextValue() {
        double value = model.computeNextValue();
        List<Double> tmp = stat.getDatasetAsList();
        tmp.add(value);
        return stat.accumulateAndReturnLast(tmp);
    }

    @Override
    public double[] getCoeffs() {
        return model.getCoeffs();
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

    @Override
    public double[] testDataset() {
        if(stat.getOrder() == 0) return model.testDataset();
        else{
            double[] differentiated = model.testDataset();
            double[] oneUp = stat.getDataset();
            for(int i = 0; i < stat.getOrder(); i++){
                double[] oldDifferentiated = differentiated;
                differentiated = Arrays.copyOf(differentiated, differentiated.length + 1);
                oneUp = stat.computeOneBefore(stat.getFirstValues().get(i), oneUp);
                for(int j = 0; j < differentiated.length; j++){
                    if(j > 0) differentiated[j] = oneUp[j - 1] + oldDifferentiated[j - 1];
                    else differentiated[j] = oneUp[j];
                }
            }
            return differentiated;
        }
    }


}
