package application;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
// MonsterGroup implements from oceanEnitity interface 
public class MonsterGroup implements OceanEntity {
    private List<SeaMonster> monsters;
    private int boundaryX1, boundaryY1, boundaryX2, boundaryY2;
    private String groupName;
    
    public MonsterGroup(String name, int x1, int y1, int x2, int y2) {
        this.monsters = new ArrayList<>();
        this.groupName = name;
        this.boundaryX1 = Math.min(x1, x2);
        this.boundaryY1 = Math.min(y1, y2);
        this.boundaryX2 = Math.max(x1, x2);
        this.boundaryY2 = Math.max(y1, y2);
    }
     // add Monster
    public void addMonster(SeaMonster monster) {
        if (monster != null && !monsters.contains(monster)) {
            monsters.add(monster);
        }
    }
    // remove monster
    public void removeMonster(SeaMonster monster) {
        monsters.remove(monster);
    }
    
    public List<SeaMonster> getMonsters() {
        return new ArrayList<>(monsters);
    }
    
    @Override
    public void move(Ship ship) {
        for (SeaMonster monster : monsters) {
            monster.move(ship);
            
            Point loc = monster.getLocation();
            if (loc.x < boundaryX1) loc.x = boundaryX1;
            if (loc.x > boundaryX2) loc.x = boundaryX2;
            if (loc.y < boundaryY1) loc.y = boundaryY1;
            if (loc.y > boundaryY2) loc.y = boundaryY2;
        }
    }
    
    @Override
    public List<Point> getPositions() {
        List<Point> allPositions = new ArrayList<>();
        for (SeaMonster monster : monsters) {
            allPositions.addAll(monster.getPositions());
        }
        return allPositions;
    }
    
    @Override
    public boolean collidesWith(int x, int y) {
        for (SeaMonster monster : monsters) {
            if (monster.collidesWith(x, y)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String getType() {
        return "MonsterGroup";
    }
    
    public String getBoundaryInfo() {
        return String.format("Zone: (%d,%d) to (%d,%d)", 
                           boundaryX1, boundaryY1, boundaryX2, boundaryY2);
    }
    
    public boolean isWithinBoundary(int x, int y) {
        return x >= boundaryX1 && x <= boundaryX2 &&
               y >= boundaryY1 && y <= boundaryY2;
    }
    
    public int getSize() {
        return monsters.size();
    }
    
    @Override
    public String toString() {
        return groupName + " (" + monsters.size() + " monsters) - " + getBoundaryInfo();
    }
}
