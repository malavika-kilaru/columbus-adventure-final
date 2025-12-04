package application;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * GameControllerV2 - Complete Game Logic
 */
public class GameControllerV2 {

    private Ship ship;
    private Treasure treasure;
    private List<PirateShip> pirates;
    private List<OceanEntity> oceanEntities;
    private OceanMap oceanMap;
    private GameState currentState;
    private int score;
    private int lives;
    private String difficulty;
    private int level;

    // Game mechanics
    private int monsterChaseDistance = 10;
    private boolean monstersActive = false;

    // Decorator ship with powers
    private ShipComponent playerShip;

    public GameControllerV2(Ship ship, Treasure treasure, OceanMap oceanMap, String difficulty) {
        this.ship = ship;
        this.treasure = treasure;
        this.oceanMap = oceanMap;
        this.difficulty = difficulty;
        this.pirates = new ArrayList<>();
        this.oceanEntities = new ArrayList<>();
        this.currentState = GameState.PLAYING;
        this.score = 0;

        // Parse level from difficulty
        this.level = parseLevelFromDifficulty(difficulty);

        // DECORATOR: base ship + shield on some modes
        this.playerShip = new BaseShip("Columbus");

        setDifficultyStats();
    }

    private int parseLevelFromDifficulty(String diff) {
        switch (diff.toUpperCase()) {
            case "EASY": return 1;
            case "MEDIUM": return 2;
            case "HARD": return 3;
            case "SURVIVAL": return 4;
            default: return 1;
        }
    }

    private void setDifficultyStats() {
        switch (difficulty.toUpperCase()) {
            case "EASY":
                this.lives = 5;
                playerShip = new ShieldDecorator(playerShip);
                System.out.println(" LEVEL 1 - EASY: 5 lives, 1 Pirate, 1 Monster");
                break;

            case "MEDIUM":
                this.lives = 4;
                playerShip = new ShieldDecorator(playerShip);
                System.out.println(" LEVEL 2 - MEDIUM: 4 lives, 2 Pirates, 2 Monsters");
                break;

            case "HARD":
                this.lives = 3;
                System.out.println(" LEVEL 3 - HARD: 3 lives, 3 Pirates, 3 Monsters");
                break;

            case "SURVIVAL":
                this.lives = 2;
                System.out.println(" LEVEL 4 - SURVIVAL: 2 lives, 4 Pirates, 4 Monsters");
                break;
        }
    }

    public int getPirateCount() {
        return level;
    }

    public int getMonsterCount() {
        switch (level) {
            case 1: return 1;
            case 2: return 2;
            case 3: return 2;
            case 4: return 2;
            default: return 1;
        }
    }

    public void addPirate(PirateShip pirate) {
        pirates.add(pirate);
    }

    public void addSeaMonster(OceanEntity monster) {
        oceanEntities.add(monster);
    }

    public void addMonsterGroup(OceanEntity group) {
        oceanEntities.add(group);
    }

    /**
     *  MAIN GAME UPDATE LOOP 
     * Called once per movement.
     */
    public void update() {
        if (currentState != GameState.PLAYING) return;

        //  Score increases each update (player movement)
        score += 10;

        Point shipLoc = ship.getShipLocation();
        Point treasureLoc = treasure.getLocation();

        double distanceToTreasure = calculateDistance(shipLoc, treasureLoc);

        // Activate monster chase mode
        if (distanceToTreasure <= monsterChaseDistance && !monstersActive) {
            monstersActive = true;
            System.out.println(" SEA MONSTERS ACTIVATED! They begin actively chasing!");
        }

        //  FIX: Monsters ALWAYS MOVE (patrol or chase)
        for (OceanEntity entity : oceanEntities) {
            try {
                entity.move(ship);   // SeaMonster decides patrol/chase internally
            } catch (Exception e) {
                // Ignore errors from individual monsters
            }
        }

        checkCollisions();
        checkWinCondition();
    }

    private double calculateDistance(Point p1, Point p2) {
        return Math.sqrt(
            Math.pow(p1.x - p2.x, 2) +
            Math.pow(p1.y - p2.y, 2)
        );
    }

    private void checkCollisions() {
        Point shipLoc = ship.getShipLocation();

        // Check pirate collision
        for (PirateShip pirate : pirates) {
            if (pirate.getLocation().equals(shipLoc)) {
                System.out.println(" PIRATE COLLISION!");
                handleCollision();
                return;
            }
        }

        // Check monster collision
        for (OceanEntity entity : oceanEntities) {
            if (entity.collidesWith(shipLoc.x, shipLoc.y)) {
                System.out.println(" MONSTER COLLISION!");
                handleCollision();
                return;
            }
        }
    }

    public void handleCollision() {
        lives--;
        score = Math.max(0, score - 100);
        System.out.println(" LOST 1 LIFE! Lives remaining: " + lives);

        if (lives <= 0) {
            currentState = GameState.LOSE;
            System.out.println(" GAME OVER!");
        } else {
            // Reset player position
            ship.getShipLocation().x = 1;
            ship.getShipLocation().y = 1;

            monstersActive = false;

            // Update pirate targets
            for (PirateShip pirate : pirates) {
                pirate.update(ship.getShipLocation());
            }
        }
    }

    public void checkWinCondition() {
        Point shipLoc = ship.getShipLocation();
        if (treasure.isAt(shipLoc.x, shipLoc.y)) {
            currentState = GameState.WIN;
            score += 1000;
            System.out.println(" TREASURE FOUND! Level " + level + " Complete!");
        }
    }

    public GameState getCurrentState() { return currentState; }
    public int getScore() { return score; }
    public int getLives() { return lives; }

    public String getShipDescription() { return playerShip.getDescription(); }
    public int getShipArmor() { return playerShip.getArmor(); }
    public int getShipProtection() { return playerShip.getProtectionLevel(); }

    public List<PirateShip> getPirates() { return pirates; }
    public List<OceanEntity> getOceanEntities() { return new ArrayList<>(oceanEntities); }
    public boolean areMonstersActive() { return monstersActive; }
    public int getLevel() { return level; }
}