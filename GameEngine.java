package conquers;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GameEngine implements Serializable
{
    protected GameBoard board;
    protected int initialFunds;
    protected List<GameTerritory> territories;
    protected int totalCycles;
    private int defaultProfit;
    private int defaultArmyThreshold;
    private List<ArmyUnit> infoArmy;
    private int counterOfRounds;
    private int numberOfPlayers;
    private int counterPlayers;
    private List<GamePlayer> players ;
    public boolean gameStarted = false;
    private GameTerritory.TypeOfTerritory typeCurrentPlayer;
    public generate.GameDescriptor gameDescriptor;


    public void initGameEngine()
    {
        board=new GameBoard(gameDescriptor.getGame().getBoard().getRows().intValue(),gameDescriptor.getGame().getBoard().getColumns().intValue());
        initialFunds=gameDescriptor.getGame().getInitialFunds().intValue();
        territories=new ArrayList<GameTerritory>(gameDescriptor.getGame().getTerritories().getTeritory().size());
        initTerritoryList();
        totalCycles=gameDescriptor.getGame().getTotalCycles().intValue();
        if(gameDescriptor.getGame().getTerritories().getDefaultProfit()==null && gameDescriptor.getGame().getTerritories().getDefaultArmyThreshold()==null)
        {
            defaultProfit=-1;
            defaultArmyThreshold=-1;

        }
        else
        {
            defaultProfit=gameDescriptor.getGame().getTerritories().getDefaultProfit().intValue();
            defaultArmyThreshold=gameDescriptor.getGame().getTerritories().getDefaultArmyThreshold().intValue();
        }
        counterOfRounds=0;
        numberOfPlayers=2;
        counterPlayers=numberOfPlayers;
        players=new ArrayList<GamePlayer>(numberOfPlayers);
        typeCurrentPlayer= GameTerritory.TypeOfTerritory.X;
        initPlayersList();
        infoArmy=new ArrayList<ArmyUnit>(gameDescriptor.getGame().getArmy().getUnit().size());
        initUnitsList();
        fillDatainBoard();
    }


    public void writeToFile(String myFileName) throws IOException
    {

        File myFile = new File(myFileName);
        myFile.createNewFile();
        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                             new FileOutputStream(myFile))) {
            out.writeObject(this);
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GameEngine readFromFile(String myFileName) throws IOException, ClassNotFoundException
    {
        GameEngine gameFromFile = new GameEngine();
        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new FileInputStream(myFileName))) {

            gameFromFile = (GameEngine) in.readObject();

        }
        return gameFromFile;
    }

    private void initPlayersList()
    {
        for (int i=0;i<2;i++)
        {
            players.add(new GamePlayer());
            players.get(i).setTurings(gameDescriptor.getGame().getInitialFunds().intValue());
            players.get(i).setNumberOfTerritories(0);
            if( i == 0 )
            {
                players.get(i).setType(GameTerritory.TypeOfTerritory.X );
            }
            else
            {
                players.get(i).setType(GameTerritory.TypeOfTerritory.Y );

            }
        }
    }

    private void initUnitsList() {
        for(int i=0;i<gameDescriptor.getGame().getArmy().getUnit().size();i++)
        {
            infoArmy.add(new ArmyUnit());
            infoArmy.get(i).setCompetenceReduction(gameDescriptor.getGame().getArmy().getUnit().get(i).getCompetenceReduction().intValue());
            infoArmy.get(i).setMaxFirePower(gameDescriptor.getGame().getArmy().getUnit().get(i).getMaxFirePower().intValue());
            infoArmy.get(i).setRank(gameDescriptor.getGame().getArmy().getUnit().get(i).getRank());
            infoArmy.get(i).setPurchase(gameDescriptor.getGame().getArmy().getUnit().get(i).getPurchase().doubleValue());
            infoArmy.get(i).setType(gameDescriptor.getGame().getArmy().getUnit().get(i).getType());
        }
    }


    private void initTerritoryList() {
        for(int i=0;i<gameDescriptor.getGame().getTerritories().getTeritory().size();i++)
        {
            territories.add(new GameTerritory());
            territories.get(i).setId(gameDescriptor.getGame().getTerritories().getTeritory().get(i).getId().intValue());
            if(gameDescriptor.getGame().getTerritories().getTeritory().get(i).getProfit()==null && gameDescriptor.getGame().getTerritories().getTeritory().get(i).getArmyThreshold()==null)
            {
                territories.get(i).setProfit(-1);
                territories.get(i).setArmyThreshold(-1);
            }
            else
            {
                territories.get(i).setProfit(gameDescriptor.getGame().getTerritories().getTeritory().get(i).getProfit().intValue());
                territories.get(i).setArmyThreshold(gameDescriptor.getGame().getTerritories().getTeritory().get(i).getArmyThreshold().intValue());
            }
        }
    }

    public void calculateTuringsForPlayer(GamePlayer currentPlayer) {
        GameTerritory.TypeOfTerritory typeOfPlayer = currentPlayer.getType();
        for (int i = 0; i < getBoard().getRows(); i++) {
            for (int j = 0; j < getBoard().getColumns(); j++) {
                if (getBoard().getBoardGame()[i][j].getType() == typeOfPlayer) {
                    currentPlayer.setTurings(getBoard().getBoardGame()[i][j].getProfit() + currentPlayer.getTurings());
                }
            }
        }
    }

    public void calculateLostPowerAndTerrirories(GamePlayer currentPlayer) {
        GameTerritory.TypeOfTerritory typeOfPlayer = currentPlayer.getType();
        List<ArmyUnit> needsToRemove=new ArrayList<ArmyUnit>();
        for (int i = 0; i < getBoard().getRows(); i++)
        {
            for (int j = 0; j < getBoard().getColumns(); j++)
            {
                GameTerritory currentTerritory = getBoard().getBoardGame()[i][j];
                if (currentTerritory.getType() == typeOfPlayer)
                {
                    for(int k=0;k<currentTerritory.getUnitInTerritory().size();k++)
                    {
                        manageUnitToRemoveAfterLostPower(currentTerritory,needsToRemove,k);
                    }

                    for(int k=0;k<needsToRemove.size();k++)
                    {
                        currentTerritory.getUnitInTerritory().remove(needsToRemove.get(k));
                    }
                    if(currentTerritory.getCurrentPower()<currentTerritory.getArmyThreshold())
                    {
                        manageLostTerritoryForPlayer(currentPlayer,currentTerritory);
                    }
                }
            }
        }
    }

    private void manageUnitToRemoveAfterLostPower(GameTerritory currentTerritory, List<ArmyUnit> needsToRemove, int k) {
        currentTerritory.getUnitInTerritory().get(k).setCurrentPower(currentTerritory.getUnitInTerritory().get(k).getCurrentPower()-currentTerritory.getUnitInTerritory().get(k).getCompetenceReduction());
        currentTerritory.setCurrentPower(currentTerritory.getCurrentPower()-currentTerritory.getUnitInTerritory().get(k).getCompetenceReduction());

        if (currentTerritory.getUnitInTerritory().get(k).getCurrentPower() <= 0) {
            needsToRemove.add(currentTerritory.getUnitInTerritory().get(k));
        }
    }

    private void manageLostTerritoryForPlayer(GamePlayer currentPlayer, GameTerritory currentTerritory) {
        currentPlayer.setNumberOfTerritories(currentPlayer.getNumberOfTerritories()-1);
        currentTerritory.setCurrentPower(0);
        currentTerritory.setUnitInTeritory(new ArrayList<ArmyUnit>());
        currentTerritory.setType(GameTerritory.TypeOfTerritory.N);
        currentPlayer.setTurings(currentPlayer.getTurings()-currentTerritory.getProfit());
    }

    public double calculateTuringToGetMaxFirePower(GameTerritory territory) {
        int result=0;

        for (int k = 0; k < territory.getUnitInTerritory().size(); k++)
        {
            result+=territory.getUnitInTerritory().get(k).getMaxFirePower()-territory.getUnitInTerritory().get(k).getCurrentPower();
        }

        double worthOfSingle=territory.getUnitInTerritory().get(0).getPurchase()/territory.getUnitInTerritory().get(0).getMaxFirePower();
        return result*worthOfSingle;
    }

    public void passTurn()
    {
        setCounterPlayers(getCounterPlayers()-1);
        if(getTypeCurrentPlayer() == GameTerritory.TypeOfTerritory.X)
        {
            setTypeCurrentPlayer(GameTerritory.TypeOfTerritory.Y);
        }
        else
        {
            setTypeCurrentPlayer(GameTerritory.TypeOfTerritory.X);
        }
    }
    public boolean maintainArmy(GameTerritory territory )
    {

        double number=calculateTuringToGetMaxFirePower(territory);
        GamePlayer currentPlayer=getCurrentPlayer();
        int addPower;
        boolean result;
        if(number<=currentPlayer.getTurings())
        {
            for(int i=0;i<territory.getUnitInTerritory().size();i++)
            {
                addPower=territory.getUnitInTerritory().get(i).getMaxFirePower()-territory.getUnitInTerritory().get(i).getCurrentPower();
                territory.getUnitInTerritory().get(i).setCurrentPower(territory.getUnitInTerritory().get(i).getCurrentPower()+addPower);
            }

            result=true;
            currentPlayer.setTurings(currentPlayer.getTurings()-number);
        }

        else
        {
            result=false;
        }

        return result;
    }

    public GamePlayer getCurrentPlayer()
    {
        int numberPlayer = getTypeCurrentPlayer().ordinal();
        GamePlayer currentPlayer = getPlayers().get(numberPlayer);
        return currentPlayer;
    }

    public boolean checkProximity(int[] rowAndCol) {
        GameTerritory[][] board= getBoard().getBoardGame();
        GameTerritory.TypeOfTerritory type = getCurrentPlayer().getType();
        int row = rowAndCol[0];
        int col = rowAndCol[1];

        //corners

        //row = 0 , col = 0
        if(rowAndCol[0]==0 && rowAndCol[1]== 0)
        {
            if(board[row+1][col].getType() == type || board[row][col+1].getType() == type || board[0][0].getType() == type)
                return true;
        }

        //row = rows  , col= columns
        else if(rowAndCol[0]==getBoard().getRows()-1 && rowAndCol[1]== getBoard().getColumns()-1)
        {
            if(board[row-1][col].getType() == type || board[row][col-1].getType() == type || board[row][col].getType()==type)
                return true;
        }

        //row = rows ,col=0
        else if(rowAndCol[0]== getBoard().getRows()-1&& rowAndCol[1]==0)
        {
            if(board[row-1][col].getType() == type || board[row][col+1].getType() == type || board[row][col].getType()==type)
                return true;
        }

        //row=0, col=columns
        else if(rowAndCol[1]== getBoard().getColumns()-1&& rowAndCol[0]==0)
        {
            if(board[row+1][col].getType() == type || board[row][col-1].getType() == type|| board[row][col].getType()==type)
                return true;
        }

        //col=0
        else if(rowAndCol[1]== 0)
        {
            if(board[row+1][col].getType() == type || board[row-1][col].getType() == type || board[row][col+1].getType() == type || board[row][col].getType()==type)
                return true;
        }

        //row=0
        else if(rowAndCol[0]== 0)
        {
            if(board[row][col+1].getType() == type || board[row][col-1].getType() == type || board[row+1][col].getType() == type || board[row][col].getType()==type)
                return true;
        }

        //col= columns
        else if(rowAndCol[1]== getBoard().getColumns()-1)
        {
            if(board[row][col-1].getType() == type || board[row+1][col].getType() == type || board[row-1][col].getType() == type|| board[row][col].getType()==type)
                return true;
        }

        //row=rows
        else if(rowAndCol[0]== getBoard().getRows()-1)
        {
            if(board[row-1][col].getType() == type || board[row][col+1].getType() == type || board[row][col-1].getType() == type || board[row][col].getType()==type)
                return true;
        }

        else if(board[row-1][col].getType() == type || board[row+1][col].getType() == type || board[row][col+1].getType() == type || board[row][col-1].getType() == type || board[row][col].getType()==type)
        {
            return true;
        }

        return false;
    }

    public static generate.GameDescriptor fromXmlFileToObject(Path path) {

        generate.GameDescriptor game=null;
        try {
            File file = new File(path.toString());
            JAXBContext jaxbContext = JAXBContext.newInstance(generate.GameDescriptor.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            game = (generate.GameDescriptor) jaxbUnmarshaller.unmarshal(file);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return game;
    }

    public boolean manageEnoughMoneyToBuy(GameTerritory currentTer, int numberOfUnits, int powerCurrentTerritory, GamePlayer currentPlayer, int[] cellTerritory)
    {
        int powerCurrentPlayer = numberOfUnits * getInfoArmy().get(0).getMaxFirePower();
        boolean winnerCurrent = false;
        boolean strongCurrent = false;
        boolean attackSucc=false;

        if (currentTer.getArmyThreshold() < powerCurrentPlayer)
        {
            double sum = powerCurrentPlayer + powerCurrentTerritory;
            double checkProb1 =powerCurrentPlayer / sum;
            double checkProb2 =powerCurrentTerritory / sum;
            double number = Math.random();
            if (powerCurrentPlayer > powerCurrentTerritory)
            {
                strongCurrent = true;
                if (number < checkProb1) {
                    winnerCurrent = true;
                }
            }
            else
                {
                if (number > checkProb2) {
                    winnerCurrent = true;
                }
            }

            if (winnerCurrent == true)
            {
                attackSucc=manageWinnerCurrentT(currentTer,powerCurrentPlayer,strongCurrent,powerCurrentTerritory,numberOfUnits,currentPlayer,cellTerritory);
            }
            else
            {

                manageWinnerCurrentF(strongCurrent,currentTer,powerCurrentTerritory,powerCurrentPlayer);

            }
        }

        return attackSucc;
    }

    private void manageWinnerCurrentF(boolean strongCurrent, GameTerritory currentTer, int powerCurrentTerritory, int powerCurrentPlayer) {
        for (int i = 0; i < currentTer.getUnitInTerritory().size(); i++) {
            if (strongCurrent == false) {
                currentTer.getUnitInTerritory().get(i).setCurrentPower((int) Math.ceil(powerCurrentTerritory / powerCurrentPlayer) * currentTer.getUnitInTerritory().get(i).getMaxFirePower());
            } else {
                currentTer.getUnitInTerritory().get(i).setCurrentPower((int) Math.ceil(0.5 * currentTer.getUnitInTerritory().get(i).getMaxFirePower()));

            }
            currentTer.setCurrentPower(currentTer.getCurrentPower() + currentTer.getUnitInTerritory().get(i).getCurrentPower());

        }


        if (currentTer.getCurrentPower() < currentTer.getArmyThreshold()) {
            currentTer.setUnitInTeritory(new ArrayList<ArmyUnit>());
            currentTer.setCurrentPower(0);
            currentTer.setType(GameTerritory.TypeOfTerritory.N);
        }
    }

    private boolean manageWinnerCurrentT(GameTerritory currentTer, int powerCurrentPlayer, boolean strongCurrent, int powerCurrentTerritory, int numberOfUnits, GamePlayer currentPlayer, int[] cellTerritory)
    {
        boolean attackSucc=false;
        if (powerCurrentPlayer >= currentTer.getArmyThreshold())
        {

            currentTer.setUnitInTeritory(new ArrayList<ArmyUnit>());
            currentTer.setCurrentPower(0);
            for (int i = 0; i < numberOfUnits; i++) {
                currentTer.getUnitInTerritory().add(new ArmyUnit(getInfoArmy().get(0).getPurchase(), getInfoArmy().get(0).getMaxFirePower(), getInfoArmy().get(0).getCompetenceReduction(), getInfoArmy().get(0).getRank(), getInfoArmy().get(0).getType()));
                if (strongCurrent == true)
                {
                    double prob = (double)(powerCurrentTerritory) / (double)(powerCurrentPlayer);
                    currentTer.getUnitInTerritory().get(i).setCurrentPower((int) Math.ceil(prob) * currentTer.getUnitInTerritory().get(i).getMaxFirePower());
                }
                else
                {
                    currentTer.getUnitInTerritory().get(i).setCurrentPower((int) Math.ceil(0.5 * currentTer.getUnitInTerritory().get(i).getMaxFirePower()));
                }
                currentTer.setCurrentPower(currentTer.getCurrentPower() + currentTer.getUnitInTerritory().get(i).getCurrentPower());
            }

            if (currentTer.getCurrentPower() < currentTer.getArmyThreshold())
            {
                manageMakeTerritoryNeutral(currentTer);
            }
            else
            {
                attackSucc=true;
                manageMoveTerritoryToOtherPlayer(currentPlayer,currentTer,numberOfUnits,cellTerritory);
            }

            for (int j = 0; j < getPlayers().size(); j++) {
                if (currentTer.getType() == getPlayers().get(j).getType()) {
                    getPlayers().get(j).setNumberOfTerritories(currentPlayer.getNumberOfTerritories() - 1);
                    getPlayers().get(j).setTurings(getPlayers().get(j).getTurings() - currentTer.getProfit());
                }
            }
        }

        return attackSucc;
    }

    public void manageMoveTerritoryToOtherPlayer(GamePlayer currentPlayer, GameTerritory currentTer, int numberOfUnits, int[] cellTerritory) {
        currentPlayer.setNumberOfTerritories(currentPlayer.getNumberOfTerritories() + 1);
        currentTer.setType(currentPlayer.getType());
        getBoard().getBoardGame()[cellTerritory[0]][cellTerritory[1]].setType(currentPlayer.getType());
        currentPlayer.setTurings(currentPlayer.getTurings() - numberOfUnits * getInfoArmy().get(0).getPurchase());
    }

    public void manageMakeTerritoryNeutral(GameTerritory currentTer) {
        currentTer.setUnitInTeritory(new ArrayList<ArmyUnit>());
        currentTer.setCurrentPower(0);
        currentTer.setType(GameTerritory.TypeOfTerritory.N);
    }

    public List<GamePlayer> getPlayers() {
        return players;
    }

    public int getTotalCycles() {
        return totalCycles;
    }

    public void setCounterPlayers(int counterPlayers) {
        this.counterPlayers = counterPlayers;
    }

    public int getCounterPlayers() {
        return counterPlayers;
    }

    public void setCounterOfRounds(int counterOfRounds) {
        this.counterOfRounds = counterOfRounds;
    }

    public void setTypeCurrentPlayer(GameTerritory.TypeOfTerritory typeCurrentPlayer) {
        this.typeCurrentPlayer = typeCurrentPlayer;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public GameTerritory.TypeOfTerritory getTypeCurrentPlayer() {
        return typeCurrentPlayer;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public GameBoard getBoard() {
        return board;
    }

    public List<ArmyUnit> getInfoArmy() {
        return infoArmy;
    }

    public int getCounterOfRounds() {
        return counterOfRounds;
    }

    public GameTerritory.TypeOfTerritory checkTheWinner()
    {
        int sumPlayerX=0;
        int sumPlayerY=0;

        for(int i=0; i<board.getRows();i++) {
            for (int j = 0; j < board.getColumns(); j++)
            {
                GameTerritory curretTer = board.getBoardGame()[i][j];
                if(curretTer.getType() == GameTerritory.TypeOfTerritory.X)
                {
                    sumPlayerX+=curretTer.getProfit();
                }
                else if(curretTer.getType() == GameTerritory.TypeOfTerritory.Y)
                {
                    sumPlayerY+=curretTer.getProfit();
                }
            }
        }
        if (sumPlayerX > sumPlayerY)
        {
            return GameTerritory.TypeOfTerritory.X;
        }
        else if(sumPlayerY>sumPlayerX) {
            return GameTerritory.TypeOfTerritory.Y;
        }
      return GameTerritory.TypeOfTerritory.N;
    }

    private void fillDatainBoard()
    {
        int numberCell;
        int counter=0;
        int id=territories.get(counter).getId();

        for(int i=0; i<board.getRows();i++)
        {
            for(int j=0;j<board.getColumns();j++)
            {
                numberCell= board.getBoardGame()[0].length * i + j;
                if(territories.size()>counter)
                {
                    id = territories.get(counter).getId();
                }

                if((numberCell+1) == id )
                {
                    int profit = territories.get(counter).getProfit();
                    int armyThreshold = territories.get(counter).getArmyThreshold();
                    board.getBoardGame()[i][j].setId(id);
                    board.getBoardGame()[i][j].setProfit(profit);
                    board.getBoardGame()[i][j].setArmyThreshold(armyThreshold);
                    counter++;

                }

                else
                {
                    board.getBoardGame()[i][j].setId(numberCell);
                    board.getBoardGame()[i][j].setProfit(defaultProfit);
                    board.getBoardGame()[i][j].setArmyThreshold(defaultArmyThreshold);
                }
            }
        }
    }
}

