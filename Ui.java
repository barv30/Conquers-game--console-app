package Ui;

import conquers.ArmyUnit;
import conquers.GameEngine;
import conquers.GamePlayer;
import conquers.GameTerritory;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Ui {

    private GameEngine theGameEngine;
    private Scanner gameInput = new Scanner(System.in);
    private boolean fileLoaded = false;
    private int rows ;
    private int columns ;
    private Path path;
    private int numberOfRoundUpload=-1;

    public Ui() throws IOException, ClassNotFoundException {
        theGameEngine = new GameEngine();
        showMainMenu();
    }


    public void printCurrentBoard(GameEngine game) {

        System.out.print("       "+1 + "       ");
        for (int i = 2; i < game.getBoard().getColumns() + 1; i++)
        {
            if (i < 10)
            {
                System.out.print( i + "        ");
            }
            else
                System.out.print( i +"       ");


        }
        System.out.print( "\n  ");

        for (int j = 0; j < game.getBoard().getRows() ; j++)
        {

            for (int k = 0; k < game.getBoard().getColumns() ; k++) {
                System.out.print("---------");
            }

            System.out.println("-");

            if(j+1<10)
                System.out.print (" "+(j + 1));
            else
                System.out.print((j + 1));

            for (int l = 0; l < game.getBoard().getColumns()  ; l++) {
                GameTerritory cell = game.getBoard().getBoardGame()[j][l];

                if ((game.getBoard().getColumns()  * j + l+1) < 10 )
                {
                    System.out.print( "|00" +(game.getBoard().getColumns()  * j + l+1)+" "+cell.getType()+"   ");
                }
                else if((game.getBoard().getColumns()  * j + l+1) >= 10 && (game.getBoard().getColumns()  * j + l+1) < 100)
                    System.out.print ( "|0" + (game.getBoard().getColumns()  * j + l+1) + " "+cell.getType()+"   ");
                else
                    System.out.print( "|" + (game.getBoard().getColumns()  * j + l+1) + " "+cell.getType()+"   ");
            }

            System.out.print( "| \n");

            for (int t = 0; t <game.getBoard().getColumns()  ; t++) {
                GameTerritory cell = game.getBoard().getBoardGame()[j][t];
                int numberOfDigit=calculateDigits(cell.getProfit());
                System.out.print("  |"+cell.getProfit());
                for(int i=0;i<6-numberOfDigit;i++)
                {
                    System.out.print(" ");
                }
            }

            System.out.print("  | \n ");

            for (int t = 0; t <game.getBoard().getColumns()  ; t++) {

                GameTerritory cell = game.getBoard().getBoardGame()[j][t];
                int numberOfDigit=calculateDigits(cell.getArmyThreshold());
                System.out.print( " |"+cell.getArmyThreshold());
                for(int i=0;i<7-numberOfDigit;i++)
                {
                    System.out.print(" ");
                }
            }

            System.out.print(" | \n ");
        }

        for (int k = 0; k < game.getBoard().getColumns() ; k++) {
            System.out.print("---------");
        }

        System.out.println("-");

    }

    private void showHistory() throws IOException, ClassNotFoundException {
        File file = new File(path.toString());
        String myDir=file.getParent();

        GameEngine currentGame;
        if(numberOfRoundUpload == -1)
        {
            for(int i=0;i<theGameEngine.getCounterOfRounds();i++)
            {
                String myFileName = myDir.concat("/round"+i+".bin");
                currentGame=GameEngine.readFromFile(myFileName);
                showStatus(currentGame);

            }
        }
      else
        {
            if(numberOfRoundUpload == theGameEngine.getCounterOfRounds())
            {
                System.out.println("Sorry, you can show history just after the round over.");
            }
            for(int i=numberOfRoundUpload;i<theGameEngine.getCounterOfRounds();i++)
            {
                String myFileName = myDir.concat("/round"+i+".bin");
                currentGame=GameEngine.readFromFile(myFileName);
                showStatus(currentGame);

            }
        }

    }

    private int calculateDigits(int number) {
        int numberOfDigit=1;
        int temp=number;
        temp=temp/10;
        while(temp>0)
        {
            temp=temp/10;
            numberOfDigit++;
        }

        return numberOfDigit;
    }


    private void showMainMenu() throws IOException, ClassNotFoundException {
        int userChoice = 0;
        System.out.println("Please select your choice: ");
        System.out.println(" 1. Read data game file \n 2. Start game \n 3. Show status game \n 4. Start your turn \n 5. Show history \n 6. Exit \n 7. Cancel last round\n 8. Save the game\n 9. Upload game");

        try {
            if(gameInput.hasNextInt())
            {
                userChoice = gameInput.nextInt();
            }
            else
            {
                gameInput.next();
            }

            if( userChoice < 1 || userChoice > 9)
            {
                checkValidNumber();
            }
        }
        catch(Exception e){
            showMainMenu();
        }

        manageValidationUserChoice(userChoice);
        manageGame(userChoice);
    }

    private void manageGame(int userChoice) throws IOException, ClassNotFoundException {
        switch(userChoice)
        {
            case 1:
                if(fileLoaded == true )
                {
                    System.out.println("Error: you already loaded a file ");
                    showMainMenu();
                }
                showStatus(theGameEngine);
                showMainMenu();
                break;
            case 2:
                if(theGameEngine.gameStarted == true)
                {
                    System.out.println("Are you sure you want new game? Y/N");
                    String yesNo=gameInput.next();
                    String yesNoUp=yesNo.toUpperCase();

                    if(!yesNoUp.equals("Y") &&  !yesNoUp.equals("N"))
                    {
                        System.out.println("Error:you need to enter Y/N");
                        manageGame(2);
                    }
                    switch(yesNoUp) {
                        case "Y":
                            theGameEngine.initGameEngine();
                            File file = new File(path.toString());
                            String myDir=file.getParent();
                            String myFileName = myDir.concat("/round"+0+".bin");
                            theGameEngine.writeToFile(myFileName);
                            System.out.println("New game started");
                            theGameEngine.setCounterOfRounds(1);
                            showMainMenu();

                        default:
                            showMainMenu();

                    }
                }

                theGameEngine.gameStarted = true;
                File file = new File(path.toString());
                String myDir=file.getParent();
                String myFileName = myDir.concat("/round"+0+".bin");
                theGameEngine.writeToFile(myFileName);
                System.out.println("The game started! good luck");
                theGameEngine.setCounterOfRounds(theGameEngine.getCounterOfRounds()+1);
                showMainMenu();
                break;
            case 3:
                showStatus(theGameEngine);
                showMainMenu();
                break;
            case 4:
                startNewRound();
                break;
            case 5:
                if(theGameEngine.getCounterOfRounds() <2)
                    System.out.println("Sorry, you can show history just after the round over.");
                else
                    showHistory();
                showMainMenu();
                break;
            case 7:
                if(theGameEngine.getCounterPlayers()!=2)
                    System.out.println("Sorry, you can't cancel round. just after the round over.");
                else
                    cancelRound();
                showMainMenu();
                break;
            case 8:
                saveTheGame();
                showMainMenu();
                break;
            case 9:
                uploadGameFromFile();
                showMainMenu();
                break;
        }
    }

    private void manageValidationUserChoice(int userChoice) throws IOException, ClassNotFoundException {
        switch(userChoice)
        {
            case 1:
                if(theGameEngine.gameStarted == true || fileLoaded == true)
                {
                    System.out.println("Error: you already loaded a file ");
                    showMainMenu();
                }
                else
                {
                    checkXML();
                    showStatus(theGameEngine);
                    System.out.println("Do you want to upload other file ? Y/N");
                    String answer=gameInput.next();
                    String answerUp=answer.toUpperCase();

                    while(!answerUp.equals("Y") &&  !answerUp.equals("N")) {
                        System.out.println("Error:you need to enter Y/N");
                        System.out.println("Do you want to upload other file ? Y/N");
                        answer = gameInput.next();
                        answerUp = answer.toUpperCase();

                    }
                    if(answerUp.equals("N")) {
                        fileLoaded = true;
                    }
                    showMainMenu();
                }

                break;

            case 6:
                if(fileLoaded == true) {
                File file = new File(path.toString());
                String myDir = file.getParent();
                for (int i = 0; i <= theGameEngine.getCounterOfRounds(); i++) {
                    String myFileName = myDir.concat("/round" + i + ".bin");
                    File myfile = new File(myFileName);
                    myfile.delete();
                }
            }
                System.exit(0) ;
                break;
            default:
                if(fileLoaded == false && userChoice !=9)
                {
                    System.out.println("Error: you didn't loaded a file ");
                    showMainMenu();
                }
                if((userChoice==3 || userChoice==4 || userChoice==5 || userChoice==7 || userChoice == 8 )&&theGameEngine.gameStarted == false)
                {
                    System.out.println("Error: you didn't start the game yet");
                    showMainMenu();
                }
        }
    }

    private void cancelRound() throws IOException, ClassNotFoundException {
        if(theGameEngine.getCounterOfRounds()<2 || numberOfRoundUpload ==theGameEngine.getCounterOfRounds())
        {
            System.out.println("Not enough rounds");
            showMainMenu();
        }
        else if(numberOfRoundUpload!=-1&&theGameEngine.getCounterOfRounds()==numberOfRoundUpload+1) {
            File file = new File(path.toString());
            String myDir = file.getParent();
            String myFileName = myDir.concat("/round" + numberOfRoundUpload + ".bin");
            theGameEngine = GameEngine.readFromFile(myFileName);
        }
        else {
            File file = new File(path.toString());
            String myDir = file.getParent();
            int counterOfRounds = theGameEngine.getCounterOfRounds();
            String myFileName = myDir.concat("/round" + (counterOfRounds - 2) + ".bin");
            String fileToDelete = myDir.concat("/round" + (counterOfRounds - 1) + ".bin");
            File myfile = new File(fileToDelete);
            myfile.delete();
            theGameEngine = GameEngine.readFromFile(myFileName);
        }
        theGameEngine.setCounterPlayers(2);
        if(theGameEngine.getCounterOfRounds() ==0)
            theGameEngine.setCounterOfRounds(1);
        showStatus(theGameEngine);
    }

    private void startNewRound() throws IOException, ClassNotFoundException {
        printInformationForPlayerInNewRound();
        checkValidOfChoiceRound();
    }

    private void printInformationForPlayerInNewRound()
    {
        GamePlayer currentPlayer = theGameEngine.getCurrentPlayer();
        System.out.println("Player " + currentPlayer.getType() + ":");
        theGameEngine.calculateTuringsForPlayer(currentPlayer);
        System.out.println("Amount of turings before calculate: " + currentPlayer.getTurings());
        theGameEngine.calculateLostPowerAndTerrirories(currentPlayer);
        System.out.println("Amount of turings after calculate: " + currentPlayer.getTurings());
        checksTerritoriesOfCurrentPlayer(currentPlayer);

    }

    private void checksTerritoriesOfCurrentPlayer(GamePlayer currentPlayer) {
        GameTerritory.TypeOfTerritory typeOfPlayer = currentPlayer.getType();
        int counterS=0;
        double counterTuring=0;
        System.out.println("Number of territories: "+ currentPlayer.getNumberOfTerritories());
        for (int i = 0; i < theGameEngine.getBoard().getRows(); i++)
        {
            for (int j = 0; j < theGameEngine.getBoard().getColumns(); j++)
            {
                GameTerritory currentTerritory = theGameEngine.getBoard().getBoardGame()[i][j];
                if (currentTerritory.getType() == typeOfPlayer)
                {
                    System.out.println("Territory id: "+ currentTerritory.getId());
                    System.out.println("Army Threshold: "+ currentTerritory.getArmyThreshold());
                    counterS=0;
                    for (int k = 0; k < currentTerritory.getUnitInTerritory().size(); k++)
                    {
                        if(currentTerritory.getUnitInTerritory().get(k).getType().toUpperCase().equals("SOLDIER"))
                        {
                            counterS++;
                        }
                    }

                    counterTuring = theGameEngine.calculateTuringToGetMaxFirePower(currentTerritory);
                    System.out.println("Number of Soldier type is: " +counterS);
                    System.out.println("Number of turing to get max fire power is : " +counterTuring);

                }
            }
        }

    }

    private void checkValidOfChoiceRound() throws IOException, ClassNotFoundException {
        int userChoice=0;
        System.out.println("Choose your move: \n1.Do nothing \n2.Do something");
        try
        {
            userChoice = gameInput.nextInt();
            if(userChoice < 1 || userChoice > 2)
            {
                System.out.println("Error:you need to enter number 1/2");
                checkValidOfChoiceRound();
            }

        }
        catch (Exception e)
        {
            System.out.println("Error:you need to enter number 1/2");
            checkValidOfChoiceRound();
        }

        playRound(userChoice);
    }

    private void playRound(int userChoice) throws IOException, ClassNotFoundException {
        GamePlayer currentPlayer=theGameEngine.getCurrentPlayer();

        if (userChoice == 2)
        {
            int[] cellTerritory = new int[2];
            cellTerritory= getValidRowAndCol();
            while(cellTerritory == null)
                cellTerritory=getValidRowAndCol();
            checkTypeOfTerritory(cellTerritory);
        }

        System.out.println("Amount of turings after calculate: " + currentPlayer.getTurings());
        checksTerritoriesOfCurrentPlayer(currentPlayer);

        theGameEngine.passTurn();
        System.out.println("Your Turn is over");

        if(theGameEngine.getCounterPlayers()==0)
        {
            System.out.println("Round "+(theGameEngine.getCounterOfRounds())+" is over");
            File file = new File(path.toString());
            String myDir=file.getParent();
            String myFileName = myDir.concat("/round"+theGameEngine.getCounterOfRounds()+".bin");
            theGameEngine.writeToFile(myFileName);
        }
        else
        {
            showMainMenu();
        }

        theGameEngine.setCounterPlayers(theGameEngine.getNumberOfPlayers());
        theGameEngine.setCounterOfRounds(theGameEngine.getCounterOfRounds()+1);

        if(theGameEngine.getCounterOfRounds() <= theGameEngine.getTotalCycles())
        {
            showMainMenu();
        }
        else
        {
            File file = new File(path.toString());
            String myDir=file.getParent();
            for(int i =0;i<=theGameEngine.getTotalCycles();i++) {
                String myFileName = myDir.concat("/round"+i+".bin");
                File myfile = new File(myFileName);
                myfile.delete();
            }

            GameTerritory.TypeOfTerritory typeOfwinner = theGameEngine.checkTheWinner();
            if( typeOfwinner == GameTerritory.TypeOfTerritory.N) {

                System.out.println("There is a tie");
            }
            else {
                System.out.println("The winner is: player" + typeOfwinner);
            }
            exitGame();
        }
    }

    private void exitGame() throws IOException, ClassNotFoundException {
        System.out.println("Do you want to play again? Y/N");
        String userChoice=gameInput.next();
        String userChoiceUp=userChoice.toUpperCase();

        if(!userChoiceUp.equals("Y") &&  !userChoiceUp.equals("N"))
        {
            System.out.println("Error:you need to enter Y/N");
            exitGame();
        }
        switch(userChoiceUp) {
            case "Y":
                manageStartNewGame();
                break;
            case "N":
                System.out.println("Bye Bye !");
                System.exit(0);

        }

    }

    private void manageStartNewGame() throws IOException, ClassNotFoundException {
        System.out.println("Same game? Y/N");
        String sameGame=gameInput.next();
        String sameGameUp=sameGame.toUpperCase();
        if(!sameGameUp.equals("Y") &&  !sameGameUp.equals("N"))
        {
            System.out.println("Error:you need to enter Y/N");
            manageStartNewGame();
        }

        switch(sameGameUp)
        {
            case "Y":
                theGameEngine.initGameEngine();
                theGameEngine.setCounterOfRounds(1);
                System.out.println("The game started! good luck");
                break;
            case "N":
                fileLoaded=false;
                theGameEngine=new GameEngine();
                break;
        }
        showMainMenu();
    }



    private void checkTypeOfTerritory(int[] cellTerritory) throws IOException, ClassNotFoundException {
        GameTerritory.TypeOfTerritory type = theGameEngine.getBoard().getBoardGame()[cellTerritory[0]][cellTerritory[1]].getType();
        GamePlayer currentPlayer= theGameEngine.getCurrentPlayer();
        GameTerritory.TypeOfTerritory typeOfPlayer= currentPlayer.getType();
        int row=cellTerritory[0];
        int col=cellTerritory[1];
        GameTerritory currentTer = theGameEngine.getBoard().getBoardGame()[row][col];

        if(type == typeOfPlayer)
        {
            chosenBelongingTerritory(currentTer,cellTerritory);
        }
        else if(type == GameTerritory.TypeOfTerritory.N)
        {
            int minimun= theGameEngine.getBoard().getBoardGame()[cellTerritory[0]][cellTerritory[1]].getArmyThreshold();
            System.out.println("Minimun power for this territory: "+ minimun);
            chosenNeutralTerritory(currentTer,cellTerritory);

        }
        else
        {
            boolean attackSucc=chosenRivalTerritory(currentTer,cellTerritory);
            if(attackSucc==true)
            {
                System.out.println("Your attack succeed");
            }
            else
            {
                System.out.println("Sorry, you lose the attack.");
            }
        }
    }

    private void buyArmy(GameTerritory territory, int [] cellTerritory)
    {
        informationAboutArmy();
        GamePlayer currentPlayer=theGameEngine.getCurrentPlayer();
        String input=askTheUserWhichArmyAndHowMany();

        try
        {
            int numberOfUnits = gameInput.nextInt();

            if(currentPlayer.getTurings()>= numberOfUnits * theGameEngine.getInfoArmy().get(0).getPurchase())
            {
                if(territory.getArmyThreshold() < numberOfUnits*theGameEngine.getInfoArmy().get(0).getMaxFirePower())
                {
                    for(int i=0;i<numberOfUnits;i++)
                    {
                        territory.getUnitInTerritory().add(new ArmyUnit(theGameEngine.getInfoArmy().get(0).getPurchase(),theGameEngine.getInfoArmy().get(0).getMaxFirePower(),theGameEngine.getInfoArmy().get(0).getCompetenceReduction(),theGameEngine.getInfoArmy().get(0).getRank(),theGameEngine.getInfoArmy().get(0).getType()));
                        territory.setCurrentPower(territory.getCurrentPower() +theGameEngine.getInfoArmy().get(0).getMaxFirePower() );
                    }

                    territory.setType(currentPlayer.getType());
                    GameTerritory territoryInBoard = theGameEngine.getBoard().getBoardGame()[cellTerritory[0]][cellTerritory[1]];
                    territoryInBoard.setType(currentPlayer.getType());
                    currentPlayer.setNumberOfTerritories(currentPlayer.getNumberOfTerritories()+1);
                }

            }
            else
            {
                System.out.println("Sorry, you dont have enough turings");
                checkValidOfChoiceRound();
            }

            currentPlayer.setTurings(currentPlayer.getTurings()-numberOfUnits * theGameEngine.getInfoArmy().get(0).getPurchase());

        }
        catch (Exception e)
        {
            System.out.println("Error:you need to enter number");
            buyArmy(territory, cellTerritory);
        }
    }

    private void informationAboutArmy()
    {
        System.out.println("The details about the Army");
        for(int i=0;i<theGameEngine.getInfoArmy().size();i++)
        {
            System.out.println("Type: "+theGameEngine.getInfoArmy().get(i).getType());
            System.out.println("Price " + theGameEngine.getInfoArmy().get(i).getPurchase());
            System.out.println("Max fire power: "+theGameEngine.getInfoArmy().get(i).getMaxFirePower());
            System.out.println();
        }

    }

    private void chosenNeutralTerritory(GameTerritory currentTer, int[] cellTerritory) {

        informationAboutArmy();
        System.out.println("Do you want to control this territory? Y/N");
        String userChoice=gameInput.next();
        String userChoiceUp=userChoice.toUpperCase();

        if(!userChoiceUp.equals("Y") &&  !userChoiceUp.equals("N"))
        {
            System.out.println("Error:you need to enter Y/N");
            chosenNeutralTerritory(currentTer, cellTerritory);
        }

        switch(userChoiceUp) {
            case "Y":
                buyArmy(currentTer,cellTerritory);
                break;
            case "N":
                System.out.println("Ok, you gave up your turn");
        }

    }

    private boolean chosenRivalTerritory(GameTerritory currentTer, int[] cellTerritory) throws IOException, ClassNotFoundException {
        GamePlayer currentPlayer = theGameEngine.getCurrentPlayer();

        boolean attackSucc = false;
        String input = askTheUserWhichArmyAndHowMany();
        try {
            int numberOfUnits = gameInput.nextInt();
            int powerCurrentTerritory = 0;

            for (int i = 0; i < currentTer.getUnitInTerritory().size(); i++) {
                powerCurrentTerritory += currentTer.getUnitInTerritory().get(i).getCurrentPower();
            }

            if (currentPlayer.getTurings() >= numberOfUnits * theGameEngine.getInfoArmy().get(0).getPurchase()) {
                attackSucc = theGameEngine.manageEnoughMoneyToBuy(currentTer, numberOfUnits, powerCurrentTerritory, currentPlayer, cellTerritory);
            } else {
                checkValidOfChoiceRound();
            }
        } catch (Exception e) {
            System.out.println("Error:you need to enter number");
            checkValidOfChoiceRound();
        }

        return attackSucc;
    }

    private String askTheUserWhichArmyAndHowMany() {
        System.out.println("Which type of unit army do you want to buy?");
        String input = gameInput.next();

        while(!input.toUpperCase().equals("SOLDIER"))
        {
            System.out.println("Not exists - only soldier");
            System.out.println("Which type of unit army do you want to buy?");
            input = gameInput.next();
        }

        System.out.println("How many army units do you want to buy?");
        return input;
    }

    private void chosenBelongingTerritory(GameTerritory currentTer, int[] cellTerritory)
    {

        System.out.println("Choose \n1. Maintain your army in this territory\n2. Add more units to this territory\n");
        int userChoice=0;
        boolean succeed;
        try
        {
            userChoice = gameInput.nextInt();
            if(userChoice < 1 || userChoice > 2)
            {
                System.out.println("Error:you need to enter number 1/2");
                chosenBelongingTerritory(currentTer, cellTerritory);
            }

        }
        catch (Exception e)
        {
            System.out.println("Error:you need to enter number 1/2");
            chosenBelongingTerritory(currentTer, cellTerritory);
        }


        if(userChoice == 1)
        {
            succeed=theGameEngine.maintainArmy(currentTer);
            if(succeed==true)
            {
                System.out.println("Your army now in full power at this territory");
            }
            else
                System.out.println("Sorry, you dont have enough turings");

        }
        else
        {
            buyArmy(currentTer, cellTerritory);
        }
    }

    private int [] getValidRowAndCol()
    {
        int colNumber=0,rowNumber=0;
        String inputCol, inputRow;
        boolean parseable=false;
        System.out.println("To choose cellTerritory - enter number for column:");
        inputCol = gameInput.next();

        while(parseable == false)
        {
            try
            {
                colNumber=Integer.parseInt(inputCol);

                if (colNumber < 1 || colNumber > columns) {
                    System.out.println("Error: you need to enter number between 1 and " + columns);
                    inputCol = gameInput.next();
                }
                else
                    parseable=true;
            }
            catch(Exception e)
            {
                System.out.println("Error: you need to enter number between 1 and " + columns);
                inputCol = gameInput.next();
            }
        }

        parseable=false;
        System.out.println("Enter number for row:");

        inputRow = gameInput.next();
        while(parseable == false)
        {
            try
            {
                rowNumber=Integer.parseInt(inputRow);
                if (rowNumber < 1 || rowNumber > rows) {
                    System.out.println("Error: you need to enter number between 1 and " + rows);
                    inputRow = gameInput.next();
                }
                else
                    parseable=true;
            }
            catch(Exception e)
            {
                System.out.println("Error: you need to enter number between 1 and " + rows);
                inputRow = gameInput.next();
            }
        }

        int[] rowAndCol = new int[2];
        rowAndCol[0]= rowNumber-1;
        rowAndCol[1]= colNumber-1;
        int numberOfTerritories = theGameEngine.getCurrentPlayer().getNumberOfTerritories();
        if(numberOfTerritories != 0)
        {
            if(theGameEngine.checkProximity(rowAndCol) == false)
            {
                System.out.println("Sorry, you can't choose this territory. ");
                return null;
            }
        }

        return rowAndCol;

    }

    private void showStatus(GameEngine game)
    {
        printCurrentBoard(game);
        List<GamePlayer> listPlayers = game.getPlayers();
        System.out.println("Rounds:" + game.getCounterOfRounds()+ " / " + game.getTotalCycles());
        for(int i = 0; i < listPlayers.size(); i++)
        {
            GamePlayer currentPlayer = listPlayers.get(i);
            System.out.println("Player " + currentPlayer.getType() + " has " + currentPlayer.getNumberOfTerritories() + " territory cell");
            printInformationTerritory(currentPlayer.getType());
            System.out.println("current Amount of turings: " + currentPlayer.getTurings());

        }
    }

    private void printInformationTerritory(GameTerritory.TypeOfTerritory type)
    {
        for (int i = 0; i< theGameEngine.getBoard().getBoardGame().length; i++)
        {
            for(int j = 0; j< theGameEngine.getBoard().getBoardGame()[0].length; j++)
            {
                GameTerritory currentCell= theGameEngine.getBoard().getBoardGame()[i][j];

                if (currentCell.getType() == type)
                {
                    System.out.println("Cell number: " + currentCell.getId());
                    System.out.println("Number of units: " + currentCell.getUnitInTerritory().size());
                    System.out.println("Current Power:" + currentCell.getCurrentPower());

                }
            }
        }
    }

    private void checkXML()
    {
        boolean ok = false;
        String inputPath;
        System.out.println("Please enter the path of the xml file:");
        gameInput.nextLine();

        while(ok==false)
        {
            boolean loadAgain=false;
            inputPath = gameInput.nextLine();
            try {
                path = Paths.get(inputPath);
                if (Files.exists(path) == false) {
                    System.out.println("Error: your file not exists. Please enter other path");

                }
                else if (path.toString().toLowerCase().endsWith(".xml") == false) {
                    System.out.println("Error: your file isn't xml file. Please enter other path");
                }
                else {
                    generate.GameDescriptor gameXml = theGameEngine.fromXmlFileToObject(path);
                    BigInteger rowsN = gameXml.getGame().getBoard().getRows();
                    int valueR= rowsN.intValue();
                    if ((valueR < 2 )|| (valueR > 30))
                    {
                        System.out.println("Error: number of rows is invalid.it need to be number 2-30. Please enter other path");
                    }
                    else
                    {
                        BigInteger columnsN = gameXml.getGame().getBoard().getColumns();
                        int valueC= columnsN.intValue();
                        if ((valueC < 3 )|| (valueC > 30))
                        {
                            System.out.println("Error: number of columns is invalid. it need to be number 3-30. Please enter other path");
                        }
                        else
                        {
                            rows = valueR;
                            columns=valueC;
                            List<generate.Teritory> teritories =gameXml.getGame().getTerritories().getTeritory();
                            for(int i=0 ;i<teritories.size();i++) {
                                if (teritories.get(i).getId().intValue() > (valueC * valueR)) {
                                    System.out.println("Error: you have teritory id invalid. Please enter other path" );
                                    loadAgain=true;
                                    i=teritories.size()-1;
                                }
                                else
                                {
                                    for (int j = 0; j < teritories.size(); j++) {
                                        if (teritories.get(j).getId().equals( teritories.get(i).getId())  && (i != j)) {
                                            System.out.println("Error: you have 2 equals teritory's id. Please enter other path");
                                            j=teritories.size()-1;
                                            i=j;
                                            loadAgain=true;
                                        }
                                    }
                                }
                            }
                            if(loadAgain==false)
                            {
                                BigInteger defaultArmy=gameXml.getGame().getTerritories().getDefaultArmyThreshold();
                                BigInteger defaultProfit=gameXml.getGame().getTerritories().getDefaultProfit();

                                if(rows*columns > teritories.size() && defaultArmy==null &&defaultProfit==null)
                                {
                                    System.out.println("Error: you don't have full define of all the territories - missing defaults");
                                }
                                else
                                {
                                    theGameEngine.gameDescriptor=gameXml;
                                    System.out.println("The file loaded successfully");
                                    theGameEngine.initGameEngine();
                                    ok=true;
                                }
                            }
                        }
                    }
                }

            }
            catch (InvalidPathException e)
            {
                System.out.println("Error: invalid path!");
            }
        }
    }

    private void checkValidNumber() throws IOException, ClassNotFoundException {
        System.out.println("Error: need to enter number from 1 to 9 ");
        showMainMenu();
    }

    private void saveTheGame() throws IOException {
        String pathInput =null;
        boolean validPath=false;
        String fileName = null;
        System.out.println("To save your game, please enter full path:");
        gameInput.nextLine();
        while(validPath == false ) {
           try {

               pathInput = gameInput.nextLine();
               Path pathfromUser = Paths.get(pathInput);

               if (Files.isDirectory(pathfromUser) == false) {
                   System.out.println("Error: your directory not exists. please enter another path:");
               }
               else
               {
                   fileName=pathInput.concat("/game.bin");
                   validPath=true;
               }
           } catch (InvalidPathException e) {
               System.out.println("Error: invalid path!");
           }

       }
        theGameEngine.writeToFile(fileName);
    }

    private void uploadGameFromFile() throws IOException, ClassNotFoundException {
        String inputPath=null;
        Path uploadPath;
        boolean validPath=false;
        System.out.println("Please enter the path of the file:");
        gameInput.nextLine();
        while (validPath == false) {
            try {

                inputPath = gameInput.nextLine();
                uploadPath = Paths.get(inputPath);
                if (Files.exists(uploadPath) == false) {
                    System.out.println("Error: your file not exists. Please enter other path");
                }
                else
                {
                    validPath = true;
                }
            } catch (InvalidPathException e) {
                System.out.println("Error: invalid path! Please enter another path:");
            }
        }

        try{
            theGameEngine = GameEngine.readFromFile(inputPath);
            fileLoaded=true;
            path = Paths.get(inputPath);
            numberOfRoundUpload=theGameEngine.getCounterOfRounds();
            columns=theGameEngine.getBoard().getColumns();
            rows=theGameEngine.getBoard().getRows();
        }
        catch(Exception e){
            System.out.println("Sorry, the file is not in the right format");
        }
    }

}