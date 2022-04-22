package my.project.simulation.engine;

import my.project.gui.enums.SimulationState;

public interface IEngine {
    void run();

    void start();

    void pause();

    SimulationState getState();

    void setRefreshInterval(int refreshInterval);
}
