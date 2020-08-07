package main;

import main.digitRecognition.DataImport;
import main.network.Layer;
import main.ticTacToe.TicTacToeAddToTwoPlayer;
import main.ticTacToe.TicTacToeGame;
import main.ticTacToe.TicTacToePlayer;
import main.network.Network;
import main.network.NetworkTrainer;

public class Main {

    public static void main(String[] args) {
        //ticTacToeDemo();
        digitRecognitionDemo();
    }

    public static void ticTacToeDemo(){
        /*
        Network nw = new Network(new int[]{2, 2, 1});

        nw.randomiseWeightsAndBiases(1, 1);

        nw.newInput(new double[]{0.9, 0.1});

        System.out.println(nw);

        */
        TicTacToePlayer playerX = new TicTacToeAddToTwoPlayer();
        TicTacToePlayer playerO = new TicTacToeAddToTwoPlayer();
        TicTacToeGame[] games = new TicTacToeGame[1000];
        for(int i = 0; i < games.length; i++){
            TicTacToeGame game = new TicTacToeGame(playerX, playerO);
            games[i] = game;
            game.play();
        }

        Network toBeTrained = new Network(new int[]{19, 10, 5, 3, 2, 1});
        toBeTrained.randomiseWeightsAndBiases(4, 8);

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

        trainer.printDerivation();
    }

    public static void digitRecognitionDemo(){
        String path = "C:\\Users\\David\\Repos\\NeuronalNetwork2020\\digitFiles\\data";
        int[][][] examples = new int[10][][];

        for(int i = 0; i < examples.length; i++){
            examples[i] = DataImport.importFile(path + i);
        }

        double[][][] trainingData = new double[2][100][];

        for(int i = 0; i < trainingData[1].length; i++){
            trainingData[1][i] = new double[10];
            trainingData[1][i][i / 10] = 1;
        }

        for(int i = 0; i < trainingData[0].length; i++){
            trainingData[0][i] = new double[24*24];

            for(int j = 0; j < trainingData[0][i].length; j++) {
                trainingData[0][i][j] = (double) examples[i / 10][i % 10][j] / 256;
            }
        }

        Network recoNet = new Network(new int[]{24*24, 12*12, 50, 10});
        recoNet.randomiseAdjusted(5, 9);

        NetworkTrainer recoTrainer = new NetworkTrainer(recoNet, trainingData);

        System.out.println("Initial Network:");
        System.out.println(recoTrainer.calculateDeviation());

        double history = 100000;
        double current = recoTrainer.calculateDeviation();
        int i = 0;

        System.out.println("Training logs:");
        while(history - current > 0.01 && i < 10000) {
            recoTrainer.improveAllNeurons();

            history = current;
            current = recoTrainer.calculateDeviation();
            i++;
            if(i % 1 == 0) {
                System.out.println(current);
                System.out.println(history - current);
            }

        }

        System.out.println("final Results:");
        System.out.println(recoTrainer.calculateDeviation());
        recoTrainer.printDerivation();
    }
}
