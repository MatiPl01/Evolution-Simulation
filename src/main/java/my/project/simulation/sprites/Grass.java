package my.project.simulation.sprites;

import my.project.simulation.maps.IMap;
import my.project.simulation.utils.Random;
import my.project.simulation.utils.Vector2D;

import java.util.ArrayList;
import java.util.List;

public class Grass extends AbstractPlant {
    protected static final List<String> IMG_PATHS = new ArrayList<>() {{
        add("src/main/resources/images/steppe/grass-1.png");
        add("src/main/resources/images/steppe/grass-2.png");
        add("src/main/resources/images/steppe/grass-3.png");
    }};
    private static final String sign = "*";

    public Grass(IMap map, Vector2D position, int plantEnergy) {
        super(map, position, plantEnergy);
    }

    @Override
    public String toString() {
        return sign;
    }

    protected String getRandomImagePath() {
        return IMG_PATHS.get(Random.randInt(IMG_PATHS.size() - 1));
    }
}
