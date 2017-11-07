package hr.fer.zemris.projekt.predictions.tdnn;

import java.util.List;

public class SigmoidNeuron extends Neuron{

    @Override
    public void calculateOutputValue() {
        double sum = 0.;
        for(Weight w : super.inputWeights){
            sum += w.getInputNeuron().getOutputValue()*w.getWeight();
        }

        super.outputValue = 1./(1.+Math.exp(-sum));
    }
}
