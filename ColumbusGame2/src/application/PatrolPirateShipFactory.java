package application;
// PatrolPirateShipFactory class 
public class PatrolPirateShipFactory extends PirateShipFactory {
    
    @Override
    public PirateShip createPirateShip(int x, int y, int dimension, OceanMap oceanMap) {
        return new PirateShip(x, y, dimension, oceanMap, new PatrolStrategy());
    }
}