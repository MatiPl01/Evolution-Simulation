package my.project.gui.simulation.sprites;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import my.project.gui.simulation.visualization.GridBuilder;
import my.project.simulation.sprites.ISprite;
import my.project.simulation.utils.IObserver;
import my.project.simulation.utils.Vector2D;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


abstract class AbstractGuiSprite implements IObserver, IGuiSprite {
    protected final ISprite sprite;
    protected final GridBuilder gridBuilder;
    protected final VBox vBox = new VBox();
    protected final ImageView imageView = new ImageView();

    public AbstractGuiSprite(ISprite sprite) {
        this.sprite = sprite;
        this.gridBuilder = sprite.getMap().getGridBuilder();
        initialize();
    }

    @Override
    public void initialize() {
        try {
            Image image = new Image(new FileInputStream(sprite.getImagePath()));
            imageView.setImage(image);
            imageView.setFitHeight(gridBuilder.getCellSize());
            imageView.setFitWidth(gridBuilder.getCellSize());
            imageView.setPreserveRatio(true);
            vBox.getChildren().add(imageView);
        } catch (FileNotFoundException e) {
            vBox.getChildren().add(new Label(sprite.toString()));
        }
    }

    @Override
    public void addSprite(ISprite sprite) {
        gridBuilder.addSprite(this);
    }

    @Override
    public void removeSprite(ISprite sprite) {
        gridBuilder.removeSprite(this);
    }

    public Vector2D getPosition() {
        return sprite.getPosition();
    }

    public Node getNode() {
        return vBox;
    }
}
