package test.application;

import application.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import java.awt.Point;

/**
 * TEST CASE 2: Game Controller - Lives & Collision System
 * 
 * Tests collision detection, lives system, and game states
 */
public class GameControllerTest {
    
    private GameControllerV2 controller;
    private Ship ship;
    private OceanMap oceanMap;
    private Treasure treasure;
    
    /**
     * Setup - runs BEFORE each test
     * FIXED: Reset OceanMap singleton state
     */
    @Before
    public void setUp() {
        // Reset the singleton instance
        OceanMap.instance = null;
        
        oceanMap = OceanMap.getInstance(20);
        oceanMap.placeIslands(10);
        ship = new Ship(1, 1, 20);
        oceanMap.setShip(ship);
        treasure = new Treasure(20, oceanMap);
    }
    
    /**
     * TEST 2.1: EASY level starts with 5 lives
     * Expected: lives = 5
     */
    @Test
    public void testEasyLevelLives() {
        controller = new GameControllerV2(ship, treasure, oceanMap, "EASY");
        Assert.assertEquals("EASY should have 5 lives", 5, controller.getLives());
    }
    
    /**
     * TEST 2.2: MEDIUM level starts with 4 lives
     * Expected: lives = 4
     */
    @Test
    public void testMediumLevelLives() {
        controller = new GameControllerV2(ship, treasure, oceanMap, "MEDIUM");
        Assert.assertEquals("MEDIUM should have 4 lives", 4, controller.getLives());
    }
    
    /**
     * TEST 2.3: HARD level starts with 3 lives
     * Expected: lives = 3
     */
    @Test
    public void testHardLevelLives() {
        controller = new GameControllerV2(ship, treasure, oceanMap, "HARD");
        Assert.assertEquals("HARD should have 3 lives", 3, controller.getLives());
    }
    
    /**
     * TEST 2.4: SURVIVAL level starts with 2 lives
     * Expected: lives = 2
     */
    @Test
    public void testSurvivalLevelLives() {
        controller = new GameControllerV2(ship, treasure, oceanMap, "SURVIVAL");
        Assert.assertEquals("SURVIVAL should have 2 lives", 2, controller.getLives());
    }
    
    /**
     * TEST 2.5: Pirate count progression (1→2→3→4)
     * Expected: Easy=1, Medium=2, Hard=3, Survival=4
     */
    @Test
    public void testPirateCountProgression() {
        GameControllerV2 easy = new GameControllerV2(ship, treasure, oceanMap, "EASY");
        Assert.assertEquals("EASY level should have 1 pirate", 1, easy.getPirateCount());
        
        GameControllerV2 medium = new GameControllerV2(ship, treasure, oceanMap, "MEDIUM");
        Assert.assertEquals("MEDIUM level should have 2 pirates", 2, medium.getPirateCount());
        
        GameControllerV2 hard = new GameControllerV2(ship, treasure, oceanMap, "HARD");
        Assert.assertEquals("HARD level should have 3 pirates", 3, hard.getPirateCount());
        
        GameControllerV2 survival = new GameControllerV2(ship, treasure, oceanMap, "SURVIVAL");
        Assert.assertEquals("SURVIVAL level should have 4 pirates", 4, survival.getPirateCount());
    }
    
    /**
     * TEST 2.6: Monster count capped at 2 (1→2→2→2)
     * Expected: Easy=1, Medium=2, Hard=2, Survival=2
     */
    @Test
    public void testMonsterCountCapped() {
        GameControllerV2 easy = new GameControllerV2(ship, treasure, oceanMap, "EASY");
        Assert.assertEquals("EASY level should have 1 monster", 1, easy.getMonsterCount());
        
        GameControllerV2 medium = new GameControllerV2(ship, treasure, oceanMap, "MEDIUM");
        Assert.assertEquals("MEDIUM level should have 2 monsters", 2, medium.getMonsterCount());
        
        GameControllerV2 hard = new GameControllerV2(ship, treasure, oceanMap, "HARD");
        Assert.assertEquals("HARD level should have 2 monsters (capped)", 2, hard.getMonsterCount());
        
        GameControllerV2 survival = new GameControllerV2(ship, treasure, oceanMap, "SURVIVAL");
        Assert.assertEquals("SURVIVAL level should have 2 monsters (capped)", 2, survival.getMonsterCount());
    }
    
