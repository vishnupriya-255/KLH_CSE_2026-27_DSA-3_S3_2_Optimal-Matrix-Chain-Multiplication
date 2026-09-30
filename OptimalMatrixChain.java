
import java.util.Scanner;

public class OptimalMatrixChain {

    // ---------------------------------------------------------
    // Calculate the cost of one particular multiplication order
    // ---------------------------------------------------------
    static int calculateCost(int[] dimensions, String order) {

        int[] rows = new int[100];
        int[] columns = new int[100];
        int[] costs = new int[100];

        int top = -1;

        for (int i = 0; i < order.length(); i++) {

            char ch = order.charAt(i);

            // If the character represents a matrix
            if (ch >= 'A' && ch <= 'Z') {

                int matrixNumber = ch - 'A' + 1;

                top++;

                rows[top] = dimensions[matrixNumber - 1];
                columns[top] = dimensions[matrixNumber];

                costs[top] = 0;
            }

            // When a closing bracket is found,
            // multiply the two sub-results
            else if (ch == ')') {

                int rightRows = rows[top];
                int rightColumns = columns[top];
                int rightCost = costs[top];

                top--;

                int leftRows = rows[top];
                int leftColumns = columns[top];
                int leftCost = costs[top];

                // Cost of multiplying two matrices
                int multiplicationCost =
                        leftRows * leftColumns * rightColumns;

                int totalCost =
                        leftCost
                        + rightCost
                        + multiplicationCost;

                // Store the resulting matrix
                rows[top] = leftRows;
                columns[top] = rightColumns;
                costs[top] = totalCost;
            }
        }

        return costs[top];
    }


    // ---------------------------------------------------------
    // Generate all possible parenthesizations
    // ---------------------------------------------------------
    static void generateOrders(
            int start,
            int end,
            String[] result,
            int[] count) {

        // Only one matrix
        if (start == end) {

            result[count[0]] = getMatrixName(start);

            count[0]++;

            return;
        }

        // Try every possible splitting point
        for (int k = start; k < end; k++) {

            String[] leftOrders = new String[10000];
            String[] rightOrders = new String[10000];

            int[] leftCount = {0};
            int[] rightCount = {0};

            generateOrders(
                    start,
                    k,
                    leftOrders,
                    leftCount
            );

            generateOrders(
                    k + 1,
                    end,
                    rightOrders,
                    rightCount
            );

            // Combine all left and right possibilities
            for (int i = 0; i < leftCount[0]; i++) {

                for (int j = 0; j < rightCount[0]; j++) {

                    result[count[0]] =
                            "("
                            + leftOrders[i]
                            + " * "
                            + rightOrders[j]
                            + ")";

                    count[0]++;
                }
            }
        }
    }


    // ---------------------------------------------------------
    // Generate matrix name
    // Supports A, B, C... and then A1, A2... if needed
    // ---------------------------------------------------------
    static String getMatrixName(int number) {

        if (number <= 26) {

            char name = (char) ('A' + number - 1);

            return String.valueOf(name);
        }

        return "A" + number;
    }


    // ---------------------------------------------------------
    // Dynamic Programming algorithm
    // ---------------------------------------------------------
    static int[][] dynamicProgramming(
            int[] dimensions,
            int n,
            int[][] split) {

        int[][] dp = new int[n + 1][n + 1];

        // length = number of matrices in the chain
        for (int length = 2; length <= n; length++) {

            for (int i = 1; i <= n - length + 1; i++) {

                int j = i + length - 1;

                // Initially set to maximum value
                dp[i][j] = Integer.MAX_VALUE;

                // Try every possible split
                for (int k = i; k < j; k++) {

                    int currentCost =
                            dp[i][k]
                            + dp[k + 1][j]
                            + dimensions[i - 1]
                            * dimensions[k]
                            * dimensions[j];

                    // Store the minimum cost
                    if (currentCost < dp[i][j]) {

                        dp[i][j] = currentCost;

                        split[i][j] = k;
                    }
                }
            }
        }

        return dp;
    }


    // ---------------------------------------------------------
    // Print optimal parenthesization
    // ---------------------------------------------------------
    static void printOptimalOrder(
            int[][] split,
            int i,
            int j) {

        if (i == j) {

            System.out.print(getMatrixName(i));

            return;
        }

        System.out.print("(");

        printOptimalOrder(
                split,
                i,
                split[i][j]
        );

        System.out.print(" * ");

        printOptimalOrder(
                split,
                split[i][j] + 1,
                j
        );

        System.out.print(")");
    }


    // ---------------------------------------------------------
    // Print DP cost table
    // ---------------------------------------------------------
    static void printDPTable(
            int[][] dp,
            int n) {

        System.out.println();

        System.out.println(
                "======================================================"
        );

        System.out.println(
                "                    DP COST TABLE"
        );

        System.out.println(
                "======================================================"
        );

        System.out.print("       ");

        for (int i = 1; i <= n; i++) {

            System.out.printf("%10s", getMatrixName(i));
        }

        System.out.println();

        for (int i = 1; i <= n; i++) {

            System.out.printf(
                    "%5s",
                    getMatrixName(i)
            );

            for (int j = 1; j <= n; j++) {

                if (j < i) {

                    System.out.printf("%10s", "-");

                } else {

                    System.out.printf(
                            "%10d",
                            dp[i][j]
                    );
                }
            }

            System.out.println();
        }
    }


    // ---------------------------------------------------------
    // MAIN METHOD
    // ---------------------------------------------------------
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println(
                "======================================================"
        );

