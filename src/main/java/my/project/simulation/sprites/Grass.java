package my.project.simulation.sprites;

import my.project.simulation.enums.MapArea;
import my.project.simulation.maps.IMap;
import my.project.simulation.utils.Random;
import my.project.simulation.utils.Vector2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grass extends AbstractSprite {
    private final String IMG_PATH;
    private final Vector2D position;
    private static final Map<MapArea, List<String>> images = new HashMap<>(){{
        put(MapArea.STEPPE, new ArrayList<>() {{
            add("/images/steppe/grass-1.png");
            add("/images/steppe/grass-2.png");
            add("/images/steppe/grass-3.png");
        }});
        put(MapArea.JUNGLE, new ArrayList<>() {{
            add("/images/jungle/bush-1.png");
            add("/images/jungle/bush-2.png");
        }});
    }};
    private static final String sign = "*";

    public Grass(IMap map, Vector2D position) {
        super(map);
        this.position = position;
        this.IMG_PATH = getRandomImagePath(map.getAreaType(position));
    }

    @Override
    public String toString() {
        return sign;
    }

    @Override
    public String getImagePath() {
        return IMG_PATH;
    }

    public Vector2D getPosition() {
        return position;
    }

    private String getRandomImagePath(MapArea areaType) {
        List<String> paths = images.get(areaType);
        return paths.get(Random.randInt(paths.size() - 1));
    }
}
