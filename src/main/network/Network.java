package main.network;

/**
 * This class represents a neuronal Network. It consists of Layers, where the information is stored.
 *
 * There are methods to compute new input.
 *
 * Addidtionally, there are methods to make back propagation more efficient.
 */
public class Network {
    private Layer[] layers;

    /**
     * This will initialize a new Network with null values, weights and biases.
     *
     * @param layerSizes Sizes of the layers, implies the total number of layers.
     */
    public Network(int[] layerSizes){
        layers = new Layer[layerSizes.length];

        layers[0] = new Layer(layerSizes[0]);

        for(int i = 1; i < layerSizes.length; i++){
            layers[i] = new Layer(layerSizes[i], layers[i - 1]);
        }
    }

    /**
     * This will create a new Network with the same Values for the weights and Biases. Intended to calculate gradients
     * for big samples easily
     */
    public Network(Network network){
        layers = new Layer[network.getNumOfLayers()];

        layers[0] = new Layer(network.layers[0].getSize());

        for(int i = 1; i < layers.length; i++){
            layers[i] = new Layer(network.layers[i].getSize(), layers[i - 1]);
        }

        applyNetwork(network);
    }

    public void randomiseWeightsAndBiases(double maxWeight, double maxBias){
        for(int i = 1; i < layers.length; i++){
            layers[i].setRandomWeights(maxWeight);
            layers[i].setRandomBiases(maxBias);
        }
    }

    public void randomiseAdjusted(double maxWeightSum, double maxBias){
        for(int i = 1; i < layers.length; i++){
            int previousLayerSize = getSizeOfLayer(i - 1);
            double maxWeight = previousLayerSize / maxWeightSum;

            layers[i].setRandomWeights(maxWeight);
            layers[i].setRandomBiases(maxBias);
        }
    }

    public void applyNetwork(Network network){
        for(int i = 1; i < layers.length; i++){
            layers[i].setWeightsAndBiases(network.layers[i].getWeights(), network.layers[i].getBiases());
        }
    }

    /**
     * This calculate the values for the network with a new input.
     * @param values The new input values.
     */
    public void newInput(double[] values){
        layers[0].setValues(values);

        for(int i = 1; i < layers.length; i++){
            layers[i].calcValues();
        }
    }

    /**
     * This will calculate the values starting at layer layer. The first layer eligible is 1.
     * @param layer
     */
    protected void calcFrom(int layer){
        for(int i = layer; i < layers.length; i++){
            layers[i].calcValues();
        }
    }

    /**
     * This will calculate the values until a layer layer is reached
     * @param layer
     */
    protected void calcTo(int layer){
        for(int i = 1; i < layer; i++){
            layers[i].calcValues();
        }
    }

    protected double getWeight(int layer, int neuron, int connection){
        return layers[layer].getWeight(neuron, connection);
    }

    protected void setWeight(int layer, int neuron, int connection, double newValue){
        //recalculates the value of the neuron, that connection leads to
        layers[layer].setWeight(neuron, connection, newValue);
    }

    protected double getBias(int layer, int neuron){
        return layers[layer].getBias(neuron);
    }

    protected void setBias(int layer, int neuron, double bias){
        //recalculates the value of the neuron, that bias is part of
        layers[layer].setBias(neuron, bias);
    }

    public double getValue(int layer, int neuron){
        return layers[layer].getValue(neuron);
    }

    protected double[] getValues(int layer){
        return layers[layer].getValues();
    }

    protected void setValue(int layer, int neuron, double newValue){
        layers[layer].setValue(neuron, newValue);

        for(int i = layer + 1; i < layers.length; i++){
            layers[i].calcValues();
        }
    }

    protected void setValues(int layer, double[] newValues){
        layers[layer].setValues(newValues);

        for(int i = layer + 1; i < layers.length; i++){
            layers[i].calcValues();
        }
    }

    public int getNumOfLayers(){
        return layers.length;
    }

    public int getSizeOfLayer(int layer){
        return layers[layer].getSize();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("This is a network with " + layers.length + " layers:");

        for(int i = 0; i < layers.length; i++){
            sb.append("\nLayer " + i + ":");

            String body = layers[i].toString();
            body = body.replace("\n", "\n  ");
            sb.append(body);
        }

        return sb.toString();
    }

}
