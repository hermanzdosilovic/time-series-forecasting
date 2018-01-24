package hr.fer.zemris.project.forecasting.models;

import hr.fer.zemris.project.forecasting.util.ArraysUtil;
import hr.fer.zemris.project.forecasting.util.DataReaderUtil;

import java.io.IOException;
import java.util.Arrays;

public class ARIMATest {

    public static void main(String[] args) throws Exception{
        double[] dataset = DataReaderUtil.readDataset("datasets/exchange-rate-twi-may-1970-aug-1.csv");

        ARIMA arima = new ARIMA(0, 1, ArraysUtil.toList(dataset));

        System.out.println(Arrays.toString(dataset));
        System.out.println(Arrays.toString(arima.testDataset()));

        double[] test = arima.testDataset();
        double mse = 0;
        for(int i = 0; i < dataset.length; i++){
            double d = dataset[i] - test[i];
            mse = d*d;
        }
        System.out.println(Math.sqrt(mse));


//        for(double d: arima.computeNextValues(10)){
//            System.out.println(d);
//        }


    }
}
