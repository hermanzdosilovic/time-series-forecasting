package hr.fer.zemris.project.forecasting.util.math;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.List;

public final class Vectors {
    
    public static RealVector asRealVector(List<Double> values) {
        RealVector vector = new ArrayRealVector(values.size());
        for (int i = 0; i < values.size(); i++) {
            vector.setEntry(i, values.get(i));
        }
        return vector;
    }
}
