import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class MSTPE
{
    public static void main(String[] args)
    {
        int rows = 1000;
        int cols = 1000;
        int minValue = 1;
        int maxValue = 100;

        int[][] matrix = generateMatrix(rows, cols, minValue, maxValue);

        System.out.println("Виконання через Thread Pool Executor (Work Dealing):");
        long startExecutor = System.nanoTime();
        Integer resultExecutor = searchWithExecutor(matrix, rows);
        long endExecutor = System.nanoTime();

        System.out.println("Згенерований масив:");
        //printMatrix(matrix);

        System.out.println("Thread Pool Executor: " + (resultExecutor != null ? resultExecutor : "Число не знайдено")
                + ", Час виконання: " + (endExecutor - startExecutor) / 1_000_000 + " ms");
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

    private static Integer searchWithExecutor(int[][] matrix, int rows)
    {
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Integer>> futures = new ArrayList<>();

        int chunkSize = rows / numThreads;
        for(int i = 0; i < numThreads; i++)
        {
            int startRow = i * chunkSize;
            int endRow = (i == numThreads - 1) ? rows - 1 : startRow + chunkSize - 1;
            futures.add(executor.submit(() ->
            {
                for(int r = startRow; r <= endRow; r++)
                {
                    for(int c = 0; c < matrix[r].length; c++)
                    {
                        if(matrix[r][c] == r + c)
                        {
                            return matrix[r][c];
                        }
                    }
                }
                return null;
            }));
        }

        executor.shutdown();

        try
        {
            for(Future<Integer> future : futures)
            {
                Integer result = future.get();
                if(result != null)
                {
                    return result;
                }
            }
        }
        catch(InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
