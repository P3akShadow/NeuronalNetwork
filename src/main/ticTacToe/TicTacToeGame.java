package main.ticTacToe;

import main.network.Network;

import java.util.Arrays;
import java.util.Random;

public class TicTacToeGame {
    private int[] gameState;
    private int[] moveHistory;
    private int currentMove;
    private TicTacToePlayer playerX;
    private TicTacToePlayer playerO;

    public TicTacToeGame(TicTacToePlayer playerX, TicTacToePlayer playerO){
        gameState = new int[9];
        moveHistory = new int[9];
        Arrays.fill(moveHistory, -1);
        currentMove = 0;
        this.playerX = playerX;
        this.playerO = playerO;
    }

    /**
     * Checks if there can be any more moves.
     * @return true if there are empty fields, else false
     */
    public boolean isPlayingFieldFull(){
        for(int field : gameState){
            if(field == 0) return false;
        }

        return true;
    }

    /**
     * Checks if there is a winner
     * @return 1 if 1 won; -1 if -1 won; 0 if no player won.
     */
    public int calcWinner(){
        for(int i = 0; i < 3; i++){
            //horizontal
            if(gameState[i] == gameState[3 + i] && gameState[3 + i] == gameState[6 + i] &&
                gameState[i] != 0){
                return gameState[i];
            }

            //vertical
            if(gameState[i * 3] == gameState[(i * 3) + 1] && gameState[(i * 3) + 1] == gameState[(i * 3) + 2] &&
                gameState[i * 3] != 0){
                return gameState[i * 3];
            }
        }

        if(gameState[0] == gameState[4] && gameState[4] == gameState[8] &&
                gameState[0] != 0){
            return gameState[0];
        }

        if(gameState[6] == gameState[4] && gameState[4] == gameState[2] &&
                gameState[6] != 0){
            return gameState[6];
        }

        return 0;
    }

    public int play(){
        while(calcWinner() == 0 && !isPlayingFieldFull()){
            int move;

            do{
                move = playerX.play(gameState, 1);
                if(gameState[move] != 0){
                    System.out.println("ERROR: Field already occupied");
                }
            } while(gameState[move] != 0);

            moveHistory[currentMove] = move;
            gameState[move] = 1;
            currentMove++;

            if(calcWinner() == 1){
                return 1;
            } else if(isPlayingFieldFull()){
                return 0;
            }

            do{
                move = playerO.play(gameState, -1);
                if(gameState[move] != 0){
                    System.out.println("ERROR: Field already occupied");
                }
            } while(gameState[move] != 0);

            moveHistory[currentMove] = move;
            gameState[move] = -1;
            currentMove++;

            if(calcWinner() == -1){
                return -1;
            }
        }
        return 0;
    }

    public String stateToString(int[] state){
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < state.length; i++){
            if(i % 3 == 0){
                sb.append("\n------------\n");
            }

            if(state[i] == 1){
                sb.append(" X |");
            } else if(state[i] == -1) {
                sb.append(" O |");
            } else{
                sb.append("   |");
            }
        }

        return sb.toString();
    }

    public String historyToString(){
        StringBuilder sb = new StringBuilder();
        int[] localGameState = new int[9];

        sb.append("\nEnd: " + stateToString(gameState));

        for(int i = 0; i < moveHistory.length && moveHistory[i] >= 0; i++){
            localGameState[moveHistory[i]] = i % 2 == 0 ? 1 : -1;
            sb.append("\nMove " + i + ":");
            sb.append(stateToString(localGameState));
        }

        return sb.toString();
    }

    public int[] historyToGameState(int move){
        int[] localGameState = new int[9];
        for(int i = 0; i < move && moveHistory[i] >= 0; i++){
            localGameState[moveHistory[i]] = i % 2 == 0 ? 1 : -1;
        }
        return localGameState;
    }

    public double[] historyStateToNetworkInput(int move){
        double[] networkInput = new double[19];           //0-8: Fields with 1; 9-17: Fields with -1; (fields that fulfill are 1 else 0) 18: 1 if 1 is to move else 0

        int[] localState = historyToGameState(move);
        int xes = 0;
        int os = 0;

        for(int i = 0; i < 9; i++){
            if(localState[i] == 1){
                networkInput[i] = 1;
                xes++;
            }
        }

        for(int i = 0; i < 9; i++){
            if(localState[i] == -1) {
                networkInput[9 + i] = 1;
                os++;
            }
        }

        networkInput[18] = xes > os ? 0 : 1;

        return networkInput;
    }

    public static double calculateDerivation (Network network, TicTacToeGame[] gamesToTest, long seed){
        double sumOfError = 0;
        Random rnd = new Random(seed);

        for(int i = 0; i < gamesToTest.length; i++){
            //goal will be 1 if x won, 0 if o won and 0.5 if the game ended in a draw
            double goal = 1;
            if(gamesToTest[i].calcWinner() == 0){
                goal = 0.5;
            } else if(gamesToTest[i].calcWinner() == -1){
                goal = 0;
            }

            //bound 8 plus 1 because an empty playing Field has little Value
            network.newInput(gamesToTest[i].historyStateToNetworkInput(rnd.nextInt(8) + 1));
            int layer = network.getNumOfLayers() - 1;
            int neuron = network.getSizeOfLayer(layer) - 1;
            double output = network.getValue(layer, neuron);

            sumOfError += (goal - output) * (goal - output);
        }

        return sumOfError;
    }

    public double[][] networkInOut(int move){
        double [] input = historyStateToNetworkInput(move);
        double [] output = {1};
        if(calcWinner() == 0){
            output[0] = 0.5;
        }else if(calcWinner() == -1){
            output[0] = 0;
        }

        return new double[][]{input, output};
    }

    public static double[][][] networkTestGroup(TicTacToeGame[] gamesToTest, long seed){
        double[][][] trainingData = new double[2][gamesToTest.length][];
        Random rnd = new Random(seed);

        for(int i = 0; i < gamesToTest.length; i++){
            double[][] inOut = gamesToTest[i].networkInOut(rnd.nextInt(8) + 1);

            trainingData[0][i] = inOut[0];
            trainingData[1][i] = inOut[1];
        }

        return trainingData;
    }
}
