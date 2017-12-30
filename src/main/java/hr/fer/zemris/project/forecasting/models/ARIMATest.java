package hr.fer.zemris.project.forecasting.models;

import hr.fer.zemris.project.forecasting.util.ArraysUtil;
import hr.fer.zemris.project.forecasting.util.DataReaderUtil;

import java.io.IOException;
import java.util.Arrays;

public class ARIMATest {

    public static void main(String[] args) throws Exception{
        double[] dataset = DataReaderUtil.readDataset("datasets/exchange-rate-twi-may-1970-aug-1.csv");

        System.out.println(ArraysUtil.toList(dataset));

        ARIMA arima = new ARIMA(0, 4, ArraysUtil.toList(dataset));

//        for(double d: arima.computeNextValues(10)){
//            System.out.println(d);
//        }
        System.out.println(arima.computeNextValue());

        //treba dobiti: [1] 834.3850 844.0228 850.6506

    }
}
