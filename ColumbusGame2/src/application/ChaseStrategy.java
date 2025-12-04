package application;
import java.awt.Point;

/**
 * ChaseStrategy - Pirate Movement with Island Avoidance
 * 
 * Behavior:
 * 1. Maintain 2-3 steps distance from Columbus
 * 2. Avoid islands by checking all directions
 * 3. Never move directly on top of Columbus
 * 4. Move strategically to intercept
 * 
 * Grid Coordinates:
 * X: 0→19 (LEFT to RIGHT)
 * Y: 0→19 (TOP to BOTTOM)
 */
public class ChaseStrategy implements PirateMovementStrategy {

    @Override
    public Point move(Point currentLocation, Point targetLocation, int dimension, OceanMap oceanMap) {
        Point newLocation = new Point(currentLocation.x, currentLocation.y);

        // Calculate distance to target (Columbus)
        int dx = targetLocation.x - currentLocation.x;
        int dy = targetLocation.y - currentLocation.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // OPTIMAL DISTANCE: 2-3 steps away from Columbus
        double OPTIMAL_MIN = 2.0;
        double OPTIMAL_MAX = 3.5;

        // If at optimal distance, try to maintain position by moving perpendicular
        if (distance >= OPTIMAL_MIN && distance <= OPTIMAL_MAX) {
            Point perpMove = tryMovePerpendicular(currentLocation, targetLocation, oceanMap);
            if (!perpMove.equals(currentLocation)) {
                return perpMove;  // Successfully moved perpendicular
            }
            return currentLocation;  // Stay in place if can't move
        }

        // If too far (> 3.5), move closer
        if (distance > OPTIMAL_MAX) {
            return moveTowardTarget(currentLocation, targetLocation, oceanMap, dimension);
        }

        // If too close (< 2), back away
        if (distance < OPTIMAL_MIN) {
            return moveAwayFromTarget(currentLocation, targetLocation, oceanMap, dimension);
        }

        return currentLocation;
    }

    /**
     * Move one step toward Columbus (decrease distance)
     */
    private Point moveTowardTarget(Point current, Point target, OceanMap oceanMap, int dimension) {
        int dx = target.x - current.x;
        int dy = target.y - current.y;

        // Try moving along the larger axis first
        if (Math.abs(dx) > Math.abs(dy)) {
            // Horizontal distance larger - try moving left/right
            Point hMove = new Point(current.x + (dx > 0 ? 1 : -1), current.y);
            if (isValidMove(hMove, oceanMap, dimension)) {
                return hMove;
            }
            // If blocked, try vertical movement
            Point vMove = new Point(current.x, current.y + (dy > 0 ? 1 : -1));
            if (isValidMove(vMove, oceanMap, dimension)) {
                return vMove;
            }
        } else {
            // Vertical distance larger - try moving up/down
            Point vMove = new Point(current.x, current.y + (dy > 0 ? 1 : -1));
            if (isValidMove(vMove, oceanMap, dimension)) {
                return vMove;
            }
            // If blocked, try horizontal movement
            Point hMove = new Point(current.x + (dx > 0 ? 1 : -1), current.y);
            if (isValidMove(hMove, oceanMap, dimension)) {
                return hMove;
            }
        }

        return current;  // Can't move - blocked
    }

    /**
     * Move one step away from Columbus (increase distance)
     */
    private Point moveAwayFromTarget(Point current, Point target, OceanMap oceanMap, int dimension) {
        int dx = target.x - current.x;
        int dy = target.y - current.y;

        // Try moving in opposite direction
        if (Math.abs(dx) > Math.abs(dy)) {
            // Move opposite to horizontal distance
            Point hMove = new Point(current.x + (dx > 0 ? -1 : 1), current.y);
            if (isValidMove(hMove, oceanMap, dimension)) {
                return hMove;
            }
            // Try perpendicular (vertical)
            Point vMove = new Point(current.x, current.y + (dy > 0 ? -1 : 1));
            if (isValidMove(vMove, oceanMap, dimension)) {
                return vMove;
            }
        } else {
            // Move opposite to vertical distance
            Point vMove = new Point(current.x, current.y + (dy > 0 ? -1 : 1));
            if (isValidMove(vMove, oceanMap, dimension)) {
                return vMove;
            }
            // Try perpendicular (horizontal)
            Point hMove = new Point(current.x + (dx > 0 ? -1 : 1), current.y);
            if (isValidMove(hMove, oceanMap, dimension)) {
                return hMove;
            }
        }

        return current;  // Blocked
    }

    /**
     * Try moving perpendicular to maintain distance
     */
    private Point tryMovePerpendicular(Point current, Point target, OceanMap oceanMap) {
        int dx = target.x - current.x;
        int dy = target.y - current.y;

        // Try moving left/right
        if (Math.abs(dx) < 5) {
            Point leftMove = new Point(current.x - 1, current.y);
            if (isValidMove(leftMove, oceanMap, 20)) {
                return leftMove;
            }
            Point rightMove = new Point(current.x + 1, current.y);
            if (isValidMove(rightMove, oceanMap, 20)) {
                return rightMove;
            }
        }

        // Try moving up/down
        if (Math.abs(dy) < 5) {
            Point upMove = new Point(current.x, current.y - 1);
            if (isValidMove(upMove, oceanMap, 20)) {
                return upMove;
            }
            Point downMove = new Point(current.x, current.y + 1);
            if (isValidMove(downMove, oceanMap, 20)) {
                return downMove;
            }
        }

        return current;
    }

    /**
     * Check if move is valid (in bounds and not on island)
     */
    private boolean isValidMove(Point location, OceanMap oceanMap, int dimension) {
        return location.x >= 0 && location.x < dimension &&
               location.y >= 0 && location.y < dimension &&
               !oceanMap.isIsland(location.x, location.y);
    }
}