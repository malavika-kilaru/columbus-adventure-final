package application;
import java.awt.Point;

public interface PirateMovementStrategy {
    Point move(Point currentLocation, Point targetLocation, int dimension, OceanMap oceanMap);
}