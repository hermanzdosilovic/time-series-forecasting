package hr.fer.zemris.project.forecasting.gp.gui;

public interface IListener<T> {

    void newBest(T best, Integer iter);

}
