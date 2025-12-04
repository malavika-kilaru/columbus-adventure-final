package application;
import java.awt.Point;

public class PirateShip implements ShipObserver {
    private Point currentLocation;
    private Point targetLocation;
    private int dimension;
    private OceanMap oceanMap;
    private PirateMovementStrategy strategy;

    public PirateShip(int x, int y, int dimension, OceanMap oceanMap, PirateMovementStrategy strategy) {
        this.currentLocation = new Point(x, y);
        this.dimension = dimension;
        this.targetLocation = new Point(x, y);
        this.oceanMap = oceanMap;
        this.strategy = strategy;
    }

    public Point getLocation() {
        return currentLocation;
    }

    @Override
    public void update(Point shipLocation) {
        targetLocation = new Point(shipLocation.x, shipLocation.y);
        moveUsingStrategy();
    }

    private void moveUsingStrategy() {
        Point newLocation = strategy.move(currentLocation, targetLocation, dimension, oceanMap);
        currentLocation = newLocation;
    }

    public PirateMovementStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(PirateMovementStrategy newStrategy) {
        this.strategy = newStrategy;
    }

    @Override
    public String toString() {
        return "PirateShip at (" + currentLocation.x + ", " + currentLocation.y + ")";
    }
}