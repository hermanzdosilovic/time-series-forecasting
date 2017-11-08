package hr.fer.zemris.projekt.predictions.tdnn;

import java.util.ArrayList;
import java.util.List;

public class Layer {
    private List<Neuron> neurons = new ArrayList<>();

    public void addNeurons(Neuron neuron) {
        neurons.add(neuron);
    }

    public Neuron getNeuronAtIndex(int index) {
        return neurons.get(index);
    }

    public int sizeOfLayer() {
        return neurons.size();
    }
}
