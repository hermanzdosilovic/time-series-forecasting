package hr.fer.zemris.projekt.predictions.tdnn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TDNN {

    private Layer inputLayer;
    private Layer outputLayer;
    private Layer hiddenLayer;
    private int timeDelay;
    private List<Synapse> synapses = new ArrayList<>();


    public TDNN(int timeDelay, int inputLayerSize, int hiddenLayerSize) {
        this.timeDelay = timeDelay;

        createInputLayer(inputLayerSize);
        createHiddenLayer(hiddenLayerSize);
        createOutputLayer();

        synapses.addAll(connectLayers(inputLayer, hiddenLayer));
        synapses.addAll(connectLayers(hiddenLayer, outputLayer));
    }



    private void createInputLayer(int size) {
        inputLayer = new Layer();
        for (int i = 0; i < size; ++i) {
            inputLayer.addNeuron(new InputNeuron());
        }
    }

    private void createHiddenLayer(int size) {
        hiddenLayer = new Layer();
        for (int i = 0; i < size; ++i) {
            hiddenLayer.addNeuron(new HiddenNeuron(timeDelay));
        }
    }

    private void createOutputLayer() {
        outputLayer = new Layer();
        outputLayer.addNeuron(new OutputNeuron());
    }

    private static  List<Synapse> connectLayers(Layer fromLayer, Layer toLayer) {
        List<Synapse> synapses = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < toLayer.sizeOfLayer(); ++i) {
            Neuron toNeuron = toLayer.getNeuronAtIndex(i);
            toNeuron.addWeight(new Synapse(BiasNeuron.getInstance(),toNeuron));
            for (int j = 0; j < fromLayer.sizeOfLayer(); ++j) {
                Neuron fromNeuron = fromLayer.getNeuronAtIndex(j);
                Synapse synapse = new Synapse(fromNeuron, toNeuron);
                toNeuron.addWeight(synapse);
                synapses.add(synapse);
            }
            //modify weights to starting values from interval [-2.4/m , 2.4/m]
            int m = toNeuron.getInputSynapses().size();
            double rangeMin = -2.4/m;
            double rangeMax = 2.4/m;
            toNeuron.getInputSynapses().forEach(s -> {
                double weight = rangeMin + (rangeMax - rangeMin) * random.nextDouble();
                s.setWeight(weight);
            });
        }
        return synapses;
    }
}
