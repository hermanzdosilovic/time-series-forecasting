package hr.fer.zemris.project.forecasting.gp.selections;

import hr.fer.zemris.project.forecasting.gp.tree.BinaryTree;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Tournament implements ISelection<BinaryTree> {

    private Random random;

    private int numberOfParticipants;

    public Tournament(Random random, int numberOfParticipants) {
        this.random = random;
        this.numberOfParticipants = numberOfParticipants;
    }

    public Tournament(int numberOfParticipants) {
        this(new Random(), numberOfParticipants);
    }

    @Override
    public BinaryTree getParent(BinaryTree[] population) {
        Set<Integer> indexes    = new HashSet<>();
        BinaryTree[] candidates = new BinaryTree[numberOfParticipants];
        int          tmp        = 0;

        while (indexes.size() < numberOfParticipants) {
            int index = random.nextInt(population.length);
            if (!indexes.contains(index)) {
                candidates[tmp++] = population[index];
            }
            indexes.add(index);
        }

        return BinaryTree.findBest(candidates, BinaryTree.getTrainFitness);
    }
}
