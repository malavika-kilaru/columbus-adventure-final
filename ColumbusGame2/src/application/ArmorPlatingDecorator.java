package application;

public class ArmorPlatingDecorator extends ShipDecorator {
    public ArmorPlatingDecorator(ShipComponent ship) {
        super(ship);
    }
    
    @Override
    public int getArmor() {
        return wrappedShip.getArmor() + 10;
    }
    
    @Override
    public String getDescription() {
        return wrappedShip.getDescription() + " +  Heavy Armor";
    }
    
    @Override
    public int getProtectionLevel() {
        return wrappedShip.getProtectionLevel() + 5;
    }
}