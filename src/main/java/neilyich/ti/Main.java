package neilyich.ti;

import org.ejml.equation.Equation;
import org.ejml.simple.SimpleMatrix;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.DoubleStream;

public class Main {
    public static void main(String[] args) {
        var c = new SimpleMatrix(new double[][]{
                {1.0, 11.0, 11.0},
                {7.0, 5.0, 8.0},
                {16.0, 6.0, 2.0}
        });


        var exampleC = new SimpleMatrix(new double[][]{
                {2.0, 1.0, 3.0},
                {3.0, 0.0, 1.0},
                {1.0, 2.0, 1.0}
        });
        //c = exampleC;

        solveUsingInverseMatrix(c);

        System.out.println("\n".repeat(5));

        var brm = new SimpleBrownRobinsonMethod(c);
        int steps = 100;
        brm.iterate(steps);
        var printer = new BrownRobinsonMethodPrinter();
        printer.print(brm, steps);
    }

    private static void solveUsingInverseMatrix(SimpleMatrix c) {
        if (c.getNumRows() != c.getNumCols()) {
            throw new IllegalArgumentException("Matrix must be n x n");
        }
        var c1 = c.invert();
        System.out.println("C^-1:");
        printMatrix(c1);
        var ut = new SimpleMatrix(DoubleStream.generate(() -> 1.0).limit(c.getNumRows()).toArray());
        var u = ut.transpose();
        var eq = new Equation();
        eq.alias(c1, "c1", u, "u", ut, "ut");
        eq.process("d = u * c1 * ut");
        eq.process("x = (u * c1) / d");
        var x = eq.lookupSimple("x");
        System.out.println("\nX:");
        printMatrix(x);
        eq.process("y = (c1 * ut)' / d");
        var y = eq.lookupSimple("y");
        System.out.println("\nY:");
        printMatrix(y);
        var v = 1 / eq.lookupDouble("d");
        System.out.print("\nV=");
        System.out.println(v);
    }

    private static void printMatrix(SimpleMatrix m) {
        for (int i = 0; i < m.getNumRows(); i++) {
            for (int j = 0; j < m.getNumCols(); j++) {
                System.out.print(BigDecimal.valueOf(m.get(i, j)).setScale(2, RoundingMode.HALF_EVEN));
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}