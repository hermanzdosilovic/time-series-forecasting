package hr.fer.zemris.project.forecasting.gp.methods;

import hr.fer.zemris.project.forecasting.gp.tree.BinaryTree;
import hr.fer.zemris.project.forecasting.gp.values.ValueTypes;

import java.util.Random;

public class RampedHalfAndHalf {

    public static BinaryTree[] getPopulation(
        int populationSize,
        int maxDepth,
        ValueTypes valueTypes
    ) {
        int differentDepths = maxDepth - 1;
        if ((populationSize / differentDepths) % 2 != 0) {
            throw new IllegalArgumentException();
        }

        Random       rand               = new Random();
        BinaryTree[] pop                = new BinaryTree[populationSize];
        int          depthPopulaiton    = (int) (populationSize * (1.0 / differentDepths));
        int          depthForEachMethod = depthPopulaiton / 2;

        for (int i = 2; i <= maxDepth; i++) {
            int offset = (i - 2) * depthPopulaiton;
            for (int j = 0; j < depthForEachMethod; j++) {
                pop[j + offset] = BinaryTree.fullFactory(i, rand, valueTypes);
                pop[j + depthForEachMethod + offset] = BinaryTree.growFactory(i, rand, valueTypes);
            }
        }
        return pop;
    }

}
