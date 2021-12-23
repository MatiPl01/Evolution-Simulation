package my.project.gui.simulation.sprites;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import my.project.gui.utils.ImageLoader;
import my.project.simulation.enums.MapArea;
import my.project.simulation.sprites.ISprite;
import my.project.simulation.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiPlantSprite extends AbstractGuiSprite {
    private static final Map<MapArea, List<Image>> IMAGES = new HashMap<>() {{
        put(MapArea.STEPPE, new ArrayList<>() {{
            add(ImageLoader.loadImage("src/main/resources/images/steppe/grass-1.png"));
            add(ImageLoader.loadImage("src/main/resources/images/steppe/grass-2.png"));
            add(ImageLoader.loadImage("src/main/resources/images/steppe/grass-3.png"));
        }});
        put(MapArea.JUNGLE, new ArrayList<>() {{
            add(ImageLoader.loadImage("src/main/resources/images/jungle/bush-1.png"));
            add(ImageLoader.loadImage("src/main/resources/images/jungle/bush-2.png"));
        }});
    }};
    private Image image;

    public GuiPlantSprite(ISprite sprite) {
        super(sprite);
        this.image = getRandomImage(sprite);
    }

    @Override
    public void initialize() {
        this.image = getRandomImage(sprite);
        super.initialize();
        mainBox.setAlignment(Pos.BOTTOM_CENTER);
    }

    @Override
    public void changeSpritePosition(ISprite sprite) {} // Do nothing

    @Override
    Image getImage() {
        return image;
    }

    private Image getRandomImage(ISprite sprite) {
        List<Image> possibleImages = IMAGES.get(sprite.getMap().getAreaType(sprite.getPosition()));
        return possibleImages.get(Random.randInt(possibleImages.size() - 1));
    }
}
