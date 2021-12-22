package my.project.gui.simulation.grid;

import my.project.gui.simulation.sprites.IGuiSprite;

public interface IBuilder {
    int getCellSize();

    void buildGrid();

    void addSprite(IGuiSprite guiSprite);

    void removeSprite(IGuiSprite guiSprite);

    void renderGrid();

    void loadBackground();
}
