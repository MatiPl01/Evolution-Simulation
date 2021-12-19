package my.project.simulation.data.structures;

import java.util.*;

class TreeNode<K, V> {
    private final Set<V> values = new HashSet<>();
    private final List<TreeNode<K, V>> branches;
    private final TreeNode<K, V> parent;
    private final K key;
    private long count = 0;

    TreeNode(int branchesCount, K key, TreeNode<K, V> parent) {
        this.key = key;
        this.parent = parent;
        this.branches = new ArrayList<>();
        for (int i = 0; i < branchesCount; i++) branches.add(null);
    }

    List<TreeNode<K, V>> getBranches() {
        return branches;
    }

    public long getCount() {
        return count;
    }

    public K getKey() {
        return key;
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
    private final Set<TreeNode<K, V>> maxCountLeaves = new HashSet<>();
    private final int branchesCount;
    private long maxCount = 0;

    public PrefixTree(List<K> possibleKeys) {
        this.mappedKeys = assignIndices(possibleKeys);
        this.branchesCount = possibleKeys.size();
        this.root = new TreeNode<>(branchesCount, null, null);
    }

    private Map<K, Integer> assignIndices(List<K> possibleKeys) {
        Map<K, Integer> map = new HashMap<>();
        int i = 0;
        for (K key: possibleKeys) map.put(key, i++);
        return map;
    }

    private int getIndex(K key) {
        return mappedKeys.get(key);
    }

    public void insert(List<K> keys, V value) {
        TreeNode<K, V> currNode = root;
        for (K key: keys) {
            int idx = getIndex(key);
            // We have to ensure that a node which is not a leaf
            // won't be stored in the maxCountLeaves Set
            maxCountLeaves.remove(currNode);

            TreeNode<K, V> nextNode = currNode.getBranches().get(idx);
            if (nextNode == null) {
                nextNode = new TreeNode<>(branchesCount, key, currNode);
                currNode.setBranch(idx, nextNode);
            } else {
                nextNode.incrementCount();
            }
            currNode = nextNode;
        }

        // Add value to the leaf node
        currNode.addValue(value);

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

    public void remove(List<K> keys) throws NoSuchElementException {
        TreeNode<K, V> currNode = root;
        for (K key: keys) {
            int idx = getIndex(key);

            TreeNode<K, V> nextNode = currNode.getBranches().get(idx);
            if (nextNode == null) {
                throw new NoSuchElementException("Keys: " + keys + " were not found in the PrefixTree");
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

    public Set<V> getValues(List<K> keys) throws NoSuchElementException {
        TreeNode<K, V> currNode = root;
        for (K key: keys) {
            int idx = getIndex(key);
            currNode = currNode.getBranches().get(idx);
            if (currNode == null) {
                throw new NoSuchElementException("Keys: " + keys + " were not found in the PrefixTree");
            }
        }
        return currNode.getValues();
    }

    public List<List<K>> getMaxCountKeys() {
        List<List<K>> result = new ArrayList<>();
        for (TreeNode<K, V> leaf: maxCountLeaves) {
            result.add(getLeafKeys(leaf));
        }
        return result;
    }

    public List<K> getLeafKeys(TreeNode<K, V> node) {
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
        Set<V> result = new HashSet<>();
        for (TreeNode<K, V> leaf: maxCountLeaves) {
            result.addAll(leaf.getValues());
        }
        return result;
    }
}
