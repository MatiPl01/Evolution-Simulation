package my.project.simulation.utils;

import java.util.List;

public class Search {
    public static int binarySearchGE(List<Integer> arr, int l, int r, int target) {
        int m = (l + r) / 2;
        if (l == r) return r;
        if (target > arr.get(m)) return binarySearchGE(arr, m + 1, r, target);
        return binarySearchGE(arr, l, m, target);
    }
}
