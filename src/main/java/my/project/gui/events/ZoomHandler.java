package my.project.gui.events;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;

public class ZoomHandler implements EventHandler<ScrollEvent> {
    private final static double MIN_SCALE_RATIO = .5d;
    private final static double MAX_SCALE_RATIO = 5d;
    private final static int MIN_ZOOM_SPEED = 1;
    private final static int MAX_ZOOM_SPEED = 100;
    private final static double SPEED_MULTIPLIER = .0001d;

    private final Node nodeToZoom;
    private final double minScale;
    private final double maxScale;
    private final double initialScale;

    private double currScale;

    public ZoomHandler(Node nodeToZoom, double initialScale) {
        this.nodeToZoom = nodeToZoom;
        this.minScale = MIN_SCALE_RATIO * initialScale;
        this.maxScale = MAX_SCALE_RATIO * initialScale;
        this.currScale = initialScale;
        this.initialScale = initialScale;
    }

    @Override
    public void handle(ScrollEvent scrollEvent) {
        if (scrollEvent.isControlDown()) {
            final double scale = calculateScale(scrollEvent);
            nodeToZoom.setScaleX(scale);
            nodeToZoom.setScaleY(scale);
            scrollEvent.consume();
        }
    }

    private double calculateScale(ScrollEvent scrollEvent) {
        double scale = nodeToZoom.getScaleX() + scrollEvent.getDeltaY() * getZoomSpeed();

        if (scale <= minScale) scale = minScale;
        else if (scale >= maxScale) scale = maxScale;
        currScale = scale;

        return scale;
    }

    private double getZoomSpeed() {
        double scalePercentage = Math.max(currScale - initialScale, 0) / (maxScale - minScale);
        return SPEED_MULTIPLIER * (MIN_ZOOM_SPEED + (MAX_ZOOM_SPEED - MIN_ZOOM_SPEED) * scalePercentage);
    }
}
