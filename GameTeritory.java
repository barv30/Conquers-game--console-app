package conquers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameTeritory implements Serializable
{
    public enum TypeOfSurface
    {
        X,Y,N
    }

    private int id;
    private int profit;
    private int armyThreshold;
    private GameTeritory.TypeOfSurface type;
    private int currentPower;
    private List<ArmyUnit> unitInTeritory;


    public GameTeritory()
    {
        type= GameTeritory.TypeOfSurface.N;
        currentPower=0;
        unitInTeritory = new ArrayList<ArmyUnit>();

    }

    public void setType(TypeOfSurface type) {
        this.type = type;
    }

    public void setCurrentPower(int currentPower) {
        this.currentPower = currentPower;
    }
    public List<ArmyUnit> getUnitInTeritory() {
        return unitInTeritory;
    }

    public void setUnitInTeritory(List<ArmyUnit> unitInTeritory) {
        this.unitInTeritory = unitInTeritory;
    }

    public GameTeritory.TypeOfSurface getType() {
        return type;
    }

    public int getCurrentPower() {
        return currentPower;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }

    public void setArmyThreshold(int armyThreshold) {
        this.armyThreshold = armyThreshold;
    }

    public int getId() {
        return id;
    }

    public int getArmyThreshold() {
        return armyThreshold;
    }

    public int getProfit() {
        return profit;
    }
}
