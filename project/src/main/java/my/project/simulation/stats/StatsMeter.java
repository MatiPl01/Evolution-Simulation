package my.project.simulation.stats;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import my.project.gui.charts.ChartDrawer;
import my.project.gui.utils.DialogUtils;
import my.project.gui.utils.InfoLogger;
import my.project.simulation.maps.IMap;
import my.project.simulation.sprites.Animal;
import my.project.simulation.utils.Converter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StatsMeter {
    private static final int INFO_DISPLAY_TIME = 5000; // ms
    private static final String DEFAULT_PATH = "./";
    private static final String ERROR_MESSAGE = "Error writing statistics to the file";
    private static final String ANIMALS_COUNT_SERIES_NAME = "Number of animals";
    private static final String PLANTS_COUNT_SERIES_NAME = "Number of plants";
    private static final String AVERAGE_ENERGY_SERIES_NAME = "Average energy";
    private static final String AVERAGE_LIFESPAN_SERIES_NAME = "Average lifespan";
    private static final String AVERAGE_CHILDREN_SERIES_NAME = "Average number of children";

    private final IMap map;
    private final String defaultFileName;

    private final List<StatsRecord> dailyStatistics = new ArrayList<>();
    private StatsRecord statsSum = new StatsRecord(0, 0, 0, 0, 0);
    private int dayNum = 0;

    private ChartDrawer chartDrawer;
    private InfoLogger infoLogger;
    private VBox dominantGenomesBox;
    private Label dominantGenomesAnimalsCountLabel;
    private Label allAnimalsCountLabel;
    private int dominantGenomesAnimalsCount = 0;

    public StatsMeter(IMap map, String defaultFileName) {
        this.map = map;
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

    public void setDominantGenomesBox(VBox dominantGenomesBox) {
        this.dominantGenomesBox = dominantGenomesBox;
    }

    public void setDominantGenomesCountLabel(Label label) {
        dominantGenomesAnimalsCountLabel = label;
    }

    public void setAllAnimalsCountLabel(Label label) {
        allAnimalsCountLabel = label;
    }

    public void setInfoLogger(InfoLogger infoLogger) {
        this.infoLogger = infoLogger;
    }

    public void updateStatistics(long aliveAnimalsCount,
                                 long plantsCount,
                                 Set<List<Integer>> dominantGenomes,
                                 double averageAliveEnergy,
                                 double averageDiedLifespan,
                                 double averageAliveChildren) {
        // Update the chart
        chartDrawer.update(dayNum,
                           aliveAnimalsCount,
                           plantsCount,
                           averageAliveEnergy,
                           averageDiedLifespan,
                           averageAliveChildren);

        // Update the dominant genomes box
        this.updateDominantGenomes(dominantGenomes);
        allAnimalsCountLabel.setText(String.valueOf(aliveAnimalsCount));

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

    private void updateDominantGenomes(Set<List<Integer>> dominantGenomes) {
        dominantGenomesBox.getChildren().clear();
        dominantGenomesAnimalsCount = 0;
        for (List<Integer> genome: dominantGenomes) {
            VBox genomeBox = createGenomeBox(genome);
            dominantGenomesBox.getChildren().add(genomeBox);
        }
        dominantGenomesAnimalsCountLabel.setText(String.valueOf(dominantGenomesAnimalsCount));
    }

    private VBox createGenomeBox(List<Integer> genome) {
        VBox vBox = new VBox();
        Label genomeLabel = new Label(Converter.genomeToString(genome));
        genomeLabel.setPadding(new Insets(0, 0, 2.5, 0));
        Label textLabel = new Label("Animals with this genome");
        textLabel.setPadding(new Insets(0, 0, 5, 0));
        HBox hBox = new HBox();
        Label noLabel = new Label("No.");
        noLabel.setPrefWidth(25);
        Label idLabel = new Label("ID");
        idLabel.setPrefWidth(50);
        Label positionLabel = new Label("Position");
        Separator vSeparator1 = new Separator(Orientation.VERTICAL);
        Separator vSeparator2 = new Separator(Orientation.VERTICAL);
        vSeparator1.setPadding(new Insets(0, 5, 0, 5));
        vSeparator2.setPadding(new Insets(0, 5, 0, 5));
        hBox.getChildren().addAll(noLabel, vSeparator1, idLabel, vSeparator2, positionLabel);
        Separator hSeparator1 = new Separator(Orientation.HORIZONTAL);
        Separator hSeparator2 = new Separator(Orientation.HORIZONTAL);
        hSeparator1.setPadding(new Insets(2.5, 0, 2.5, 0));
        hSeparator2.setPadding(new Insets(0, 0, 5, 0));
        VBox animalsVBox = new VBox();
        vBox.getChildren().addAll(genomeLabel, hSeparator1, textLabel, hSeparator2, hBox, animalsVBox);

        int i = 1;
        Set<Animal> animalsWithGenome = map.getAnimalsWithGenome(genome);
        for (Animal animal: animalsWithGenome) {
            vBox.getChildren().add(createAnimalGenomeBox(i++, animal));
        }
        dominantGenomesAnimalsCount += animalsWithGenome.size();
        return vBox;
    }

    private HBox createAnimalGenomeBox(int ordinalNumber, Animal animal) {
        HBox hBox = new HBox();
        hBox.setSpacing(15);
        Label noLabel = new Label(String.valueOf(ordinalNumber));
        noLabel.setPrefWidth(25);
        Label idLabel = new Label(String.valueOf(animal.getID()));
        idLabel.setPrefWidth(50);
        Label positionLabel = new Label(String.valueOf(animal.getDisplayedPosition()));
        hBox.getChildren().addAll(noLabel, idLabel, positionLabel);
        return hBox;
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
        String outputPath = DialogUtils.textInputDialog("Save file", "Choose where to save statistics file",
                "Enter a path to the file", DEFAULT_PATH + defaultFileName);
        if (outputPath == null) return;
        Platform.runLater(() -> {
            infoLogger.displayInfo("Saving stats file to " + outputPath, INFO_DISPLAY_TIME);
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath));
               writeLinesFromArray(bw);
                writeAverageStats(bw);
                bw.close();
            } catch (IOException e) {
                System.out.println(ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
