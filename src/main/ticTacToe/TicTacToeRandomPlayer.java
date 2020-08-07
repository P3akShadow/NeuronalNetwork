package main.ticTacToe;

import java.util.Random;

public class TicTacToeRandomPlayer implements TicTacToePlayer {
    @Override
    /**
     * This method will move to an empty, random field.
     */
    public int play(int[] field, int player) {
        int range = 0;

        for(int i = 0; i < field.length; i++){
            if(field[i] == 0) range++;
        }
        if (range == 0) return -1;

        Random rnd = new Random();
        int pos = rnd.nextInt(range);

        for(int i = 0, j = 0; i < field.length; i++){
            if(field[i] == 0){
                if(j == pos){
                    return i;
                } else {
                    j++;
                }
            }
        }

        return 0;
    }
}
