package my.project.simulation.datastructures;

import java.util.*;

class Node<T> {
    private final List<Node<T>> branches;
    private final Node<T> parent;
    private final T value;
    private long count = 0;

    Node(int branchesCount, T value, Node<T> parent) {
        this.value = value;
        this.parent = parent;
        this.branches = new ArrayList<>();
        for (int i = 0; i < branchesCount; i++) branches.add(null);
    }

    public List<Node<T>> getBranches() {
        return branches;
    }

    public void setBranch(int idx, Node<T> nextNode) {
        branches.set(idx, nextNode);
    }

    public void incrementCount() {
        count++;
    }

    public void decrementCount() {
        count--;
    }

    public long getCount() {
        return count;
    }

    public T getValue() {
        return value;
    }

    public Node<T> getParent() {
        return parent;
    }
}

public class PrefixTree<T> { // inserted data must be an iterable
    private final Node<T> root;
    private final Map<T, Integer> mappedValues;
    private final Set<Node<T>> maxCountLeaves = new HashSet<>();
    private final int branchesCount;
    private long maxCount = 0;

    public PrefixTree(List<T> possibleValues) {
        this.mappedValues = assignIndices(possibleValues);
        this.branchesCount = possibleValues.size();
        this.root = new Node<>(branchesCount, null, null);
    }

    private Map<T, Integer> assignIndices(List<T> possibleValues) {
        Map<T, Integer> map = new HashMap<>();
        int i = 0;
        for (T value: possibleValues) map.put(value, i++);
        return map;
    }

    private int getIndex(T value) {
        return mappedValues.get(value);
    }

    public void insert(List<T> data) {
        Node<T> currNode = root;
        for (T value: data) {
            int idx = getIndex(value);
            // We have to ensure that a node which is not a leaf
            // won't be stored in the maxCountLeaves Set
            maxCountLeaves.remove(currNode);

            Node<T> nextNode = currNode.getBranches().get(idx);
            if (nextNode == null) {
                nextNode = new Node<>(branchesCount, value, currNode);
                currNode.setBranch(idx, nextNode);
            } else {
                nextNode.incrementCount();
            }
            currNode = nextNode;
        }

        // Update the Set of leaves which have max count
        long currCount = currNode.getCount();
        if (currCount > maxCount || maxCountLeaves.size() == 0) {
            maxCount = currCount;
            maxCountLeaves.clear();
        }
        if (currCount >= maxCount) {
            maxCountLeaves.add(currNode);
        }
    }

    public void remove(List<T> data) throws NoSuchElementException {
        Node<T> currNode = root;
        for (T value: data) {
            int idx = getIndex(value);

            Node<T> nextNode = currNode.getBranches().get(idx);
            if (nextNode == null) {
                throw new NoSuchElementException("Data: " + data + " was not found in the PrefixTree");
            } else {
                nextNode.decrementCount();
                // Remove a subtree if the next node is the last node (counter dropped to 0)
                if (nextNode.getCount() == 0) {
                    currNode.setBranch(idx, null);
                    return;
                }
            }
            currNode = nextNode;
        }
    }

    public List<List<T>> getMaxCountData() {
        List<List<T>> result = new ArrayList<>();
        for (Node<T> leaf: maxCountLeaves) {
            result.add(getLeafParentsData(leaf));
        }
        return result;
    }

    public List<T> getLeafParentsData(Node<T> node) {
        Node<T> currNode = node;
        List<T> result = new ArrayList<>();

        while (currNode.getValue() != null) {
            result.add(currNode.getValue());
            currNode = currNode.getParent();
        }
        Collections.reverse(result);
        return result;
    }
}
