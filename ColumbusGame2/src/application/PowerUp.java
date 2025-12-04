package application;
import java.awt.Point;

public class PowerUp {
    public enum PowerUpType {
        SHIELD("🛡️", "Shield - 1 Free Hit"),
        SPEED("⚡", "Speed - Move 2x"),
        HEALTH("❤️", "Health +1"),
        RADAR("🎯", "Radar - See Enemies");

        private String emoji;
        private String description;

        PowerUpType(String emoji, String description) {
            this.emoji = emoji;
            this.description = description;
        }

        public String getEmoji() {
            return emoji;
        }

        public String getDescription() {
            return description;
        }
    }

    private Point location;
    private PowerUpType type;
    private int turnsRemaining;
    private boolean collected;

    public PowerUp(int x, int y, PowerUpType type) {
        this.location = new Point(x, y);
        this.type = type;
        this.turnsRemaining = 50;
        this.collected = false;
    }

    public boolean isAt(int x, int y) {
        return location.x == x && location.y == y;
    }

    public boolean isActive() {
        return !collected && turnsRemaining > 0;
    }

    public void collect() {
        this.collected = true;
    }

    public void updateTimer() {
        turnsRemaining--;
    }

    public Point getLocation() {
        return location;
    }

    public PowerUpType getType() {
        return type;
    }

    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    public boolean isCollected() {
        return collected;
    }

    @Override
    public String toString() {
        return type.emoji + " " + type.description;
    }
}