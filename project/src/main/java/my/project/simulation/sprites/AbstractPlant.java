package my.project.simulation.sprites;

import my.project.gui.simulation.sprites.GuiPlantSprite;
import my.project.simulation.maps.IMap;
import my.project.simulation.utils.Vector2D;

public abstract class AbstractPlant extends AbstractSprite {
    protected final int energy;

    public AbstractPlant(IMap map, Vector2D position, int energy) {
        super(map, position);
        this.energy = energy;
        addObserver(new GuiPlantSprite(this));
    }

    public int getEnergy() {
        return energy;
    }
}
