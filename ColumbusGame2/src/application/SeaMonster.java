package application;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * SeaMonster - Leaf Node of Composite Pattern
 */
public class SeaMonster implements OceanEntity {
    private Point location;
    private OceanMap oceanMap;
    private Random random;
    private String monsterType;
    private int detectionRange = 5;  // Chase up to 5 grids from treasure
    private int moveAttempts = 0;
    
    public SeaMonster(int x, int y, int dimension, OceanMap oceanMap, String monsterType) {
        this.location = new Point(x, y);
        this.oceanMap = oceanMap;
        this.random = new Random();
        this.monsterType = monsterType;
    }
    
    public Point getLocation() {
        return location;
    }
    
    public String getMonsterType() {
        return monsterType;
    }
    
    public boolean isAt(int x, int y) {
        return location.x == x && location.y == y;
    }
    
    /**
     * Move monster - chase ship if detected, otherwise patrol
     * COMPOSITE PATTERN: Implements OceanEntity interface
     */
    @Override
    public void move(Ship ship) {
        if (ship == null) {
            randomPatrol();
            return;
        }
        
        if (canSeeShip(ship)) {
            chaseShip(ship);
        } else {
            randomPatrol();
        }
    }
    
    /**
     * Check if monster can "see" the ship within detection range
     */
    private boolean canSeeShip(Ship ship) {
        if (ship == null) return false;
        
        Point shipLoc = ship.getShipLocation();
        double distance = Math.sqrt(
            Math.pow(location.x - shipLoc.x, 2) + 
            Math.pow(location.y - shipLoc.y, 2)
        );
        
        return distance <= detectionRange;
    }
    
    /**
     * Chase the ship - move directly toward it, avoiding islands
     */
    private void chaseShip(Ship ship) {
        if (ship == null) return;
        
        Point shipLoc = ship.getShipLocation();
        
        int dx = shipLoc.x - location.x;
        int dy = shipLoc.y - location.y;
        
        // Don't move if already on ship
        if (dx == 0 && dy == 0) return;
        
        // Try moving along largest difference first (better pathfinding)
        if (Math.abs(dx) > Math.abs(dy)) {
            // Horizontal distance is larger
            Point hMove = new Point(
                location.x + (dx > 0 ? 1 : -1),
                location.y
            );
            if (isValidMove(hMove)) {
                location = hMove;
                return;
            }
            // Blocked horizontally, try vertical
            Point vMove = new Point(
                location.x,
                location.y + (dy > 0 ? 1 : -1)
            );
            if (isValidMove(vMove)) {
                location = vMove;
                return;
            }
        } else {
            // Vertical distance is larger
            Point vMove = new Point(
                location.x,
                location.y + (dy > 0 ? 1 : -1)
            );
            if (isValidMove(vMove)) {
                location = vMove;
                return;
            }
            // Blocked vertically, try horizontal
            Point hMove = new Point(
                location.x + (dx > 0 ? 1 : -1),
                location.y
            );
            if (isValidMove(hMove)) {
                location = hMove;
                return;
            }
        }
        
        // If blocked, try diagonal moves
        tryDiagonalMove(dx, dy);
    }
    
    /**
     * Try diagonal movement when blocked
     */
    private void tryDiagonalMove(int dx, int dy) {
        Point[] diagonals = new Point[] {
            new Point(location.x + (dx > 0 ? 1 : -1), location.y + (dy > 0 ? 1 : -1)),
            new Point(location.x + (dx > 0 ? 1 : -1), location.y),
            new Point(location.x, location.y + (dy > 0 ? 1 : -1)),
        };
        
        for (Point move : diagonals) {
            if (isValidMove(move)) {
                location = move;
                return;
            }
        }
    }
    
    /**
     * Patrol randomly when ship not detected
     */
    private void randomPatrol() {
        moveAttempts = 0;
        
        while (moveAttempts < 4) {
            int direction = random.nextInt(4);
            Point newLocation = new Point(location.x, location.y);

            switch (direction) {
                case 0:  // UP
                    newLocation.y--;
                    break;
                case 1:  // DOWN
                    newLocation.y++;
                    break;
                case 2:  // RIGHT
                    newLocation.x++;
                    break;
                case 3:  // LEFT
                    newLocation.x--;
                    break;
            }

            if (isValidMove(newLocation)) {
                location = newLocation;
                return;
            }
            
            moveAttempts++;
        }
        
        // If all directions blocked, stay in place
    }
    
    /**
     * Check if a move is valid (in bounds and not on island)
     */
    private boolean isValidMove(Point newLocation) {
        return oceanMap.isINBounds(newLocation.x, newLocation.y) &&
               !oceanMap.isIsland(newLocation.x, newLocation.y);
    }
    
    /**
     * COMPOSITE PATTERN: Get all positions of this monster (leaf node)
     */
    @Override
    public List<Point> getPositions() {
        List<Point> positions = new ArrayList<>();
        positions.add(location);
        return positions;
    }
    
    /**
     * COMPOSITE PATTERN: Get type of this entity
     */
    @Override
    public String getType() {
        return "SeaMonster";
    }
    
    /**
     * COMPOSITE PATTERN: Check if monster collides with coordinates
     * Only collision if monster is EXACTLY on the location
     */
    @Override
    public boolean collidesWith(int x, int y) {
        return location.x == x && location.y == y;
    }
    
    public String getImagePath() {
        return "file:" + monsterType.toLowerCase() + ".png";
    }
    
    public void setDetectionRange(int range) {
        this.detectionRange = Math.max(1, range);
    }
    
    public int getDetectionRange() {
        return detectionRange;
    }
    
    @Override
    public String toString() {
        return monsterType + " at (" + location.x + ", " + location.y + ")";
    }
}