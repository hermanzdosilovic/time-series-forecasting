package hr.fer.zemris.projekt.predictions.util;

import java.util.ArrayList;
import java.util.List;

public class Graph {

    private List<List<Double>> data;

    private List<String> names;

    public Graph() {
        data = new ArrayList<>();
        names = new ArrayList<>();
    }

    public Graph(List<List<Double>> data, List<String> names) {
        this.data = data;
        this.names = names;
    }

    public Integer size() {
        return data.size();
    }

    public void addNewData(List<Double> list, String name) {
        data.add(list);
        names.add(name);
    }

    public void removeFromData(List<Double> list, String name) {
        data.remove(list);
        names.remove(name);
    }

    public List<Double> getData(int index) throws UtilException {
        checkIfIndexIsInCorrectRange(index);
        return data.get(index);
    }

    public String getName(int index) throws UtilException {
        checkIfIndexIsInCorrectRange(index);
        return names.get(index);
    }

    public void checkIfIndexIsInCorrectRange(int index) throws UtilException {
        int size = size();
        if (index >= size || index < 0) {
            throw new UtilException(
                    String.format("Wrong index given. Right indexes are in range from 0 to %d", size - 1)
            );
        }
    }
}

