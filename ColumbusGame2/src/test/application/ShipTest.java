package test.application;

import application.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import java.awt.Point;

/**
 * TEST CASE 1: Ship Movement & Boundary Testing
 * 
 * Tests all 4 directions and boundary conditions
 */
public class ShipTest {
    
    private Ship ship;
    private OceanMap oceanMap;
    
    /**
     * Setup - runs BEFORE each test
     * FIXED: Reset OceanMap singleton
     */
    @Before
    public void setUp() {
        // Reset the singleton instance
        OceanMap.instance = null;
        
        // Initialize fresh instances for each test
        oceanMap = OceanMap.getInstance(20);
        oceanMap.placeIslands(10);
        ship = new Ship(1, 1, 20);
    }
    
    /**
     * TEST 1.1: Move North (UP arrow)
     * Expected: Y decreases by 1
     */
    @Test
    public void testMoveNorth() {
        int initialY = ship.getShipLocation().y;
        int initialX = ship.getShipLocation().x;
        
        ship.goNorth(oceanMap);
        
        Assert.assertEquals("Y should decrease", initialY - 1, ship.getShipLocation().y);
        Assert.assertEquals("X should stay same", initialX, ship.getShipLocation().x);
    }
    
    /**
     * TEST 1.2: Move South (DOWN arrow)
     * Expected: Y increases by 1
     */
    @Test
    public void testMoveSouth() {
        int initialY = ship.getShipLocation().y;
        int initialX = ship.getShipLocation().x;
        
        ship.goSouth(oceanMap);
        
        Assert.assertEquals("Y should increase", initialY + 1, ship.getShipLocation().y);
        Assert.assertEquals("X should stay same", initialX, ship.getShipLocation().x);
    }
    
    /**
     * TEST 1.3: Move East (RIGHT arrow)
     * Expected: X increases by 1
     */
    @Test
    public void testMoveEast() {
        int initialX = ship.getShipLocation().x;
        int initialY = ship.getShipLocation().y;
        
        ship.goEast(oceanMap);
        
        Assert.assertEquals("X should increase", initialX + 1, ship.getShipLocation().x);
        Assert.assertEquals("Y should stay same", initialY, ship.getShipLocation().y);
    }
    
    /**
     * TEST 1.4: Move West (LEFT arrow)
     * Expected: X decreases by 1
     */
    @Test
    public void testMoveWest() {
        // Start at (5,5) to avoid boundary issues
        ship = new Ship(5, 5, 20);
        int initialX = ship.getShipLocation().x;
        int initialY = ship.getShipLocation().y;
        
        ship.goWest(oceanMap);
        
        Assert.assertEquals("X should decrease", initialX - 1, ship.getShipLocation().x);
        Assert.assertEquals("Y should stay same", initialY, ship.getShipLocation().y);
    }
    
    /**
     * TEST 1.5: Cannot move North beyond boundary (Y=0)
     * Expected: Y stays at 0 (blocked)
     */
    @Test
    public void testNorthBoundary() {
        ship = new Ship(1, 0, 20);  // At top boundary
        int initialY = ship.getShipLocation().y;
        
        ship.goNorth(oceanMap);  // Try to move north
        
        Assert.assertEquals("Should stay at boundary Y=0", initialY, ship.getShipLocation().y);
    }
    
    /**
     * TEST 1.6: Cannot move South beyond boundary (Y=19)
     * Expected: Y stays at 19 (blocked)
     */
    @Test
    public void testSouthBoundary() {
        ship = new Ship(1, 19, 20);  // At bottom boundary
        int initialY = ship.getShipLocation().y;
        
        ship.goSouth(oceanMap);  // Try to move south
        
        Assert.assertEquals("Should stay at boundary Y=19", initialY, ship.getShipLocation().y);
    }
    
    /**
     * TEST 1.7: Cannot move West beyond boundary (X=0)
     * Expected: X stays at 0 (blocked)
     */
    @Test
    public void testWestBoundary() {
        ship = new Ship(0, 1, 20);  // At left boundary
        int initialX = ship.getShipLocation().x;
        
        ship.goWest(oceanMap);  // Try to move west
        
        Assert.assertEquals("Should stay at boundary X=0", initialX, ship.getShipLocation().x);
    }
    
    /**
     * TEST 1.8: Cannot move East beyond boundary (X=19)
     * Expected: X stays at 19 (blocked)
     */
    @Test
    public void testEastBoundary() {
        ship = new Ship(19, 1, 20);  // At right boundary
        int initialX = ship.getShipLocation().x;
        
        ship.goEast(oceanMap);  // Try to move east
        
        Assert.assertEquals("Should stay at boundary X=19", initialX, ship.getShipLocation().x);
    }
}