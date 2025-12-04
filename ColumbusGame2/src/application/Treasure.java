package application;
import java.awt.Point;
import java.util.Random;

public class Treasure {
    private Point location;
    private boolean discovered;
    

    public Treasure(int dimension, OceanMap oceanMap) {
        this.discovered = false;
                
        Random random = new Random();
        boolean validLocation = false;
        
        while (!validLocation) {
            int x = random.nextInt(dimension);
            int y = random.nextInt(dimension);
            if (!oceanMap.isIsland(x, y)) {
                this.location = new Point(x, y);
                validLocation = true;
            }
        }
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(int x, int y) {
        this.location = new Point(x, y);
    }

    public boolean isAt(int x, int y) {
        return location.x == x && location.y == y;
    }

    public boolean isDiscovered() {
        return discovered;
    }

    public void discover() {
        this.discovered = true;
    }

   

    @Override
    public String toString() {
        return "Treasure at(" + location.x + "," + location.y + ")";
    }
}