package main.digitRecognition;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class DataImport {
    public static void main(String[] args) {
        setupCanvas();

        int[][] imported = importFile("C:\\Users\\David\\Repos\\NeuronalNetwork2020\\digitFiles\\data0");

        for(int i = 0; i < imported.length; i++) {
            drawPattern(imported[i]);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static int[][] importFile(String path){
        try {
            File file = new File(path);

            FileInputStream inputStream = new FileInputStream(file);
            byte[][] output = new byte[1000][28*28];
            int[][] trans = new int[output.length][output.length];

            for(int i = 0; i < output.length; i++) {
                inputStream.read(output[i]);
            }

            inputStream.close();


            for(int i = 0; i < output.length; i++){
                for(int j = 0; j < output[i].length; j++){
                    trans[i][j] = output[i][j] & 255;
                }
            }

            return trans;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void setupCanvas(){
        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(700, 700);
        StdDraw.setScale(0, 27);
    }

    public static void drawPattern(int[] pixels){
        for(int i = 0; i < pixels.length; i++){
            StdDraw.setPenColor(pixels[i], pixels[i], pixels[i]);
            double x = (i % 28);
            double y = 27 -(i / 28);
            StdDraw.filledSquare(x, y, .51);
        }
        StdDraw.show();

    }
}
