package neilyich.ti;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class BrownRobinsonMethodPrinter {
    private BrownRobinsonMethod brm;
    private int rows;
    private int cellWidth;
    public static String DELIMITER = " |";

    public void print(BrownRobinsonMethod brm, int rows) {
        this.brm = brm;
        this.rows = rows;
        calcCellWidth();
        printHeader();
        for (int row = 0; row < rows; row++) {
            printRow(row);
        }
        nextLine();
        System.out.println("X = " + brm.xMixedStrategy());
        System.out.println("Y = " + brm.yMixedStrategy());
        System.out.println(brm.maxMinCost(rows - 1) + " <= V <= " + brm.minMaxCost(rows - 1));
    }

    private void calcCellWidth() {
        int maxNumber = Stream.iterate(0, i -> i < rows, i -> i + 1)
                .flatMapToInt(row ->
                        Stream.<Function<Integer, Integer>>of(
                                brm::xStrategy,
                                brm::yStrategy,
                                brm::maxCostEstimate,
                                r -> brm.xWin(r).stream().mapToInt(it -> it).max().orElse(0),
                                r -> brm.yWin(r).stream().mapToInt(it -> it).max().orElse(0)
                        ).mapToInt(f -> f.apply(row))
                ).max().orElse(0);
        cellWidth = (int) Math.log10(maxNumber) + 1;
    }

    private void printHeader() {
        printRow(List.of(
                () -> print("i"),
                () -> print("xi"),
                () -> print("yi"),
                () -> printGroup("X", brm.xStrategiesCount()),
                () -> printGroup("Y", brm.yStrategiesCount()),
                () -> print("V"),
                () -> print("v"),
                () -> print("e", cellWidth + 3)
        ));
    }

    private void printRow(Iterable<Runnable> contentCreators) {
        for (var cellCreator : contentCreators) {
            cellCreator.run();
            sep();
        }
        nextLine();
    }

    private void printRow(int row) {
        printRow(List.of(
                () -> print(row + 1),
                () -> print(brm.xStrategy(row) + 1),
                () -> print(brm.yStrategy(row) + 1),
                () -> printGroup(brm.xWin(row)),
                () -> printGroup(brm.yWin(row)),
                () -> print(brm.maxCostEstimate(row)),
                () -> print(brm.minCostEstimate(row)),
                () -> print(brm.e(row))
        ));
    }

    private void print(int n) {
        print(String.valueOf(n));
    }

    private void print(double n) {
        print(BigDecimal.valueOf(n).setScale(2, RoundingMode.HALF_EVEN).toString(), cellWidth + 3);
    }

    private void print(String cell) {
        print(cell, cellWidth);
    }

    private void printGroup(String content, int colsCount) {
        print(content, colsCount * cellWidth);
    }

    private void printGroup(List<Integer> content) {
        print(String.join(" ", content.stream().map(Object::toString).toList()), content.size() * (cellWidth + 1));
    }

    private void print(String content, int width) {
        System.out.print(" ".repeat(width - content.length()));
        System.out.print(content);
    }

    private void sep() {
        System.out.print(DELIMITER);
    }

    private void nextLine() {
        System.out.println();
    }
}
