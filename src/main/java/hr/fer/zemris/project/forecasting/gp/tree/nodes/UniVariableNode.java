package hr.fer.zemris.project.forecasting.gp.tree.nodes;

import hr.fer.zemris.project.forecasting.gp.tree.BinaryTree;
import hr.fer.zemris.project.forecasting.gp.tree.Node;
import hr.fer.zemris.project.forecasting.gp.util.functionalInterface.FiveParametarInterface;
import hr.fer.zemris.project.forecasting.gp.util.functionalInterface.FourParametarInterface;
import hr.fer.zemris.project.forecasting.gp.util.functionalInterface.ThreeParametarInterface;
import hr.fer.zemris.project.forecasting.gp.values.ValueTypes;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.DoubleUnaryOperator;

public class UniVariableNode extends Node {

    private DoubleUnaryOperator value;

    private Node child;

    public UniVariableNode(int totalDepth, int depth, Node parent, DoubleUnaryOperator value, Node child) {
        super(totalDepth, depth, parent);
        this.value = value;
        this.child = child;
    }

    public UniVariableNode(int totalDepth, int depth, Node parent, DoubleUnaryOperator value) {
        this(totalDepth, depth, parent, value, null);
    }

    @Override
    public void setNewDepth(int depth) {
        setDepth(depth);
        child.setNewDepth(depth + 1);
    }

    @Override public TreeItem<String> asTreeItem(boolean expand) {
        TreeItem<String> parent = new TreeItem<>(value.toString(), BinaryTree.LOG_ICON.get());
        parent.setExpanded(expand);
        parent.getChildren().add(child.asTreeItem(expand));
        return parent;
    }

    public void setChild(Node child) {
        this.child = child;
    }

    @Override
    public List<Node> getChildren() {
        List<Node> tmpList = new ArrayList<>(1);
        tmpList.add(child);
        return tmpList;
    }

    @Override public void addChildren(
        Random random,
        FiveParametarInterface<Integer, Integer, Random, Node, ValueTypes, Node> getNode,
        ThreeParametarInterface<Node, Random, ValueTypes> buildMethod,
        ValueTypes valueTypes
    ) {
        child = getNode.apply(getTotalDepth(), getDepth() + 1, random, this, valueTypes);
        buildMethod.apply(child, random, valueTypes);
    }

    @Override
    public Node newLikeThis(Node parent) {
        UniVariableNode uniVariableNode = new UniVariableNode(
            getTotalDepth(),
            getDepth(),
            parent,
            value
        );
        Node tmpChild = child.newLikeThis(uniVariableNode);
        uniVariableNode.setChild(tmpChild);
        return uniVariableNode;
    }

    @Override public void replaceChild(Node toBeReplaced, Node replacement) {
        if (child == toBeReplaced) {
            child = replacement;
        } else {
            throw new IllegalArgumentException("Node not found");
        }
    }

    @Override
    public double getOutput(double[] input) {
        return value.applyAsDouble(child.getOutput(input));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UniVariableNode that = (UniVariableNode) o;

        if (!value.equals(that.value)) {
            return false;
        }
        return child.equals(that.child);
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + child.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringJoiner sj = asStringJoiner(value);
        sj.add(child.toString());
        return sj.toString();
    }
}
