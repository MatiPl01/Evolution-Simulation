package my.project.gui.events;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;

public class ZoomHandler implements EventHandler<ScrollEvent> {
    private final static double MIN_SCALE_RATIO = .5d;
    private final static double MAX_SCALE_RATIO = 5d;

    private final Node nodeToZoom;
    private final double minScale;
    private final double maxScale;

    public ZoomHandler(Node nodeToZoom, double initialScale) {
        this.nodeToZoom = nodeToZoom;
        this.minScale = MIN_SCALE_RATIO * initialScale;
        this.maxScale = MAX_SCALE_RATIO * initialScale;
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
        double scale = nodeToZoom.getScaleX() + scrollEvent.getDeltaY() / 250;

        if (scale <= minScale) scale = minScale;
        else if (scale >= maxScale) scale = maxScale;

        return scale;
    }
}
