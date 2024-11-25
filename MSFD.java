import java.util.Random;
import java.util.concurrent.*;

public class MSFD
{
    public static void main(String[] args)
    {
        int rows = 1000;
        int cols = 1000;
        int minValue = 1;
        int maxValue = 100;

        int[][] matrix = generateMatrix(rows, cols, minValue, maxValue);

        System.out.println("Виконання через Fork/Join Framework (Work Stealing):");
        long startForkJoin = System.nanoTime();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Integer resultForkJoin = forkJoinPool.invoke(new ForkJoinTask(matrix, 0, rows - 1));
        long endForkJoin = System.nanoTime();

        System.out.println("Згенерований масив:");
        //printMatrix(matrix);

        System.out.println("Результат:");
        System.out.println("Fork/Join Framework: " + (resultForkJoin != null ? resultForkJoin : "Число не знайдено")
                + ", Час виконання: " + (endForkJoin - startForkJoin) / 1_000_000 + " ms");
    }

    private static int[][] generateMatrix(int rows, int cols, int min, int max)
    {
        Random random = new Random();
        int[][] matrix = new int[rows][cols];
        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < cols; j++)
            {
                matrix[i][j] = random.nextInt(max - min + 1) + min;
            }
        }
        return matrix;
    }

    private static void printMatrix(int[][] matrix)
    {
        for(int[] row : matrix)
        {
            for(int val : row)
            {
                System.out.print(val + " ");
            }
            System.out.println();
        }
    }

    private static class ForkJoinTask extends RecursiveTask<Integer>
    {
        private static final int THRESHOLD = 100;
        private final int[][] matrix;
        private final int startRow;
        private final int endRow;
    
        ForkJoinTask(int[][] matrix, int startRow, int endRow)
        {
            this.matrix = matrix;
            this.startRow = startRow;
            this.endRow = endRow;
        }
    
        @Override
        protected Integer compute()
        {
            if (endRow - startRow <= THRESHOLD)
            {
                for (int i = startRow; i <= endRow; i++)
                {
                    for (int j = 0; j < matrix[i].length; j++)
                    {
                        if (matrix[i][j] == i + j)
                        {
                            return matrix[i][j];
                        }
                    }
                }
                return null;
            }
            else
            {
                int mid = (startRow + endRow) / 2;
                ForkJoinTask task1 = new ForkJoinTask(matrix, startRow, mid);
                ForkJoinTask task2 = new ForkJoinTask(matrix, mid + 1, endRow);
                invokeAll(task1, task2);
    
                Integer result1 = task1.join();
                Integer result2 = task2.join();
                return result1 != null ? result1 : result2;
            }
        }
    }
}
