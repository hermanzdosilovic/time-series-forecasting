package hr.fer.zemris.project.forecasting.gp.values.terminating;

import java.util.function.Function;

public interface ITerminatorGenerator {

    Function<double[], Double> getRandomTerminator();
}
