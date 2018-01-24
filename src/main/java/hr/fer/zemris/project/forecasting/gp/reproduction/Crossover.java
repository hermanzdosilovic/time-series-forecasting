package hr.fer.zemris.project.forecasting.gp.reproduction;

import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import hr.fer.zemris.project.forecasting.gp.selections.ISelection;
import hr.fer.zemris.project.forecasting.gp.tree.BinaryTree;
import hr.fer.zemris.project.forecasting.gp.tree.Node;
import hr.fer.zemris.project.forecasting.gp.values.ValueTypes;

import java.util.List;
import java.util.Random;

public class Crossover extends AReproduction<BinaryTree> {

    private int maxNodes;
    private int maxDepth;

    public Crossover(
        Random random,
        List<DatasetEntry> train,
        List<DatasetEntry> test,
        ValueTypes valueTypes,
        int maxNodes,
        int maxDepth
    ) {
        super(random, train, test, valueTypes);
        this.maxNodes = maxNodes;
        this.maxDepth = maxDepth;
    }

    @Override
    public BinaryTree[] getChildren(
        BinaryTree[] population,
        ISelection<BinaryTree> selection
    ) {
        BinaryTree[] binaryTrees = new BinaryTree[2];
        BinaryTree   parent1, parent2, child1, child2;
        while (true) {
            parent1 = selection.getParent(population);
            parent2 = selection.getParent(population);

            child1 = parent1.newLikeThis();
            child2 = parent2.newLikeThis();

            int  index1     = getRandom().nextInt(child1.getNodesSize());
            Node node1      = child1.getNode(index1).newLikeThis(null);
            int  node1Depth = node1.getDepth();

            int  index2     = getRandom().nextInt(child2.getNodesSize());
            Node node2      = child2.getNode(index2).newLikeThis(null);
            int  node2Depth = node2.getDepth();

            node1.setNewDepth(node2Depth);
            node2.setNewDepth(node1Depth);

            child1.replaceNode(index1, node2);
            child2.replaceNode(index2, node1);

            if (child1.getNodesSize() <= maxNodes && child2.getNodesSize() <= maxNodes) {
                if (child1.getDepth() <= maxDepth && child2.getDepth() <= maxDepth) {
                    break;
                }
            }
        }

        child1.evaluate(getTrain(), getTest());
        child2.evaluate(getTrain(), getTest());

        binaryTrees[0] = child1;
        binaryTrees[1] = child2;
        return binaryTrees;
    }
}
