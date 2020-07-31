package main;

import main.TicTacToe.TicTacToeGame;
import main.TicTacToe.TicTacToePlayer;
import main.TicTacToe.TicTacToeRandomPlayer;
import main.network.Network;
import main.network.NetworkTrainer;

import java.sql.SQLOutput;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        /*
        Network nw = new Network(new int[]{2, 2, 1});

        nw.randomiseWeightsAndBiases(1, 1);

        nw.newInput(new double[]{0.9, 0.1});

        System.out.println(nw);

        */
        TicTacToePlayer playerX = new TicTacToeRandomPlayer();
        TicTacToePlayer playerO = new TicTacToeRandomPlayer();
        TicTacToeGame[] games = new TicTacToeGame[1000];
        for(int i = 0; i < games.length; i++){
            TicTacToeGame game = new TicTacToeGame(playerX, playerO);
            games[i] = game;
            game.play();
        }

        Network toBeTrained = new Network(new int[]{19, 10, 5, 3, 2, 1});
        toBeTrained.randomiseWeightsAndBiases(5, 9);

        System.out.println("Initial Network:");
        System.out.println(TicTacToeGame.calculateDerivation(toBeTrained, games, 69));
        System.out.println(TicTacToeGame.calculateDerivation(toBeTrained, games, 420));
        System.out.println(TicTacToeGame.calculateDerivation(toBeTrained, games, 69420));

        double[][][] trainingData = TicTacToeGame.networkTestGroup(games, 69);

        NetworkTrainer trainer = new NetworkTrainer(toBeTrained, trainingData);

        double history = 100000;
        double current = TicTacToeGame.calculateDerivation(toBeTrained, games, 69);
        int i = 0;

        System.out.println("Training logs:");
        while(history - current > 0.01 && i < 10000) {
            toBeTrained = trainer.improveNeuron(5,0);

            toBeTrained = trainer.improveNeuron(4,0);
            toBeTrained = trainer.improveNeuron(4,1);

            toBeTrained = trainer.improveNeuron(3,0);
            toBeTrained = trainer.improveNeuron(3,1);
            toBeTrained = trainer.improveNeuron(3,2);

            toBeTrained = trainer.improveNeuron(2,0);
            toBeTrained = trainer.improveNeuron(2,1);
            toBeTrained = trainer.improveNeuron(2,2);
            toBeTrained = trainer.improveNeuron(2,3);
            toBeTrained = trainer.improveNeuron(2,4);

            toBeTrained = trainer.improveNeuron(1,0);
            toBeTrained = trainer.improveNeuron(1,1);
            toBeTrained = trainer.improveNeuron(1,2);
            toBeTrained = trainer.improveNeuron(1,3);
            toBeTrained = trainer.improveNeuron(1,4);
            toBeTrained = trainer.improveNeuron(1,5);
            toBeTrained = trainer.improveNeuron(1,6);
            toBeTrained = trainer.improveNeuron(1,7);
            toBeTrained = trainer.improveNeuron(1,8);
            toBeTrained = trainer.improveNeuron(1,9);


            history = current;
            current = TicTacToeGame.calculateDerivation(toBeTrained, games, 69);
            i++;
            if(i % 1 == 0) {
                System.out.println(current);
                System.out.println(history - current);
            }

        }

        System.out.println("final Results:");

        System.out.println(TicTacToeGame.calculateDerivation(toBeTrained, games, 69));
        System.out.println(TicTacToeGame.calculateDerivation(toBeTrained, games, 420));
        System.out.println(TicTacToeGame.calculateDerivation(toBeTrained, games, 69420));

        //System.out.println(trainer.printDerivation());


    }
}
