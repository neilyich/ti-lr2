package neilyich.ti;

import org.ejml.simple.SimpleMatrix;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SimpleBrownRobinsonMethod implements BrownRobinsonMethod {
    private final SimpleMatrix c;
    private final int xStrategiesCount;
    private final int yStrategiesCount;
    private final int[] xWin;
    private final int[] yWin;

    private final int[] xStrategiesChoices;
    private final int[] yStrategiesChoices;

    private int k = 0;

    private final List<Integer> xStrategiesSequence = new LinkedList<>();
    private final List<List<Integer>> xWinSequence = new LinkedList<>();
    private final List<Integer> yStrategiesSequence = new LinkedList<>();
    private final List<List<Integer>> yWinSequence = new LinkedList<>();

    private final List<Double> minMaxCosts = new LinkedList<>();
    private final List<Double> maxMinCosts = new LinkedList<>();


    public SimpleBrownRobinsonMethod(SimpleMatrix c) {
        this.c = c;
        xStrategiesCount = c.getNumRows();
        yStrategiesCount = c.getNumCols();
        xWin = new int[xStrategiesCount];
        yWin = new int[yStrategiesCount];
        xStrategiesChoices = new int[xStrategiesCount];
        yStrategiesChoices = new int[yStrategiesCount];
    }

    public void iterate(int times) {
        submitStrategies(chooseXStrategy(), chooseYStrategy());
        k++;
        for (int i = 0; i < times; i++) {
            int xStrategy = chooseXStrategy();
            int yStrategy = chooseYStrategy();
            updateEstimatedCosts(xStrategy, yStrategy);
            submitStrategies(xStrategy, yStrategy);
            k++;
        }
    }

    private void submitStrategies(int xStrategy, int yStrategy) {
        xStrategiesChoices[xStrategy] += 1;
        for (int yi = 0; yi < yStrategiesCount; yi++) {
            yWin[yi] += c.get(xStrategy, yi);
        }
        yStrategiesChoices[yStrategy] += 1;
        for (int xi = 0; xi < xStrategiesCount; xi++) {
            xWin[xi] += c.get(xi, yStrategy);
        }
        xStrategiesSequence.add(xStrategy);
        xWinSequence.add(Arrays.stream(xWin).boxed().toList());
        yStrategiesSequence.add(yStrategy);
        yWinSequence.add(Arrays.stream(yWin).boxed().toList());
    }

    private void updateEstimatedCosts(int xStrategy, int yStrategy) {
        double minMaxCost = (double) xWin[xStrategy] / k;
        if (minMaxCosts.isEmpty()) {
            minMaxCosts.add(minMaxCost);
        } else {
            minMaxCosts.add(Math.min(minMaxCost, minMaxCosts.get(minMaxCosts.size() - 1)));
        }
        double maxMinCost = (double) yWin[yStrategy] / k;
        if (maxMinCosts.isEmpty()) {
            maxMinCosts.add(maxMinCost);
        } else {
            maxMinCosts.add(Math.max(maxMinCost, maxMinCosts.get(maxMinCosts.size() - 1)));
        }
    }

    private int chooseXStrategy() {
        int xStrategy = 0;
        int maxWin = xWin[xStrategy];
        for (int xi = 0; xi < xStrategiesCount; xi++) {
            if (maxWin < xWin[xi]) {
                maxWin = xWin[xi];
                xStrategy = xi;
            }
        }
        return xStrategy;
    }

    private int chooseYStrategy() {
        int yStrategy = 0;
        int minLoss = yWin[yStrategy];
        for (int yi = 0; yi < yStrategiesCount; yi++) {
            if (minLoss > yWin[yi]) {
                minLoss = yWin[yi];
                yStrategy = yi;
            }
        }
        return yStrategy;
    }

    @Override
    public int xStrategiesCount() {
        return xStrategiesCount;
    }

    @Override
    public int yStrategiesCount() {
        return yStrategiesCount;
    }

    @Override
    public int xStrategy(int step) {
        return xStrategiesSequence.get(step);
    }

    @Override
    public int yStrategy(int step) {
        return yStrategiesSequence.get(step);
    }

    @Override
    public List<Integer> xWin(int step) {
        return xWinSequence.get(step);
    }

    @Override
    public List<Integer> yWin(int step) {
        return yWinSequence.get(step);
    }

    @Override
    public List<Double> xMixedStrategy() {
        return Arrays.stream(xStrategiesChoices).mapToDouble(it -> (double) it / (double) k).boxed().toList();
    }

    @Override
    public List<Double> yMixedStrategy() {
        return Arrays.stream(yStrategiesChoices).mapToDouble(it -> (double) it / (double) k).boxed().toList();
    }

    @Override
    public double minMaxCost(int step) {
        return minMaxCosts.get(step);
    }

    @Override
    public double maxMinCost(int step) {
        return maxMinCosts.get(step);
    }
}
