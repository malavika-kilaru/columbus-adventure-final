package application;
// chase pirateShip class using Factory design pattern 
//extends from superclass of PirateshipFactory
public class ChasePirateShipFactory extends PirateShipFactory {
    
    @Override
    public PirateShip createPirateShip(int x, int y, int dimension, OceanMap oceanMap) {
        return new PirateShip(x, y, dimension, oceanMap, new ChaseStrategy());
    }
}