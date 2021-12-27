package my.project.gui.charts;

import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;

public class ChartDrawer {
    private static int MAX_POINTS = 200;
    private final LineChart<Number, Number> lineChart;
    private final List<XYChart.Series<Number, Number>> seriesList = new ArrayList<>();

    public ChartDrawer(AnchorPane chartParentPane,
                       String chartTitle,
                       String xAxisName,
                       String yAxisName,
                       Side legendSide) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(xAxisName);
        xAxis.setAnimated(false);
        yAxis.setLabel(yAxisName);
        xAxis.setForceZeroInRange(false);
        yAxis.setAnimated(false);
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setLegendSide(legendSide);
        lineChart.setCreateSymbols(false);
        if (chartTitle != null) lineChart.setTitle(chartTitle);
        lineChart.setAnimated(false);
        AnchorPane.setBottomAnchor(lineChart, 0.0);
        AnchorPane.setTopAnchor(lineChart, 0.0);
        AnchorPane.setLeftAnchor(lineChart, 0.0);
        AnchorPane.setRightAnchor(lineChart, 0.0);
        chartParentPane.getChildren().add(lineChart);
    }

    public void createSeries(String ...seriesNames) {
        for (String seriesName: seriesNames) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(seriesName);
            lineChart.getData().add(series);
            seriesList.add(series);
        }
    }

    // yValues must be passed in the same order as seriesNames corresponding to them
    public void update(Number xValue, Number ...yValues) {
        for (int i = 0; i < Math.min(yValues.length, seriesList.size()); i++) {
            // If there is no new value for a particular series
            if (yValues[i] == null) continue;
            XYChart.Series<Number, Number> series = seriesList.get(i);
            series.getData().add(new XYChart.Data<>(xValue, yValues[i]));
        }
        clearExcessivePoints();
    }

    private void clearExcessivePoints() {
        for (XYChart.Series<Number, Number> series: seriesList) {
            if (series.getData().size() > MAX_POINTS) series.getData().remove(0);
        }
    }
}
