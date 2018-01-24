package hr.fer.zemris.project.forecasting.gp.reproduction;

import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import hr.fer.zemris.project.forecasting.gp.selections.ISelection;
import hr.fer.zemris.project.forecasting.gp.values.ValueTypes;

import java.util.List;
import java.util.Random;

public abstract class AReproduction<T> {

    private Random random;

    private List<DatasetEntry> train;

    private List<DatasetEntry> test;

    private ValueTypes valueTypes;

    public AReproduction(
        Random random,
        List<DatasetEntry> train,
        List<DatasetEntry> test,
        ValueTypes valueTypes
    ) {
        this.random = random;
        this.train = train;
        this.test = test;
        this.valueTypes = valueTypes;
    }

    public abstract T[] getChildren(T[] population, ISelection<T> selection);

    public Random getRandom() {
        return random;
    }

    public List<DatasetEntry> getTest() {
        return test;
    }

    public List<DatasetEntry> getTrain() {
        return train;
    }

    public ValueTypes getValueTypes() {
        return valueTypes;
    }
}
