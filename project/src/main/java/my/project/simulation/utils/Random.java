package my.project.simulation.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Random {
    public static double random() {
        return Math.random();
    }

    public static int randInt(int maxVal) { // maxVal inclusive
        return (int)(Math.random() * (maxVal + 1));
    }

    public static int randInt(int minVal, int maxVal) { // maxVal inclusive
        return (int)(Math.random() * (maxVal - minVal + 1) + minVal);
    }

    public static <T> List<T> sample(List<T> list, int count) {
        List<T> result = new ArrayList<>();
        Collections.shuffle(result);
        for (int i = 0; i < count; i++) result.add(list.get(i));
        return result;
    }
}
