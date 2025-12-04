package application;

/**
 * GameState Enum - Represents possible game states
 */
public enum GameState {
    PLAYING("Game is running - find the treasure!"),
    WIN("Player won - found treasure!"),
    LOSE("Player lost - caught by enemy!"),
    PAUSED("Game is paused");
    
    private String description;
    
    /**
     * Constructor
     */
    GameState(String description) {
        this.description = description;
    }
    
    /**
     * Get description of this state
     */
    public String getDescription() {
        return description;
    }
}