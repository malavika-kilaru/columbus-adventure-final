package application;

public class BaseShip implements ShipComponent {
    private String name;
    
    public BaseShip(String name) {
        this.name = name;
    }
    
    @Override
    public int getArmor() {
        return 0;
    }
    
    @Override
    public boolean hasShield() {
        return false;
    }
    
    @Override
    public String getDescription() {
        return "⛵ " + name;
    }
    
    @Override
    public int getProtectionLevel() {
        return 0;
    }
}
