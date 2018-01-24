package hr.fer.zemris.project.forecasting.gp.reproduction;

import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import hr.fer.zemris.project.forecasting.gp.selections.ISelection;
import hr.fer.zemris.project.forecasting.gp.tree.BinaryTree;
import hr.fer.zemris.project.forecasting.gp.values.ValueTypes;

import java.util.List;
import java.util.Random;

public class Mutation extends AReproduction<BinaryTree> {

    private int maxNodes;

    private int maxDepth;

    public Mutation(
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
        BinaryTree[] binaryTrees = new BinaryTree[1];
        BinaryTree   parent      = selection.getParent(population);
        BinaryTree   child       = parent.newLikeThis();

        BinaryTree tmpChild;
        while (true) {
            tmpChild = child.newLikeThis();
            int indexOfReplacedNode = getRandom().nextInt(tmpChild.getNodesSize());
            int depthOfReplacedNode = tmpChild.getNode(indexOfReplacedNode).getDepth();

            BinaryTree randomTree = BinaryTree.randomFactory(
                depthOfReplacedNode,
                getRandom().nextInt(maxDepth - depthOfReplacedNode + 1) + depthOfReplacedNode,
                getRandom(),
                getValueTypes()
            );
            tmpChild.replaceNode(indexOfReplacedNode, randomTree.getTopNode());
            tmpChild.setMaximumDepth();

            if (tmpChild.getNodesSize() <= maxNodes && tmpChild.getDepth() <= maxDepth) {
                break;
            }
        }
        tmpChild.evaluate(getTrain(), getTest());
        binaryTrees[0] = tmpChild;
        return binaryTrees;
    }
}
