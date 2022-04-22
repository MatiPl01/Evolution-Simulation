package my.project.gui.simulation.grid;

import my.project.gui.controllers.AbstractContainerController;
import my.project.gui.simulation.sprites.IGuiSprite;

public interface IBuilder {
    int getCellSize();

    void addSprite(IGuiSprite guiSprite);

    void removeSprite(IGuiSprite guiSprite);

    void initialize();

    void render();

    void setEventsController(AbstractContainerController controller);
}
