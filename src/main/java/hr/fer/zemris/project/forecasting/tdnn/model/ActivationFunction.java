package hr.fer.zemris.project.forecasting.tdnn.model;

import org.apache.commons.math3.analysis.UnivariateFunction;

public final class ActivationFunction {

    public static final UnivariateFunction RELU = (v) -> {
        return Math.max(0, v);
    };

    public static final UnivariateFunction SIGMOID = (v) -> {
        return 1.0 / (1.0 + Math.exp(-v));
    };

    public static final UnivariateFunction LINEAR = (v) -> {
        return v;
    };
}
