package hr.fer.zemris.project.forecasting.metaheuristics.observers;

import com.dosilovic.hermanzvonimir.ecfjava.metaheuristics.util.IObserver;
import com.dosilovic.hermanzvonimir.ecfjava.util.Solution;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDataObserver<T> implements IObserver<T> {

    private List<Double> data;
    private double       lastValue;

    public AbstractDataObserver() {
        data = new ArrayList<>();
    }

    public double[] getData() {
        return ArrayUtils.toPrimitive(data.toArray(new Double[data.size()]));
    }

    public double getLastValue() {
        return lastValue;
    }

    @Override
    public void update(Solution<T> solution) {
        lastValue = getValueOfInterest(solution);
        data.add(lastValue);
    }

    protected abstract double getValueOfInterest(Solution<T> solution);
}
