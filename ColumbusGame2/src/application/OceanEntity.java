package application;

import java.awt.Point;
import java.util.List;
// interface OceanEntity
public interface OceanEntity {
    void move(Ship ship);
    List<Point> getPositions();
    String getType();
    boolean collidesWith(int x, int y);
}