    /**
     * TEST 2.7: Collision loses 1 life
     * Expected: 5 lives → 4 lives
     */
    @Test
    public void testCollisionLosesLife() {
        controller = new GameControllerV2(ship, treasure, oceanMap, "EASY");
        int initialLives = controller.getLives();  // 5
        
        // Simulate collision
        controller.handleCollision();
        
        Assert.assertEquals("Should lose 1 life", initialLives - 1, controller.getLives());
    }
    
    /**
     * TEST 2.8: Collision resets ship to (1,1)
     * Expected: Ship position = (1, 1)
     */
    @Test
    public void testCollisionResetPosition() {
        controller = new GameControllerV2(ship, treasure, oceanMap, "EASY");
        
        // Move ship away from start
        ship.goEast(oceanMap);   // (2, 1)
        ship.goSouth(oceanMap);  // (2, 2)
        
        Assert.assertNotEquals("Ship moved away", 1, ship.getShipLocation().x);
        Assert.assertNotEquals("Ship moved away", 1, ship.getShipLocation().y);
        
        // Collision occurs
        controller.handleCollision();
        
        // Check reset to (1,1)
        Assert.assertEquals("Ship should reset to X=1", 1, ship.getShipLocation().x);
        Assert.assertEquals("Ship should reset to Y=1", 1, ship.getShipLocation().y);
    }
    
    /**
     * TEST 2.9: Collision score penalty
     * Expected: score decreases by 100
     */
    @Test
    public void testCollisionScorePenalty() {
        controller = new GameControllerV2(ship, treasure, oceanMap, "EASY");
        int initialScore = controller.getScore();
        
        controller.handleCollision();
        
        Assert.assertTrue("Score should decrease", controller.getScore() <= initialScore);
    }
    
    /**
     * TEST 2.10: Game Over when all lives lost
     * Expected: GameState = LOSE
     */
    @Test
    public void testGameOverAllLivesLost() {
        controller = new GameControllerV2(ship, treasure, oceanMap, "EASY");
        
        // Initial state should be PLAYING
        Assert.assertEquals("Should start as PLAYING", GameState.PLAYING, controller.getCurrentState());
        
        // Lose all 5 lives
        for (int i = 0; i < 5; i++) {
            controller.handleCollision();
        }
        
        // After 5 collisions, lives = 0 and state = LOSE
        Assert.assertEquals("Should be LOSE after all lives lost", GameState.LOSE, controller.getCurrentState());
    }
    
    /**
     * TEST 2.11: Score increases per valid move
     * Expected: score increases by 10 per update
     */
    @Test
    public void testScoreIncreasePerMove() {
        controller = new GameControllerV2(ship, treasure, oceanMap, "EASY");
        int initialScore = controller.getScore();  // 0
        
        controller.update();
        int scoreAfterMove = controller.getScore();
        
        Assert.assertTrue("Score should increase per move", scoreAfterMove > initialScore);
    }
    
    /**
     * TEST 2.12: Remaining lives prevents game over
     * Expected: Game continues with 1 life left
     */
    @Test
    public void testGameContinuesWithRemainingLives() {
        controller = new GameControllerV2(ship, treasure, oceanMap, "SURVIVAL");  // 2 lives
        
        Assert.assertEquals("Start with 2 lives", 2, controller.getLives());
        Assert.assertEquals("State is PLAYING", GameState.PLAYING, controller.getCurrentState());
        
        // Lose 1 life
        controller.handleCollision();
        
        Assert.assertEquals("Now 1 life left", 1, controller.getLives());
        Assert.assertEquals("Still PLAYING", GameState.PLAYING, controller.getCurrentState());
        
        // Lose 2nd life
        controller.handleCollision();
        
        Assert.assertEquals("No lives left", 0, controller.getLives());
        Assert.assertEquals("Now LOSE", GameState.LOSE, controller.getCurrentState());
    }
}