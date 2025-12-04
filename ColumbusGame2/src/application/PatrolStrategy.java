package application;
import java.awt.Point;
import java.util.Random;

public class PatrolStrategy implements PirateMovementStrategy {
    private Random random = new Random();
    private int patrolCounter = 0;

    @Override
    public Point move(Point currentLocation, Point targetLocation, int dimension, OceanMap oceanMap) {
        Point newLocation = new Point(currentLocation.x, currentLocation.y);
        patrolCounter++;

        if (patrolCounter % 5 == 0) {
            int direction = random.nextInt(4);
            
            switch (direction) {
                case 0:
                    newLocation.y--;
                    break;
                case 1:
                    newLocation.y++;
                    break;
                case 2:
                    newLocation.x++;
                    break;
                case 3:
                    newLocation.x--;
                    break;
            }
        }

        if (canMoveTo(newLocation, oceanMap)) {
            return newLocation;
        }
        
        return currentLocation;
    }

    private boolean canMoveTo(Point location, OceanMap oceanMap) {
        return oceanMap.isINBounds(location.x, location.y) && 
               !oceanMap.isIsland(location.x, location.y);
    }
}