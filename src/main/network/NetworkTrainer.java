package main.network;

import java.util.Arrays;

public class NetworkTrainer {
    static final double D = 1e-9;
    static final int DEVIATION_POWER = 3;
    static final double DEVIATION_MIN = 0;

    private Network[] networks;
    double[][][] trainingData;      //[0]: inputs; [1]: outputs; [0][i] input i


    public NetworkTrainer(Network network, double[][][] trainingData) {
        this.trainingData = trainingData;

        networks = new Network[trainingData[0].length];

        for (int i = 0; i < networks.length; i++) {
            networks[i] = new Network(network);
            networks[i].newInput(trainingData[0][i]);
        }
    }

    public void setTrainingData(double [][][] trainingData){
        this.trainingData = trainingData;
        flushNetworks();
    }

    public Network improveAllNeurons(){
        for(int i = networks[0].getNumOfLayers() - 1; i >= 1; i--){
            System.out.println("Layer: " + i);
            improveLayer(i);
        }
        return networks[0];
    }

    public Network improveLayer(int layer){
        for(int i = 0; i < networks[0].getSizeOfLayer(layer); i++){
            improveNeuron(layer, i);
        }
        return networks[0];
    }

    public Network improveNeuron(int layer, int neuron) {
        double[] desiredValues = desiredValues(layer, neuron);

        double oldDeviation;

        int reps = 0;

        do {
            reps++;
            flushNetworks();

            oldDeviation = calculateDeviation();

            double previousLayerSize = networks[0].getSizeOfLayer(layer - 1);
            gradientDecentWeight(layer, neuron, previousLayerSize / 1000, desiredValues);
            gradientDecentBias(layer, neuron, previousLayerSize / 2000, desiredValues);
            flushNetworks();

        } while (oldDeviation - calculateDeviation() > oldDeviation / 1000000 && reps < 10);

        return networks[0];
    }

    public double[] desiredValues(int layer, int neuron) {
        double oldValues[] = getValues(layer, neuron);
        double newValues[] = Arrays.copyOf(oldValues, oldValues.length);

        for (int i = 0; i < networks.length; i++) {
            double error;

            do {
                error = calculateDeviation(i);

                if (newValues[i] >= 0.001) {
                    newValues[i] -= 0.001;
                }

                networks[i].setValue(layer, neuron, newValues[i]);
                networks[i].calcFrom(layer + 1);
            } while (newValues[i] >= 0.001 && error > calculateDeviation(i));

            do {
                error = calculateDeviation(i);

                if (newValues[i] <= 0.999) {
                    newValues[i] += 0.001;
                }

                networks[i].setValue(layer, neuron, newValues[i]);
                networks[i].calcFrom(layer + 1);
            } while (newValues[i] <= 0.999 && error > calculateDeviation(i));
        }

        return newValues;
    }

    public double[] getValues(int layer, int neuron) {
        double[] values = new double[networks.length];

        for (int i = 0; i < networks.length; i++) {
            values[i] = networks[i].getValue(layer, neuron);
        }

        return values;
    }

    public void setValues(int layer, int neuron, double value) {
        for (int i = 0; i < networks.length; i++) {
            networks[i].setValue(layer, neuron, value);
        }
    }

    public double calculateDeviation() {
        int lastLayerIndex = networks[0].getNumOfLayers() - 1;

        return calculateDeviation(lastLayerIndex, trainingData[1]);
    }

    public double calculateDeviation(int layer, double[][] expectedResults) {
        return  calculateDeviation(layer, expectedResults, DEVIATION_POWER);
    }

    public double calculateDeviation(int layer, double[][] expectedResults, int power) {
        double sum = 0;

        for (int i = 0; i < networks.length; i++) {
            sum += calculateDeviation(i, layer, expectedResults, power);
        }

        return sum;
    }

    public double calculateDeviation(int network) {
        int lastLayerIndex = networks[0].getNumOfLayers() - 1;

        return calculateDeviation(network, lastLayerIndex, trainingData[1], DEVIATION_POWER);
    }

    public double calculateDeviation(int network, int layer, double[][] expectedResults, int power) {
        double sum = 0;

        int layerLength = networks[0].getSizeOfLayer(layer);

        for (int j = 0; j < layerLength; j++) {
            double expectedValue = expectedResults[network][j];
            double actualValue = networks[network].getValue(layer, j);

            double addedValue = (expectedValue - actualValue) * (expectedValue - actualValue);
            addedValue += DEVIATION_MIN;

            for(int k = 0; k < power; k++){
                addedValue *= addedValue;
            }

            sum += addedValue;
        }

        return sum;
    }

    public double[] weightGradient(int layer, int neuron, double[] expectedValues) {
        int previousLayerLength = networks[0].getSizeOfLayer(layer);

        double grad[] = new double[previousLayerLength];

        for (int predecessor = 0; predecessor < previousLayerLength; predecessor++) {
            double currWeight = networks[0].getWeight(layer, neuron, predecessor);

            double sum = 0;

            for (int i = 0; i < networks.length; i++) {
                networks[i].setWeight(layer, neuron, predecessor, currWeight + D);
                double newValue = networks[i].getValue(layer, neuron);
                double newDerivation = (newValue - expectedValues[i]) * (newValue - expectedValues[i]);

                networks[i].setWeight(layer, neuron, predecessor, currWeight);
                double oldValue = networks[i].getValue(layer, neuron);
                double oldDerivation = (oldValue - expectedValues[i]) * (oldValue - expectedValues[i]);

                sum += newDerivation - oldDerivation;
            }

            grad[predecessor] = sum;
        }

        return grad;
    }