        System.out.println(
                "          OPTIMAL MATRIX CHAIN MULTIPLICATION"
        );

        System.out.println(
                "======================================================"
        );


        // -----------------------------------------------------
        // STEP 1: Number of matrices
        // -----------------------------------------------------

        System.out.print(
                "\nEnter the number of matrices: "
        );

        int n = sc.nextInt();


        // Check minimum number of matrices
        if (n < 2) {

            System.out.println(
                    "Please enter at least 2 matrices."
            );

            sc.close();

            return;
        }


        // -----------------------------------------------------
        // STEP 2: Create dimensions array
        // -----------------------------------------------------

        int[] dimensions = new int[n + 1];


        // -----------------------------------------------------
        // STEP 3: Explain input format dynamically
        // -----------------------------------------------------

        System.out.println();

        System.out.println(
                "======================================================"
        );

        System.out.println(
                "                    INPUT FORMAT"
        );

        System.out.println(
                "======================================================"
        );

        System.out.println();

        System.out.println(
                "You have entered " + n + " matrices."
        );

        System.out.println(
                "Therefore, you must enter "
                + (n + 1)
                + " dimensions."
        );

        System.out.println();

        System.out.println(
                "Enter all dimensions in ONE LINE."
        );

        System.out.println();

        System.out.println(
                "General format:"
        );

        System.out.print("d1 d2 d3");

        for (int i = 4; i <= n + 1; i++) {

            System.out.print(" d" + i);
        }

        System.out.println();

        System.out.println();

        System.out.println(
                "The matrices will be:"
        );

        for (int i = 1; i <= n; i++) {

            System.out.println(
                    getMatrixName(i)
                    + " = d" + i
                    + " x d" + (i + 1)
            );
        }

        System.out.println();

        System.out.print(
                "Enter the dimensions: "
        );


        // -----------------------------------------------------
        // STEP 4: Read dimensions
        // -----------------------------------------------------

        for (int i = 0; i <= n; i++) {

            dimensions[i] = sc.nextInt();
        }


        // -----------------------------------------------------
        // STEP 5: Display matrices
        // -----------------------------------------------------

        System.out.println();

        System.out.println(
                "======================================================"
        );

        System.out.println(
                "                       MATRICES"
        );

        System.out.println(
                "======================================================"
        );

        for (int i = 1; i <= n; i++) {

            System.out.println(
                    getMatrixName(i)
                    + " = "
                    + dimensions[i - 1]
                    + " x "
                    + dimensions[i]
            );
        }


        // -----------------------------------------------------
        // STEP 6: Generate all possible multiplication cases
        // -----------------------------------------------------

        String[] orders = new String[100000];

        int[] count = {0};

        generateOrders(
                1,
                n,
                orders,
                count
        );


        // -----------------------------------------------------
        // STEP 7: Display all possible cases and their costs
        // -----------------------------------------------------

        System.out.println();

        System.out.println(
                "======================================================"
        );

        System.out.println(
                "          ALL POSSIBLE MULTIPLICATION CASES"
        );

        System.out.println(
                "======================================================"
        );

        int minimumCost = Integer.MAX_VALUE;

        String bestOrder = "";


        for (int i = 0; i < count[0]; i++) {

            String order = orders[i];

            int cost =
                    calculateCost(
                            dimensions,
                            order
                    );

            System.out.println();

            System.out.println(
                    "Case " + (i + 1)
            );

            System.out.println(
                    "Order : " + order
            );

            System.out.println(
                    "Cost  : "
                    + cost
                    + " scalar multiplications"
            );


            // Find minimum cost
            if (cost < minimumCost) {

                minimumCost = cost;

                bestOrder = order;
            }
        }


        // -----------------------------------------------------
        // STEP 8: Dynamic Programming
        // -----------------------------------------------------

        int[][] split =
                new int[n + 1][n + 1];

        int[][] dp =
                dynamicProgramming(
                        dimensions,
                        n,
                        split
                );


        // -----------------------------------------------------
        // STEP 9: Display DP table
        // -----------------------------------------------------

        printDPTable(
                dp,
                n
        );


        // -----------------------------------------------------
        // STEP 10: Display optimal result
        // -----------------------------------------------------

        System.out.println();

        System.out.println(
                "======================================================"
        );

        System.out.println(
                "                    OPTIMAL RESULT"
        );

        System.out.println(
                "======================================================"
        );

        System.out.println();

        System.out.println(
                "Optimal Parenthesization:"
        );

        printOptimalOrder(
                split,
                1,
                n
        );

        System.out.println();

        System.out.println();

        System.out.println(
                "Minimum Scalar Multiplications: "
                + dp[1][n]
        );


        // -----------------------------------------------------
        // STEP 11: Final summary
        // -----------------------------------------------------

        System.out.println();

        System.out.println(
                "======================================================"
        );

        System.out.println(
                "                     SUMMARY"
        );

        System.out.println(
                "======================================================"
        );

        System.out.println();

        System.out.println(
                "Number of matrices       : " + n
        );

        System.out.println(
                "Number of possible cases : "
                + count[0]
        );

        System.out.println(
                "Best order from cases    : "
                + bestOrder
        );

        System.out.println(
                "Minimum cost             : "
                + minimumCost
        );

        System.out.println(
                "DP optimal cost          : "
                + dp[1][n]
        );

        System.out.println();

        System.out.println(
                "======================================================"
        );

        System.out.println(
                "                  PROGRAM COMPLETED"
        );

        System.out.println(
                "======================================================"
        );

        sc.close();
    }
}


