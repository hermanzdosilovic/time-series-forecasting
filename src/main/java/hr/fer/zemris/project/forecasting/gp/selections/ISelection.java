package hr.fer.zemris.project.forecasting.gp.selections;

public interface ISelection<T> {

    T getParent(T[] population);
}
