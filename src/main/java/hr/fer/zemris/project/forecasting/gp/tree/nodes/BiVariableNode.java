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
import java.util.function.DoubleBinaryOperator;

public class BiVariableNode extends Node {

    private DoubleBinaryOperator value;

    private Node leftNode;

    private Node rightNode;

    public BiVariableNode(
        int totalDepth,
        int depth,
        Node parent,
        DoubleBinaryOperator value,
        Node leftNode,
        Node rightNode
    ) {
        super(totalDepth, depth, parent);
        this.value = value;
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }

    public BiVariableNode(int totalDepth, int depth, Node parent, DoubleBinaryOperator value) {
        this(totalDepth, depth, parent, value, null, null);
    }

    @Override
    public void setNewDepth(int depth) {
        setDepth(depth);
        leftNode.setNewDepth(depth + 1);
        rightNode.setNewDepth(depth + 1);
    }

    @Override public TreeItem<String> asTreeItem(boolean expand) {
        TreeItem<String> parent = new TreeItem<>(value.toString() + String.format(" (depth: %d)", getDepth()), BinaryTree.LOG_ICON.get());
        parent.setExpanded(expand);
        parent.getChildren().add(leftNode.asTreeItem(expand));
        parent.getChildren().add(rightNode.asTreeItem(expand));
        return parent;
    }

    public void setLeftNode(Node leftNode) {
        this.leftNode = leftNode;
    }

    public void setRightNode(Node rightNode) {
        this.rightNode = rightNode;
    }

    @Override
    public List<Node> getChildren() {
        List<Node> tmpList = new ArrayList<>(2);
        tmpList.add(leftNode);
        tmpList.add(rightNode);
        return tmpList;
    }

    @Override public void addChildren(
        Random random,
        FiveParametarInterface<Integer, Integer, Random, Node, ValueTypes, Node> getNode,
        ThreeParametarInterface<Node, Random, ValueTypes> buildMethod,
        ValueTypes valueTypes
    ) {
        leftNode = getNode.apply(getTotalDepth(), getDepth() + 1, random, this, valueTypes);
        rightNode = getNode.apply(getTotalDepth(), getDepth() + 1, random, this, valueTypes);
        buildMethod.apply(leftNode, random, valueTypes);
        buildMethod.apply(rightNode, random, valueTypes);
    }

    @Override
    public Node newLikeThis(Node parent) {
        BiVariableNode biVariableNode = new BiVariableNode(
            getTotalDepth(),
            getDepth(),
            parent,
            value
        );

        Node rightChild = rightNode.newLikeThis(biVariableNode);
        Node leftChild  = leftNode.newLikeThis(biVariableNode);

        biVariableNode.setLeftNode(leftChild);
        biVariableNode.setRightNode(rightChild);

        return biVariableNode;
    }

    @Override public void replaceChild(Node toBeReplaced, Node replacement) {
        if (leftNode == toBeReplaced) {
            leftNode = replacement;
        } else if (rightNode == toBeReplaced) {
            rightNode = replacement;
        } else {
            throw new IllegalArgumentException("Node not found");
        }
    }

    @Override
    public double getOutput(double[] input) {
        return value.applyAsDouble(
            leftNode.getOutput(input),
            rightNode.getOutput(input)
        );
    }

    @Override
    public String toString() {
        StringJoiner sj = asStringJoiner(value);
        sj.add(leftNode.toString());
        sj.add(rightNode.toString());
        return sj.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BiVariableNode that = (BiVariableNode) o;

        if (!value.equals(that.value)) {
            return false;
        }
        if (!leftNode.equals(that.leftNode)) {
            return false;
        }
        return rightNode.equals(that.rightNode);
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + leftNode.hashCode();
        result = 31 * result + rightNode.hashCode();
        return result;
    }
}
