package main.ticTacToe;

/**
 * This Interface provides a play method for a Tic Tac Toe Player. Tic Tac Toe Game will have two Tic Tac Toe Players.
 */
public interface TicTacToePlayer {

    /**
     * This method is intended to be called by TicTacToeGame if a new move is expected
     * @param field Array of 0, 1 and -1 that represents the playing field.
     * @param player Represents if 1 or -1 should move next
     * @return An integer that represents the location of the next move
     */
    int play(int[] field, int player);
}
