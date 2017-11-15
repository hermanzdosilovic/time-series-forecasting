package hr.fer.zemris.project.forecasting.tdnn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TDNN implements INeuralNetwork {

    private Layer inputLayer;
    private Layer outputLayer;
    private Layer[] hiddenLayers;
    private List<Synapse> synapses;

    public TDNN(int inputLayerSize, int... hiddenLayerSizes) {
        if (hiddenLayerSizes.length < 1) {
            throw new TDNNException("TDNN should have more than zero hidden layers.");
        }
        createInputLayer(inputLayerSize);
        createHiddenLayers(hiddenLayerSizes);
        createOutputLayer();

        synapses = createSynapses();
    }

    public double feedForward() {
        for (int i = 0, n = inputLayer.sizeOfLayer(); i < n; ++i) {
            inputLayer.getNeuronAtIndex(i).calculateOutputValue();
        }

        for (Layer hiddenLayer : hiddenLayers) {
            for (int i = 0, n = hiddenLayer.sizeOfLayer(); i < n; ++i) {
                hiddenLayer.getNeuronAtIndex(i).calculateOutputValue();
            }
        }

        for (int i = 0, n = outputLayer.sizeOfLayer(); i < n; ++i) {
            outputLayer.getNeuronAtIndex(i).calculateOutputValue();
        }
        return outputLayer.getNeuronAtIndex(0).getOutputValue();
    }

    public void setInput(double[] inputValues) {
        if (inputValues.length != inputLayer.sizeOfLayer()) {
            throw new TDNNException("Invalid lenght of input array");
        }

        for (int i = 0, n = inputValues.length; i < n; ++i) {
            InputNeuron inputNeuron = (InputNeuron) inputLayer.getNeuronAtIndex(i);
            inputNeuron.setInputValue(inputValues[i]);
        }
    }

    public double[] getWeights() {
        double[] weights = new double[synapses.size()];
        for (int i = 0, n = synapses.size(); i < n; ++i) {
            weights[i] = synapses.get(i).getWeight();
        }
        return weights;
    }

    public void setWeights(double[] weights) {
        if (weights.length != synapses.size()) {
            throw new TDNNException("Invalid weights array size.");
        }

        for (int i = 0, n = weights.length; i < n; ++i) {
            synapses.get(i).setWeight(weights[i]);
        }
    }

    public int getInputLayerSize() {
        return inputLayer.sizeOfLayer();
    }

    private List<Synapse> createSynapses() {
        List<Synapse> synapses = new ArrayList<>();
        synapses.addAll(connectLayers(inputLayer, hiddenLayers[0]));
        for (int i = 1, n = hiddenLayers.length; i < n - 1; ++i) {
            synapses.addAll(connectLayers(hiddenLayers[i], hiddenLayers[i + 1]));
        }
        synapses.addAll(connectLayers(hiddenLayers[hiddenLayers.length - 1], outputLayer));
        return synapses;
    }

    private void createInputLayer(int size) {
        inputLayer = new Layer();
        for (int i = 0; i < size; ++i) {
            inputLayer.addNeuron(new InputNeuron());
        }
    }


    private void createHiddenLayers(int... size) {
        hiddenLayers = new Layer[size.length];
        for (int i = 0, n = size.length; i < n; ++i) {
            hiddenLayers[i] = new Layer();
            for (int j = 0, m = size[i]; j < m; ++j) {
                hiddenLayers[i].addNeuron(new HiddenNeuron());
            }
        }
    }

    private void createOutputLayer() {
        outputLayer = new Layer();
        outputLayer.addNeuron(new OutputNeuron());
    }


    private static List<Synapse> connectLayers(Layer fromLayer, Layer toLayer) {
        List<Synapse> synapses = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < toLayer.sizeOfLayer(); ++i) {
            Neuron toNeuron = toLayer.getNeuronAtIndex(i);
            toNeuron.addWeight(new Synapse(BiasNeuron.getInstance(), toNeuron));
            for (int j = 0; j < fromLayer.sizeOfLayer(); ++j) {
                Neuron fromNeuron = fromLayer.getNeuronAtIndex(j);
                Synapse synapse = new Synapse(fromNeuron, toNeuron);
                toNeuron.addWeight(synapse);
                synapses.add(synapse);
            }
            //modify weights to starting values from interval [-2.4/m , 2.4/m]
            int m = toNeuron.getInputSynapses().size();
            double rangeMin = -2.4 / m;
            double rangeMax = 2.4 / m;
            toNeuron.getInputSynapses().forEach(s -> {
                double weight = rangeMin + (rangeMax - rangeMin) * random.nextDouble();
                s.setWeight(weight);
            });
        }
        return synapses;
    }
}
