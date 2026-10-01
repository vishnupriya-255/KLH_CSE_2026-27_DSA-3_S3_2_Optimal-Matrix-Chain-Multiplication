import java.util.Scanner;

public class OptimalMatrixChain {

    // ---------------------------------------------------------
    // Calculate the cost of one particular multiplication order
    // ---------------------------------------------------------
    static int calculateCost(int[] rows, int[] columns, String order) {

        int[] resultRows = new int[100];
        int[] resultColumns = new int[100];
        int[] costs = new int[100];

        int top = -1;

        for (int i = 0; i < order.length(); i++) {

            char ch = order.charAt(i);

            // If the character represents a matrix
            if (ch >= 'A' && ch <= 'Z') {

                int matrixNumber = ch - 'A';

                top++;

                resultRows[top] = rows[matrixNumber];
                resultColumns[top] = columns[matrixNumber];

                costs[top] = 0;
            }

            // When a closing bracket is found,
            // multiply the two sub-results
            else if (ch == ')') {

                int rightRows = resultRows[top];
                int rightColumns = resultColumns[top];
                int rightCost = costs[top];

                top--;

                int leftRows = resultRows[top];
                int leftColumns = resultColumns[top];
                int leftCost = costs[top];

                // Cost of multiplying two matrices
                int multiplicationCost =
                        leftRows * leftColumns * rightColumns;

                int totalCost =
                        leftCost
                        + rightCost
                        + multiplicationCost;

                // Store the resulting matrix
                resultRows[top] = leftRows;
                resultColumns[top] = rightColumns;
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
    // ---------------------------------------------------------
    static String getMatrixName(int number) {

        if (number < 26) {

            char name = (char) ('A' + number);

            return String.valueOf(name);
        }

        return "A" + number;
    }


    // ---------------------------------------------------------
    // Dynamic Programming algorithm
    // ---------------------------------------------------------
    static int[][] dynamicProgramming(
            int[] rows,
            int[] columns,
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

                    /*
                     * For:
                     *
                     * (A_i ... A_k) (A_k+1 ... A_j)
                     *
                     * The resulting left matrix is:
                     * rows[i-1] x columns[k-1]
                     *
                     * The resulting right matrix is:
                     * rows[k] x columns[j-1]
                     *
                     * Since matrices are compatible:
                     * columns[k-1] = rows[k]
                     */

                    int currentCost =
                            dp[i][k]
                            + dp[k + 1][j]
                            + rows[i - 1]
                            * columns[k - 1]
                            * columns[j - 1];

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

            System.out.print(getMatrixName(i - 1));

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

        System.out.println();

        System.out.print("       ");

        for (int i = 1; i <= n; i++) {

            System.out.printf(
                    "%10s",
                    getMatrixName(i - 1)
            );
        }

        System.out.println();

        for (int i = 1; i <= n; i++) {

            System.out.printf(
                    "%5s",
                    getMatrixName(i - 1)
            );

            for (int j = 1; j <= n; j++) {

                if (j < i) {

                    System.out.printf(
                            "%10s",
                            "-"
                    );

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
        // STEP 2: Create rows and columns arrays
        // -----------------------------------------------------

        int[] rows = new int[n];
        int[] columns = new int[n];


        // -----------------------------------------------------
        // STEP 3: Enter rows and columns
        // -----------------------------------------------------

        System.out.println();

        System.out.println(
                "======================================================"
        );

        System.out.println(
                "              ENTER MATRIX DIMENSIONS"
        );

        System.out.println(
                "======================================================"
        );

        System.out.println();

        System.out.println(
                "Enter the rows and columns of each matrix separately."
        );

        System.out.println();


        for (int i = 0; i < n; i++) {

            System.out.println(
                    "Matrix " + getMatrixName(i)
            );

            System.out.print("Enter number of rows: ");
            rows[i] = sc.nextInt();

            System.out.print("Enter number of columns: ");
            columns[i] = sc.nextInt();

            System.out.println();
        }


        // -----------------------------------------------------
        // STEP 4: Display entered matrices
        // -----------------------------------------------------

        System.out.println();

        System.out.println(
                "======================================================"
        );

        System.out.println(
                "                 ENTERED MATRICES"
        );

        System.out.println(
                "======================================================"
        );

        System.out.println();

        for (int i = 0; i < n; i++) {

            System.out.println(
                    getMatrixName(i)
                    + " = "
                    + rows[i]
                    + " x "
                    + columns[i]
            );
        }


        // -----------------------------------------------------
        // STEP 5: Check matrix compatibility
        // -----------------------------------------------------

        System.out.println();

        System.out.println(
                "======================================================"
        );

        System.out.println(
                "             CHECKING COMPATIBILITY"
        );

        System.out.println(
                "======================================================"
        );

        System.out.println();


        boolean possible = true;

        for (int i = 0; i < n - 1; i++) {

            System.out.println(
                    "Checking "
                    + getMatrixName(i)
                    + " x "
                    + getMatrixName(i + 1)
                    + "..."
            );

            System.out.println(
                    "Columns of "
                    + getMatrixName(i)
                    + " = "
                    + columns[i]
            );

            System.out.println(
                    "Rows of "
                    + getMatrixName(i + 1)
                    + " = "
                    + rows[i + 1]
            );


            if (columns[i] == rows[i + 1]) {

                System.out.println(
                        "Result: COMPATIBLE"
                );

            } else {

                System.out.println(
                        "Result: NOT COMPATIBLE"
                );

                System.out.println();

                System.out.println(
                        "Matrix multiplication is NOT possible."
                );

                System.out.println(
                        "Columns of "
                        + getMatrixName(i)
                        + " ("
                        + columns[i]
                        + ") must be equal to rows of "
                        + getMatrixName(i + 1)
                        + " ("
                        + rows[i + 1]
                        + ")."
                );

                possible = false;

                break;
            }

            System.out.println();
        }


        // -----------------------------------------------------
        // STEP 6: Stop if multiplication is not possible
        // -----------------------------------------------------

        if (!possible) {

            System.out.println(
                    "Program stopped because the matrices are incompatible."
            );

            sc.close();

            return;
        }


        // -----------------------------------------------------
        // STEP 7: All matrices are compatible
        // -----------------------------------------------------

        System.out.println(
                "======================================================"
        );

        System.out.println(
                "       ALL MATRICES ARE COMPATIBLE"
        );

        System.out.println(
                "======================================================"
        );

        System.out.println();

        System.out.println(
                "Matrix Chain Multiplication can be performed."
        );


        // -----------------------------------------------------
        // STEP 8: Generate all possible multiplication cases
        // -----------------------------------------------------

        String[] orders = new String[100000];

        int[] count = {0};

        generateOrders(
                0,
                n - 1,
                orders,
                count
        );


        // -----------------------------------------------------
        // STEP 9: Display all possible cases and their costs
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
                            rows,
                            columns,
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
        // STEP 10: Dynamic Programming
        // -----------------------------------------------------

        int[][] split =
                new int[n + 1][n + 1];

        int[][] dp =
                dynamicProgramming(
                        rows,
                        columns,
                        n,
                        split
                );


        // -----------------------------------------------------
        // STEP 11: Display DP table
        // -----------------------------------------------------

        printDPTable(
                dp,
                n
        );


        // -----------------------------------------------------
        // STEP 12: Display optimal result
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
        // STEP 13: Final summary
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
