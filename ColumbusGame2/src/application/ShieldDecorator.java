package application;

public class ShieldDecorator extends ShipDecorator {
    public ShieldDecorator(ShipComponent ship) {
        super(ship);
    }
    
    @Override
    public int getArmor() {
        return wrappedShip.getArmor() + 5;
    }
    
    @Override
    public boolean hasShield() {
        return true;
    }
    
    @Override
    public String getDescription() {
        return wrappedShip.getDescription() + " + 🛡️ Shield";
    }
    
    @Override
    public int getProtectionLevel() {
        return wrappedShip.getProtectionLevel() + 3;
    }
}
