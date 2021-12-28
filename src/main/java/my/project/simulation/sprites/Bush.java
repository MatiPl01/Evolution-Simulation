package my.project.simulation.sprites;

import my.project.simulation.maps.IMap;
import my.project.simulation.utils.Vector2D;

public class Bush extends AbstractPlant {
    private static final String sign = "#";

    public Bush(IMap map, Vector2D position, int plantEnergy) {
        super(map, position, plantEnergy);
    }

    @Override
    public String toString() {
        return sign + " " + getDisplayedPosition();
    }
}
