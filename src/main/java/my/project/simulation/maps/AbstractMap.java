package my.project.simulation.maps;

import my.project.simulation.IObserver;
import my.project.simulation.enums.MapArea;
import my.project.simulation.sprites.Animal;
import my.project.simulation.sprites.Grass;
import my.project.simulation.sprites.ISprite;
import my.project.simulation.utils.EnergyComparator;
import my.project.simulation.utils.Random;
import my.project.simulation.utils.Vector2D;

import java.util.*;

public abstract class AbstractMap implements IMap, IObserver {
    protected final Vector2D mapLowerLeft = new Vector2D(0, 0);
    protected final Vector2D mapUpperRight;
    protected final Vector2D jungleLowerleft;
    protected final Vector2D jungleUpperRight;

    protected final Map<Vector2D, SortedSet<Animal>> mapAnimals = new HashMap<>();
    protected final Map<Vector2D, Grass> mapGrass = new HashMap<>();

    AbstractMap(int width, int height, double jungleRatio) {
        this.mapUpperRight = new Vector2D(width, height);
        int jungleWidth  = (int) (2 * Math.round((width / 2) * jungleRatio) + (width % 2 == 1 ? 1 : 0));
        int jungleHeight = (int) (2 * Math.round((height / 2) * jungleRatio) + (height % 2 == 1 ? 1 : 0));
        this.jungleLowerleft = new Vector2D((width - jungleWidth) / 2, (height - jungleHeight) / 2);
        this.jungleUpperRight = this.jungleLowerleft.add(new Vector2D(jungleWidth, jungleHeight));
    }

    @Override
    public void changeSpritePosition(ISprite sprite) throws IllegalArgumentException, NoSuchElementException {
        if (!(sprite instanceof Animal)) {
            throw new IllegalArgumentException("Cannot change position of a sprite which is not an animal");
        } else {
            Vector2D prevPosition = ((Animal) sprite).getPrevPosition();
            SortedSet<Animal> animals = mapAnimals.get(prevPosition);
            if (animals == null || !animals.remove(sprite)) {
                throw new NoSuchElementException("Sprite " + sprite + " is not on the map");
            }
            addSprite(sprite);
        }
    }

    @Override
    public void removeSprite(ISprite sprite) throws NoSuchElementException {
        Vector2D position;
        // If a sprite is an animal object
        if (sprite instanceof Animal) {
            position = ((Animal) sprite).getCurrPosition();
            SortedSet<Animal> animals = mapAnimals.get(position);
            if (animals == null || !animals.remove(sprite)) {
                throw new NoSuchElementException("Sprite " + sprite + " is not on the map");
            }
        // If a sprite is a grass object
        } else if (sprite instanceof Grass) {
            position = ((Grass) sprite).getPosition();
            if (mapGrass.remove(position) == null) {
                throw new NoSuchElementException("Sprite " + sprite + " is not on the map");
            }
        }
    }

    @Override
    public void addSprite(ISprite sprite) throws IllegalArgumentException {
        Vector2D position;
        // If a sprite is an animal object
        if (sprite instanceof Animal) {
            position = ((Animal) sprite).getCurrPosition();
            // Create the new animals list if there is no animals list on the specified position
            if (mapAnimals.get(position) == null) {
                mapAnimals.put(position, new TreeSet<>(new EnergyComparator()));
            }
            // Add an animal to the list
            mapAnimals.get(position).add((Animal)sprite);
        // If a sprite is a grass object
        } else if (sprite instanceof Grass) {
            position = ((Grass) sprite).getPosition();
            // Place grass on a field only if there is no other element occupying this field
            if (mapGrass.get(position) != null || mapAnimals.get(position) != null) {
                String message = "Cannot place grass on field: " + position + ". Field is not empty.";
                throw new IllegalArgumentException(message);
            }
            mapGrass.put(position, (Grass)sprite);
        }
    }

    @Override
    public MapArea getAreaType(Vector2D position) {
        if (position.precedes(jungleUpperRight) && position.follows(jungleLowerleft)) {
            return MapArea.JUNGLE;
        } else if (position.precedes(mapUpperRight) && position.follows(mapLowerLeft)) {
            return MapArea.STEPPE;
        }
        return null;
    }

    @Override
    public void spawnGrass() {
        spawnSingleGrass(MapArea.JUNGLE);
        spawnSingleGrass(MapArea.STEPPE);
    }

    public void update() {
        spawnGrass();
    }

