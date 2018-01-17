package hr.fer.zemris.project.forecasting.gp.gui;

public interface IObserver<T> {

    T getBestSolution();

    T start();

    void stop();

    void addListener(IListener l);

    void removeListener(IListener l);
}
