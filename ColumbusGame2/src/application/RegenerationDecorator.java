package application;

public class RegenerationDecorator extends ShipDecorator {
    private int healingPower = 1;
    
    public RegenerationDecorator(ShipComponent ship) {
        super(ship);
    }
    
    @Override
    public String getDescription() {
        return wrappedShip.getDescription() + " + 💚 Regeneration";
    }
    
    @Override
    public int getProtectionLevel() {
        return wrappedShip.getProtectionLevel() + 2;
    }
    
    public int getHealingPower() {
        return healingPower;
    }
}
