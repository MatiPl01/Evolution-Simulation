package my.project.simulation.datastructures;

import java.util.*;

class TreeNode<K, V> {
    private final Set<V> values = new HashSet<>();
    private final List<TreeNode<K, V>> branches;
    private final TreeNode<K, V> parent;
    private final K nodeKey;
    private int count = 0;

    TreeNode(int branchesCount, K nodeKey, TreeNode<K, V> parent) {
        this.nodeKey = nodeKey;
        this.parent = parent;
        this.branches = new ArrayList<>();
        for (int i = 0; i < branchesCount; i++) branches.add(null);
    }

    List<TreeNode<K, V>> getBranches() {
        return branches;
    }

    public int getCount() {
        return count;
    }

    public K getKey() {
        return nodeKey;
    }

    public Set<V> getValues() {
        return values;
    }

    public void addValue(V value) {
        values.add(value);
    }

    public void removeValue(V value) {
        values.remove(value);
    }

    public TreeNode<K, V> getParent() {
        return parent;
    }

    void setBranch(int idx, TreeNode<K, V> nextNode) {
        branches.set(idx, nextNode);
    }

    void incrementCount() {
        count++;
    }

    void decrementCount() {
        count--;
    }
}

public class PrefixTree<K, V> { // inserted data must be a list
    private final TreeNode<K, V> root;
    private final Map<K, Integer> mappedKeys;

    private final List<Set<TreeNode<K, V>>> leavesNodesSets = new ArrayList<>();
    private final int branchesCount;

    public PrefixTree(List<K> possibleNodeKeys) {
        this.mappedKeys = assignIndices(possibleNodeKeys);
        this.branchesCount = possibleNodeKeys.size();
        this.root = new TreeNode<>(branchesCount, null, null);
    }

    private Map<K, Integer> assignIndices(List<K> possibleNodeKeys) {
        Map<K, Integer> map = new HashMap<>();
        int i = 0;
        for (K nodeKey: possibleNodeKeys) map.put(nodeKey, i++);
        return map;
    }

    private int getIndex(K key) {
        return mappedKeys.get(key);
    }

    public void add(List<K> keys, V value) {
        TreeNode<K, V> currNode = root;
        for (K key: keys) {
            int idx = getIndex(key);
            TreeNode<K, V> nextNode = currNode.getBranches().get(idx);
            if (nextNode == null) {
                nextNode = new TreeNode<>(branchesCount, key, currNode);
                currNode.setBranch(idx, nextNode);
            }
            nextNode.incrementCount();
            currNode = nextNode;
        }

        // Add value to the leaf node
        currNode.addValue(value);

        // Update the leavesValues array
        int count = currNode.getCount();
        while (count >= leavesNodesSets.size()) leavesNodesSets.add(new HashSet<>());
        leavesNodesSets.get(count - 1).remove(currNode);
        leavesNodesSets.get(count).add(currNode);
    }

    public void remove(List<K> key, V value) throws NoSuchElementException {
        TreeNode<K, V> currNode = root;
        TreeNode<K, V> leaf = getLeafNode(key);
        // Remove the leaf node from a proper set in leavesNodesSets array
        leaf.removeValue(value);
        Set<TreeNode<K, V>> leafSet = leavesNodesSets.get(leaf.getCount());
        leafSet.remove(leaf);
        // Remove the whole set, if there are no more leaves remaining in the set
        // and set is last one set in an array
        // Update counts of nodes in a tree
        for (K nodeKey: key) {
            int idx = getIndex(nodeKey);
            TreeNode<K, V> nextNode = currNode.getBranches().get(idx);
            nextNode.decrementCount();
            if (nextNode.getCount() == 0) {
                currNode.setBranch(idx, null);
                return;
            }
            currNode = nextNode;
        }
        // Add a leaf node back to the leavesNodesSets on the updated position
        // if number of key occurrences is still grater than 0.
        leavesNodesSets.get(leaf.getCount()).add(leaf);
        // Check if the last Set in leavesNodeSets is empty
        int lastSetIndex = leavesNodesSets.size() - 1;
        Set<TreeNode<K, V>> leavesNodesSet = leavesNodesSets.get(lastSetIndex);
        if (leavesNodesSet.size() == 0) leavesNodesSets.remove(lastSetIndex);
    }

    public Set<V> getValues(List<K> key) throws NoSuchElementException {
        TreeNode<K, V> currNode = root;
        for (K nodeKey: key) {
            int idx = getIndex(nodeKey);
            currNode = currNode.getBranches().get(idx);
            if (currNode == null) throwException(key);
        }
        return currNode.getValues();
    }

    public Set<List<K>> getMaxCountKeys() {
        Set<List<K>> keys = new HashSet<>();
        for (TreeNode<K, V> leaf: leavesNodesSets.get(leavesNodesSets.size() - 1)) {
            keys.add(getLeafKey(leaf));
        }
        return keys;
    }

    private List<K> getLeafKey(TreeNode<K, V> node) {
        TreeNode<K, V> currNode = node;
        List<K> result = new ArrayList<>();

        while (currNode.getKey() != null) {
            result.add(currNode.getKey());
            currNode = currNode.getParent();
        }
        Collections.reverse(result);
        return result;
    }

    public Set<V> getMaxCountValues() {
        Set<V> values = new HashSet<>();
        for (TreeNode<K, V> leaf: leavesNodesSets.get(leavesNodesSets.size() - 1)) {
            values.addAll(leaf.getValues());
        }
        return values;
    }

    private TreeNode<K, V> getLeafNode(List<K> key) throws NoSuchElementException {
        TreeNode<K, V> currNode = root;
        for (K nodeKey: key) {
            int idx = getIndex(nodeKey);
            currNode = currNode.getBranches().get(idx);
            if (currNode == null) throwException(key);
        }
        return currNode;
    }

    private void throwException(List<K> key) {
        throw new NoSuchElementException("Key: " + key + " was not found in the PrefixTree");
    }
}
