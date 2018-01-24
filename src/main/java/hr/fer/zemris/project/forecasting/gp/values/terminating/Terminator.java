package hr.fer.zemris.project.forecasting.gp.values.terminating;

import java.io.Serializable;
import java.util.function.Function;

public abstract class Terminator implements Function<double[], Double>, Serializable {
    @Override public int hashCode() {
        return 0;
    }

    @Override public boolean equals(Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass();
    }
}
