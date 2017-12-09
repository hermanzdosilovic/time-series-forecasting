package hr.fer.zemris.project.forecasting.metaheuristics.observers;

import com.dosilovic.hermanzvonimir.ecfjava.util.Solution;

public class PenaltyObserver<T> extends AbstractDataObserver<T> {

    @Override
    protected double getValueOfInterest(Solution<T> solution) {
        return solution.getPenalty();
    }
}
