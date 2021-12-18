package my.project.simulation.utils;

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
}
