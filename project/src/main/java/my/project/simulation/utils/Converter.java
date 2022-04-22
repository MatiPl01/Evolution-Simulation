package my.project.simulation.utils;

import java.util.List;
import java.util.stream.Collectors;

public class Converter {
    public static String genomeToString(List<Integer> genome) {
        return genome.stream().map(Object::toString).collect(Collectors.joining(""));
    }
}