    public Network gradientDecentWeight(int layer, int neuron, double stepSize, double[] expectedValues) {

        double[] gradient = weightGradient(layer, neuron, expectedValues);

        double[] steps = new double[gradient.length];

        double abs = 0;
        for (int i = 0; i < gradient.length; i++) {
            abs += gradient[i] * gradient[i];
        }
        abs = Math.sqrt(abs);
        if(! (abs > 0)){
            abs = Double.MIN_NORMAL;
        }

        for (int i = 0; i < gradient.length; i++) {
            gradient[i] /= abs;
            steps[i] = gradient[i] * stepSize;
        }

        for (int i = 0; i < networks.length; i++) {
            for (int connection = 0; connection < steps.length; connection++) {
                double oldWeight = networks[i].getWeight(layer, neuron, connection);

                networks[i].setWeight(layer, neuron, connection, oldWeight - steps[connection]);

            }
        }

        return new Network(networks[1]);
    }

    public Network gradientDecentBias(int layer, int neuron, double stepSize, double[] expectedValues) {
        double oldError;
        double newError = calculateDeviation();

        int reps = 0;

        do {
            double diff = 0;

            for (int i = 0; i < networks.length; i++) {
                networks[i].calcTo(layer);
                diff += expectedValues[i];
                diff -= networks[i].getValue(layer, neuron);
            }


            for (int i = 0; i < networks.length; i++) {
                double oldBias = networks[i].getBias(layer, neuron);
                if (diff > 0) {
                    networks[i].setBias(layer, neuron, oldBias + stepSize);
                } else{
                    networks[i].setBias(layer, neuron, oldBias - stepSize);
                }
            }

            flushNetworks();
            oldError = newError;
            newError = calculateDeviation();
        } while (oldError - newError > oldError/1000 && reps < 100);

        return networks[1];
    }

    public double[] lastLayerWeightGradient(int neuron) {
        int lastLayerIndex = networks[0].getNumOfLayers() - 1;
        int previousLayerLength = networks[0].getSizeOfLayer(lastLayerIndex - 1);

        double grad[] = new double[previousLayerLength];

        for (int predecessor = 0; predecessor < previousLayerLength; predecessor++) {
            double currWeight = networks[0].getWeight(lastLayerIndex, neuron, predecessor);

            double sum = 0;

            for (int i = 0; i < networks.length; i++) {
                networks[i].setWeight(lastLayerIndex, neuron, predecessor, currWeight + D);
                double newValue = networks[i].getValue(lastLayerIndex, neuron);
                double newDerivation = (newValue - trainingData[0][i][neuron]) * (newValue - trainingData[0][i][neuron]);

                networks[i].setWeight(lastLayerIndex, neuron, predecessor, currWeight);
                double oldValue = networks[i].getValue(lastLayerIndex, neuron);
                double oldDerivation = (oldValue - trainingData[0][i][neuron]) * (oldValue - trainingData[0][i][neuron]);

                sum += newDerivation - oldDerivation;
            }

            grad[predecessor] = sum;
        }

        return grad;
    }

    public Network gradientDecentLastLayer(int neuron, double stepSize) {
        int lastLayerIndex = networks[0].getNumOfLayers() - 1;
        int previousLayerLength = networks[0].getSizeOfLayer(lastLayerIndex - 1);

        double[] gradient = lastLayerWeightGradient(neuron);
        double[] steps = new double[gradient.length];

        double abs = 0;
        for (int i = 0; i < gradient.length; i++) {
            abs += gradient[i] * gradient[i];
        }
        abs = Math.sqrt(abs);

        for (int i = 0; i < gradient.length; i++) {
            gradient[i] /= abs;
            steps[i] = gradient[i] * stepSize;
        }

        for (int i = 0; i < networks.length; i++) {
            for (int connection = 0; connection < steps.length; connection++) {
                double oldWeight = networks[i].getWeight(lastLayerIndex, neuron, connection);

                networks[i].setWeight(lastLayerIndex, neuron, connection, oldWeight - steps[connection]);
            }
        }

        return new Network(networks[1]);
    }

    private void flushNetworks() {
        for (Network network : networks) {
            network.calcFrom(1);
        }
    }

    public String printDerivation() {
        StringBuilder sb = new StringBuilder();


        double sum = 0;

        int lastLayerIndex = networks[0].getNumOfLayers() - 1;
        int layerLength = networks[0].getSizeOfLayer(lastLayerIndex);

        for (int i = 0; i < networks.length; i++) {
            for (int j = 0; j < layerLength; j++) {
                String expectedValue = "EXPECTED:" + "Network " + i + " Neuron" + j + ": " + trainingData[1][i][j];
                String actualValue = "Actual:" + "Network " + i + " Neuron" + j + ": " + networks[i].getValue(lastLayerIndex, j);
                sb.append(expectedValue + "\n");
                sb.append(actualValue + "\n");
            }
        }

        System.out.println(sb.toString());
        return sb.toString();
    }

    /**
     * This method just works for training data with excactly one 1 as desired output, the rest shall be 0.
     * @return
     */
    public int calculateMisjudgements(){
        flushNetworks();

        int numOfMisjudgements = 0;
        int lastLayer = networks[0].getNumOfLayers() - 1;

        for(int i = 0; i < networks.length; i++){
            double maxValue = networks[i].getValue(lastLayer, 0);
            int maxValueIndex = 0;
            int correctJudgement = -1;

            for(int j = 1; j < networks[i].getSizeOfLayer(lastLayer); j++){
                if(maxValue < networks[i].getValue(lastLayer, j)){
                    maxValueIndex = j;
                }

                if(trainingData[1][i][j] > 0.5){
                    correctJudgement = j;
                }
            }

            if(correctJudgement != maxValueIndex){
                numOfMisjudgements++;
            }
        }

        return numOfMisjudgements;
    }
}
