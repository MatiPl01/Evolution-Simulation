package my.project.gui.controllers;

import javafx.geometry.Side;
import javafx.scene.layout.AnchorPane;
import my.project.gui.charts.ChartDrawer;
import my.project.simulation.engine.IEngine;
import my.project.simulation.engine.SimulationEngine;
import my.project.simulation.maps.IMap;

public class AbstractContainerController {
    private AnchorPane simulationBox;
    private AnchorPane chartBox;
    protected Side legendSide;

    protected void setSimulationBox(AnchorPane simulationBox) {
        this.simulationBox = simulationBox;
    }

    protected void setChartBox(AnchorPane chartBox) {
        this.chartBox = chartBox;
    }

    public void launch(IMap map) {
        map.setChartDrawer(new ChartDrawer(this.chartBox, null, "Day", "Count", legendSide));
        IEngine engine = new SimulationEngine(map, simulationBox);
        Thread engineThread = new Thread((Runnable) engine);
        engineThread.start();
    }
}
