package main.ticTacToe;

/**
 * This player will look for opportunities to win and for opportunities for the opponent to win. If there is one, it will
 * place its token to win or avoid a loss. If there is no such opportunity, it will choose the field randomly.
 */
public class TicTacToeAddToTwoPlayer implements TicTacToePlayer {

    TicTacToeRandomPlayer randomPlayer = new TicTacToeRandomPlayer();

    @Override
    public int play(int[] field, int player) {
        int takeWin = placeBetween(field, player);
        if (takeWin >= 0) {
            return takeWin;
        }

        int avoidLoss = placeBetween(field, -player);
        if (avoidLoss >= 0) {
            return avoidLoss;
        }

        return randomPlayer.play(field, player);
    }

    private int placeBetween(int[] field, int player) {
        for (int i = 0; i < 3; i++) {
            //horizontal
            if (field[i] == field[3 + i] && field[i] == player && field[6 + i] == 0) {
                return 6 + i;
            } else if (field[i] == field[6 + i] && field[i] == player && field[3 + i] == 0) {
                return 3 + i;
            } else if (field[3 + i] == field[6 + i] && field[3 + i] == player && field[i] == 0) {
                return i;
            }

            //vertical
            if (field[i * 3] == field[(i * 3) + 1] && field[i * 3] == player && field[(i * 3) + 2] == 0) {
                return (i * 3) + 2;
            } else if (field[i * 3] == field[(i * 3) + 2] && field[i * 3] == player && field[(i * 3) + 1] == 0) {
                return (i * 3) + 1;
            } else if (field[i * 3 + 1] == field[(i * 3) + 2] && field[i * 3 + 1] == player && field[(i * 3)] == 0) {
                return (i * 3);
            }
        }

        if (field[0] == field[4] && field[0] == player && field[8] == 0) {
            return 8;
        } else if (field[0] == field[8] && field[0] == player && field[4] == 0) {
            return 4;
        } else if (field[4] == field[8] && field[4] == player && field[0] == 0) {
            return 0;
        }

        if (field[6] == field[4] && field[6] == player && field[2] == 0) {
            return 2;
        } else if (field[6] == field[2] && field[6] == player && field[4] == 0) {
            return 4;
        } else if (field[4] == field[2] && field[4] == player && field[6] == 0) {
            return 6;
        }

        return -1;
    }
}
