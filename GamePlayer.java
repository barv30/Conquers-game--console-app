package conquers;

import java.io.Serializable;

public class GamePlayer implements Serializable
{
    private double turings;
    private int numberOfTerritories;
    private GameTerritory.TypeOfTerritory type;

    public double getTurings() {
        return turings;
    }

    public void setTurings(double turings) {
        this.turings = turings;
    }

    public void setNumberOfTerritories(int numberOfSurface) {
        this.numberOfTerritories = numberOfSurface;
    }

    public void setType(GameTerritory.TypeOfTerritory type) {
        this.type = type;
    }

    public int getNumberOfTerritories() {
        return numberOfTerritories;
    }

    public GameTerritory.TypeOfTerritory getType() {
        return type;
    }
}
