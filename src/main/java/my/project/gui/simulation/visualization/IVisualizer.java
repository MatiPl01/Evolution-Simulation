package my.project.gui.simulation.visualization;

public interface IVisualizer {

    void startVisualization();

    void pauseVisualization();

    boolean isPaused();

    void showNewFrame();

    void drawGrid();
}
