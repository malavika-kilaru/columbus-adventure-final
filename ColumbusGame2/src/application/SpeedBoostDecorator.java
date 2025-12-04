package application;

public class SpeedBoostDecorator extends ShipDecorator {
    public SpeedBoostDecorator(ShipComponent ship) {
        super(ship);
    }
    
    @Override
    public int getArmor() {
        return wrappedShip.getArmor() + 2;
    }
    
    @Override
    public String getDescription() {
        return wrappedShip.getDescription() + " + ⚡ Speed Boost";
    }
    
    @Override
    public int getProtectionLevel() {
        return wrappedShip.getProtectionLevel() + 1;
    }
}
