package application;

public abstract class ShipDecorator implements ShipComponent {
    protected ShipComponent wrappedShip;
    
    public ShipDecorator(ShipComponent ship) {
        this.wrappedShip = ship;
    }
    
    @Override
    public int getArmor() {
        return wrappedShip.getArmor();
    }
    
    @Override
    public boolean hasShield() {
        return wrappedShip.hasShield();
    }
    
    @Override
    public String getDescription() {
        return wrappedShip.getDescription();
    }
    
    @Override
    public int getProtectionLevel() {
        return wrappedShip.getProtectionLevel();
    }
}