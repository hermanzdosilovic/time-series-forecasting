package hr.fer.zemris.project.forecasting.gp.values.uniVariable;

import java.io.Serializable;
import java.util.function.DoubleUnaryOperator;

public abstract class DoubleUnary implements DoubleUnaryOperator, Serializable {
    @Override public int hashCode() {
        return 0;
    }

    @Override public boolean equals(Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass();
    }

}