    public boolean isEmptyField(Vector2D position) {
        return mapAnimals.get(position) == null
                || mapAnimals.get(position).size() == 0
                || mapGrass.get(position) == null;
    }

    private void spawnSingleGrass(MapArea area) {
        Vector2D position = switch (area) {
            case JUNGLE -> getJungleEmptyFieldVector();
            case STEPPE -> getSteppeEmptyFieldVector();
        };
        // Do not spawn a grass object if there is no space available
        if (position == null) return;
        ISprite grass = new Grass(this, position);
        grass.add();
    }

    private Vector2D getJungleEmptyFieldVector() {
        return getSegmentEmptyFieldVector(null, jungleLowerleft, jungleUpperRight);
    }

    private Vector2D getSteppeEmptyFieldVector() {
        /*
        *  +---+---+---+
        *  | 0 | 1 | 2 |
        *  +---+---+---+
        *  | 3 |   | 4 |
        *  +---+---+---+
        *  | 5 | 6 | 7 |
        *  +---+---+---+
        */
        int segmentsCount = 8;
        int segmentIdx = Random.randInt(segmentsCount - 1);

        Vector2D position = null;
        int count = 0;
        while (count++ < segmentsCount && position == null) {
            Vector2D segmentLowerLeft  = getSegmentLowerLeft(segmentIdx);
            Vector2D segmentUpperRight = getSegmentUpperRight(segmentIdx);
            Vector2D initialPosition   = count > 0 ? segmentLowerLeft : null;
            position = getSegmentEmptyFieldVector(initialPosition, segmentLowerLeft, segmentUpperRight);
            segmentIdx = (segmentIdx + 1) % segmentsCount;
        }
        return position;
    }

    private Vector2D getSegmentLowerLeft(int segmentIdx) throws IllegalArgumentException {
        return switch (segmentIdx) {
            case 0 -> new Vector2D(mapLowerLeft.getX(), jungleUpperRight.getY());
            case 1 -> jungleLowerleft.upperLeft(jungleUpperRight);
            case 2 -> jungleUpperRight;
            case 3 -> new Vector2D(mapLowerLeft.getX(), jungleLowerleft.getY());
            case 4 -> jungleLowerleft.lowerRight(jungleUpperRight);
            case 5 -> mapLowerLeft;
            case 6 -> new Vector2D(jungleLowerleft.getX(), mapLowerLeft.getY());
            case 7 -> mapLowerLeft.lowerRight(mapUpperRight);
            default -> throw new IllegalArgumentException(segmentIdx + " is not valid segment index");
        };
    }

    private Vector2D getSegmentUpperRight(int segmentIdx) throws IllegalArgumentException {
        return switch (segmentIdx) {
            case 0 -> new Vector2D(jungleLowerleft.getX(), mapUpperRight.getY());
            case 1 -> new Vector2D(jungleUpperRight.getX(), mapUpperRight.getY());
            case 2 -> mapUpperRight;
            case 3 -> jungleLowerleft.upperLeft(jungleUpperRight);
            case 4 -> new Vector2D(mapUpperRight.getX(), jungleUpperRight.getY());
            case 5 -> jungleLowerleft;
            case 6 -> new Vector2D(jungleLowerleft.getX(), mapLowerLeft.getY());
            case 7 -> new Vector2D(mapUpperRight.getX(), jungleLowerleft.getY());
            default -> throw new IllegalArgumentException(segmentIdx + " is not valid segment index");
        };
    }

    private Vector2D getSegmentEmptyFieldVector(Vector2D initialPosition, Vector2D lowerLeft, Vector2D upperRight) {
        int minX = lowerLeft.getX();
        int maxX = upperRight.getX();
        int minY = lowerLeft.getY();
        int maxY = upperRight.getY();
        // Choose the first vector randomly if is not specified
        Vector2D position = initialPosition == null ? Vector2D.randomVector(minX, maxX, minY, maxY) : initialPosition;
        // Calculate segment dimensions
        int segmentWidth = maxX - minX + 1;
        int segmentHeight = maxY - minY + 1;
        int i = initialPosition.getX() * segmentHeight + initialPosition.getY();
        int segmentFieldsCount = segmentWidth * segmentHeight;
        // Loop over subsequent fields till a field is not empty
        while (!isEmptyField(position)) {
            i = (i + 1) % segmentFieldsCount;
            position = lowerLeft.add(new Vector2D(i / segmentHeight, i % segmentWidth));
        }
        return position;
    }
}
