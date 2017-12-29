package hr.fer.zemris.project.forecasting.models;

import hr.fer.zemris.project.forecasting.util.ArraysUtil;
import hr.fer.zemris.project.forecasting.util.DataReaderUtil;

import java.io.IOException;

public class ARIMATest {

    public static void main(String[] args) throws IOException{
        double[] dataset = DataReaderUtil.readDataset("./datasets/monthly-milk-production-pounds-p.csv");

        for (double d : dataset) {
            System.out.println(d + ",");
        }

        ARIMA arima = new ARIMA(0, 2, ArraysUtil.toList(dataset));

        System.out.println(arima.computeNextValue());
        for(double d: arima.computeNextValues(7)){
            System.out.println(d);
        }

        //treba dobiti: [1] 834.3850 844.0228 850.6506

    }
}
