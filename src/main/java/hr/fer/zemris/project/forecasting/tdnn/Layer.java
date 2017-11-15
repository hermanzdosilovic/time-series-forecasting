package hr.fer.zemris.project.forecasting.tdnn;

import java.util.ArrayList;
import java.util.List;

public class Layer {
    private List<Neuron> neurons = new ArrayList<>();

    public void addNeuron(Neuron neuron) {
        neurons.add(neuron);
    }

    public Neuron getNeuronAtIndex(int index) {
        return neurons.get(index);
    }

    public int sizeOfLayer() {
        return neurons.size();
    }
}
