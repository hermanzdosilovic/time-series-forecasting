package hr.fer.zemris.projekt.predictions.tdnn;

import java.util.ArrayList;
import java.util.List;

public class Layer {
    private List<Neuron[]> neurons = new ArrayList<>();

    public void addNeurons(Neuron[] neuronsArray) {
        neurons.add(neuronsArray);
    }

    public Neuron[] getNeuronsAtIndex(int index) {
        return neurons.get(index);
    }

    public int sizeOfNeuronsList() {
        return neurons.size();
    }
}
