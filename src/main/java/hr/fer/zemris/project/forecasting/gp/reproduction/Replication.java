package hr.fer.zemris.project.forecasting.gp.reproduction;

import com.dosilovic.hermanzvonimir.ecfjava.util.DatasetEntry;
import hr.fer.zemris.project.forecasting.gp.selections.ISelection;
import hr.fer.zemris.project.forecasting.gp.tree.BinaryTree;
import hr.fer.zemris.project.forecasting.gp.values.ValueTypes;

import java.util.List;
import java.util.Random;

public class Replication extends AReproduction<BinaryTree> {


    public Replication(
        Random random,
        List<DatasetEntry> train,
        List<DatasetEntry> test,
        ValueTypes valueTypes
    ) {
        super(random, train, test, valueTypes);
    }

    @Override
    public BinaryTree[] getChildren(
        BinaryTree[] population,
        ISelection<BinaryTree> selection
    ) {
        BinaryTree[] binaryTrees = new BinaryTree[1];
        BinaryTree   child       = selection.getParent(population).newLikeThis();
        child.evaluate(getTrain(), getTest());
        binaryTrees[0] = child;
        return binaryTrees;
    }
}
