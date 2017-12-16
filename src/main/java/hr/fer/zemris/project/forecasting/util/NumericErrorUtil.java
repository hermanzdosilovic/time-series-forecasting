package hr.fer.zemris.project.forecasting.util;

public final class NumericErrorUtil {

    public static double meanSquaredError(double[] actual, double[] forecast) {
        if (actual.length != forecast.length) {
            throw new IllegalArgumentException("Actual and forecast data are not the same length");
        }

        double error = 0;
        for (int i = 0; i < actual.length; i++) {
            error += Math.pow(actual[i] - forecast[i], 2);
        }

        return error / actual.length;
    }

    public static double meanAbsolutePercentageError(double[] actual, double[] forecast) {
        if (actual.length != forecast.length) {
            throw new IllegalArgumentException("Actual and expected data are not the same length");
        }

        double error = 0;
        for (int i = 0; i < actual.length; i++) {
            error += Math.abs((actual[i] - forecast[i]) / actual[i]);
        }

        return 100.0 / actual.length * error;
    }

    public static double meanPercentageError(double[] actual, double[] forecast) {
        if (actual.length != forecast.length) {
            throw new IllegalArgumentException("Actual and expected data are not the same length");
        }

        double error = 0;
        for (int i = 0; i < actual.length; i++) {
            error += (actual[i] - forecast[i]) / actual[i];
        }

        return 100.0 / actual.length * error;
    }
}
