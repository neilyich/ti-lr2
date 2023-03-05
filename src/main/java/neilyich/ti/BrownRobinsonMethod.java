package neilyich.ti;

import java.util.List;

public interface BrownRobinsonMethod {
    int xStrategiesCount();
    int yStrategiesCount();
    int xStrategy(int step);
    int yStrategy(int step);
    List<Integer> xWin(int step);
    List<Integer> yWin(int step);

    List<Double> xMixedStrategy();
    List<Double> yMixedStrategy();

    double minMaxCost(int step);
    double maxMinCost(int step);

    default int xWin(int step, int xStrategy) {
        return xWin(step).get(xStrategy);
    }
    default int yWin(int step, int yStrategy) {
        return yWin(step).get(yStrategy);
    }
    default int maxCostEstimate(int step) {
        return xWin(step, xStrategy(step + 1));
    }
    default int minCostEstimate(int step) {
        return yWin(step, yStrategy(step + 1));
    }
    default double e(int step) {
        return minMaxCost(step) - maxMinCost(step);
    }
}
