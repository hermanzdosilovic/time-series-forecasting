package hr.fer.zemris.project.forecasting.gp.values.biVariable;

import java.io.Serializable;
import java.util.function.DoubleBinaryOperator;

public abstract class DoubleBinary implements DoubleBinaryOperator, Serializable {
    @Override public int hashCode() {
        return 0;
    }

    @Override public boolean equals(Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass();
    }

}
