package application;

public abstract class PirateShipFactory {
    
    public abstract PirateShip createPirateShip(int x, int y, int dimension, OceanMap oceanMap);

    public PirateShip makePirateShip(int x, int y, int dimension, OceanMap oceanMap) {
        return createPirateShip(x, y, dimension, oceanMap);
    }
}