package main.network;

import java.util.Arrays;
import java.util.Random;

/**
 * This class represents a layer of a neuronal network. Its constructor demands a previous layer to create the
 * weights array.
 *
 * It has methods to feed information forward.
 */
public class Layer {
    private double[] values;
    private double[] biases;
    private double[][] weights;
    private Layer previousLayer;

    public static final double base = Math.E;

    //intended for the first Layer of the NN
    public Layer(int size){
        values  = new double[size];
        biases = new double[size];
        weights = null;
        previousLayer = null;
    }

    public Layer(int size, Layer previousLayer){
        values  = new double[size];
        biases = new double[size];
        weights = new double[size][previousLayer.getSize()];
        this.previousLayer = previousLayer;
    }

    public int getSize(){
        return values.length;
    }

    protected double[] getValues(){
        return values;
    }

    public double getValue(int neuron){
        return values[neuron];
    }

    /**
     * This method propagates the values from the previous layer to this layer. Therefore it multiplies the values of
     * the values in the previous layer with the corresponding weights and adds a bias. Then, a sigmoid function will
     * be applied to fit the results in the interval (0; 1).
     */
    public void calcValues(){
        if(previousLayer == null){
            System.err.println("ERROR: previousLayer is null!");
            return;
        }

        for(int i = 0; i < values.length; i++){
            double sum = biases[i];

            for(int j = 0; j < weights[i].length; j++){
                sum += previousLayer.values[j] * weights[i][j];
            }

            values[i] = sigmoid(sum);
        }
    }

    public static double sigmoid(double x){
        return 1 / (1 + Math.pow(base, -x));
    }

    public static double reverseSigmoid(double y){
        return - Math.log((1/y)-1);
    }

    public void setValues(double[] values) {
        if(values.length != getSize()){
            System.err.println("ERROR: tried to set a Layer with varying length.");
            return;
        }

        for(double value : values){
            if(value < 0 || value > 1){
                System.err.println("ERROR: all values of a layer must be between 0 and 1");
                return;
            }
        }

        this.values = values;
    }

    public void setValue(int neuron, double value) {
        values[neuron] = value;
    }

    public void setRandomBiases(double maxAbsBias){
        Random rnd = new Random();

        for(int i = 0; i < biases.length; i++){
            biases[i] = (2 * maxAbsBias * rnd.nextDouble()) - maxAbsBias;
        }
    }

    public void setRandomWeights(double maxAbsWeight){
        Random rnd = new Random();

        for(int i = 0; i < weights.length; i++){
            for(int j = 0; j < weights[i].length; j++) {
                weights[i][j] = (2 * maxAbsWeight * rnd.nextDouble()) - maxAbsWeight;
            }
        }
    }

    protected double[][] getWeights(){
        return weights;
    }

    protected double[] getBiases(){
        return biases;
    }

    protected double getWeight(int neuron, int connection){
        return weights[neuron][connection];
    }

    //this will recalculate the value that is changed through the weight change, too.
    protected void setWeight(int neuron, int connection, double newWeight){
        weights[neuron][connection] = newWeight;

        double sum = biases[neuron];

        for(int i = 0; i < weights[neuron].length; i++){
            sum += previousLayer.values[i] * weights[neuron][i];
        }

        values[neuron] = sigmoid(sum);

    }

    protected double getBias(int neuron){
        return biases[neuron];
    }

    protected void setBias(int neuron, double newBias){
        biases[neuron] = newBias;

        double sum = biases[neuron];

        for(int i = 0; i < weights[neuron].length; i++){
            sum += previousLayer.values[i] * weights[neuron][i];
        }

        values[neuron] = sigmoid(sum);
    }

    /**
     * This will set the weights and biases to the same value as the values on the params are. Params remain unchanged.
     * @param weights
     * @param biases
     */
    protected void setWeightsAndBiases(double[][] weights, double[] biases){
        if(biases.length != this.biases.length){
            System.err.println("ERROR: Length of old and new biases do not match");
        }

        if(weights.length != this.weights.length){
            System.err.println("ERROR: Length of old and new weights do not match");
        }

        for(int i = 0; i < weights.length; i++){
            if(weights[i].length != previousLayer.getSize()){
                System.err.println("ERROR: Length of old and new weights[" + i +"] do not match");
            }
        }

        this.weights = new double[weights.length][];
        for(int i = 0; i < this.weights.length; i++){
            this.weights[i] = Arrays.copyOf(weights[i], weights[i].length);
        }
        this.biases = Arrays.copyOf(biases, biases.length);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < values.length; i++){
            sb.append("\nNeuron " + i + ":");
            sb.append("\n  Value: " + values[i] + "");
            if(previousLayer != null){
                sb.append("\n  Bias: " + biases[i] + "");
                sb.append("\n  Weights:");

                for(int j = 0; j < weights[i].length; j++){
                    sb.append("\n    " + j + ": " + weights[i][j]);
                }
            }
        }
        return sb.toString();
    }
}
