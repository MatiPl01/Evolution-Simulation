package my.project.gui.simulation.sprites;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import my.project.gui.simulation.grid.IBuilder;
import my.project.simulation.sprites.ISprite;
import my.project.simulation.utils.IObserver;
import my.project.simulation.utils.Vector2D;

abstract class AbstractGuiSprite implements IObserver, IGuiSprite {
    protected final ISprite sprite;
    protected final IBuilder gridBuilder;
    protected final VBox mainBox = new VBox();
    protected final StackPane spriteBox = new StackPane();
    protected final ImageView imageView = new ImageView();

    public AbstractGuiSprite(ISprite sprite) {
        this.sprite = sprite;
        this.gridBuilder = sprite.getMap().getGridBuilder();
        initialize();
    }

    @Override
    public void initialize() {
        Image image = getImage();
        if (image != null) {
            imageView.setImage(image);
            imageView.setFitHeight(gridBuilder.getCellSize());
            imageView.setFitWidth(gridBuilder.getCellSize());
            imageView.setPreserveRatio(true);
            spriteBox.getChildren().add(imageView);
            mainBox.getChildren().add(spriteBox);
        } else {
            Label label = new Label(sprite.toString());
            label.setFont(new Font(gridBuilder.getCellSize() / 1.2));
            VBox vBox = new VBox(label);
            label.setStyle("-fx-text-fill: #419c11");
            vBox.setAlignment(Pos.CENTER);
            mainBox.getChildren().add(vBox);
        }
    }

    @Override
    public void addSprite(ISprite sprite) {
        gridBuilder.addSprite(this);
    }

    @Override
    public ISprite getSprite() { return sprite; }

    @Override
    public void removeSprite(ISprite sprite) {
        gridBuilder.removeSprite(this);
    }

    public Vector2D getPosition() {
        return sprite.getPosition();
    }

    public Node getNode() {
        return mainBox;
    }

    abstract Image getImage();
}
