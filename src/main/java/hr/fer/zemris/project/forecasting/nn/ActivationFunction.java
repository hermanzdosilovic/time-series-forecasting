package hr.fer.zemris.project.forecasting.nn;

import org.apache.commons.math3.analysis.UnivariateFunction;

public final class ActivationFunction {

    public static final UnivariateFunction IDENTITY = (v) -> {
        return v;
    };

    public static final UnivariateFunction BINARY_STEP = (v) -> {
        return v >= 0 ? 1 : 0;
    };

    public static final UnivariateFunction SIGMOID = (v) -> {
        return 1.0 / (1.0 + Math.exp(-v));
    };

    public static final UnivariateFunction TANH = (v) -> {
        return (2.0 / (1.0 + Math.exp(-2.0 * v))) - 1.0;
    };

    public static final UnivariateFunction SOFTSIGN = (v) -> {
        return v / (1 + Math.abs(v));
    };

    public static final UnivariateFunction RELU = (v) -> {
        return Math.max(0, v);
    };
}
