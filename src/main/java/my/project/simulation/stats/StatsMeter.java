package my.project.simulation.stats;

import javafx.application.Platform;
import my.project.gui.charts.ChartDrawer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StatsMeter {
    private static final String DEFAULT_PATH = "./output/";
    private static final String ERROR_MESSAGE = "Error writing statistics to the file";
    private static final String ANIMALS_COUNT_SERIES_NAME = "Number of animals";
    private static final String PLANTS_COUNT_SERIES_NAME = "Number of plants";
    private static final String AVERAGE_ENERGY_SERIES_NAME = "Average energy";
    private static final String AVERAGE_LIFESPAN_SERIES_NAME = "Average lifespan";
    private static final String AVERAGE_CHILDREN_SERIES_NAME = "Average number of children";

    private final String defaultFileName;

    private final List<StatsRecord> dailyStatistics = new ArrayList<>();
    private StatsRecord statsSum = new StatsRecord(0, 0, 0, 0, 0);
    private int dayNum = 0;
    private ChartDrawer chartDrawer;

    public StatsMeter(String defaultFileName) {
        this.defaultFileName = defaultFileName;
    }

    public void setChartDrawer(ChartDrawer chartDrawer) {
        this.chartDrawer = chartDrawer;
        chartDrawer.createSeries(ANIMALS_COUNT_SERIES_NAME,
                                 PLANTS_COUNT_SERIES_NAME,
                                 AVERAGE_ENERGY_SERIES_NAME,
                                 AVERAGE_LIFESPAN_SERIES_NAME,
                                 AVERAGE_CHILDREN_SERIES_NAME);
    }

    public void updateStatistics(long aliveAnimalsCount,
                                 long diedAnimalsCount,
                                 long plantsCount,
                                 Set<List<Integer>> dominantGenomes,
                                 double averageAliveEnergy,
                                 double averageDiedLifespan,
                                 double averageAliveChildren) {
        chartDrawer.update(dayNum,
                           aliveAnimalsCount,
                           plantsCount,
                           averageAliveEnergy,
                           averageDiedLifespan,
                           averageAliveChildren);

        // Save daily statistics
        StatsRecord record = new StatsRecord(aliveAnimalsCount,
                                             plantsCount,
                                             averageAliveEnergy,
                                             averageDiedLifespan,
                                             averageAliveChildren);


        dailyStatistics.add(record);
        // Update sums of statistic which will be used to calculate an average
        statsSum = statsSum.add(record);
        dayNum++;
    }

    public void updateTrackedAnimalDeath(long dayNum) {
        // TODO - send updates to GUI components (to implement)
    }

    public void updateTrackedAnimalDescendants(long descendantsCount) {
        // TODO - send updates to GUI components (to implement)
    }

    public void updateTrackedAnimalChildren(int childrenCount) {
        // TODO - send updates to GUI components (to implement)
    }

    private void writeLinesFromArray(BufferedWriter bw) throws IOException {
        for (StatsRecord record: dailyStatistics) bw.write(record.toString() + "\n");
    }

    private void writeAverageStats(BufferedWriter bw) throws IOException {
        String sep = statsSum.getSeparator();
        bw.write(1. * statsSum.aliveAnimalsCount() / dayNum + sep +
                1. * statsSum.plantsCount() / dayNum + sep +
                statsSum.averageAliveEnergy() / dayNum + sep +
                statsSum.averageDiedLifespan() / dayNum + sep +
                statsSum.averageAliveChildren() / dayNum);
    }

    public void generateCSVFile() {
        Platform.runLater(() -> {
            String outputPath = DEFAULT_PATH + defaultFileName;
            System.out.println("Generating statistics CSV file in: " + outputPath);

            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath));
               writeLinesFromArray(bw);
                writeAverageStats(bw);
                bw.close();
                System.out.println("Statistics file: " + outputPath + " was successfully generated");
            } catch (IOException e) {
                System.out.println(ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
