import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class AsyncArrayProcessing
{

    public static void main(String[] args)
    {
        CompletableFuture<int[]> generateArrayFuture = CompletableFuture.supplyAsync(() ->
        {
            long startTime = System.currentTimeMillis();
            int[] array = IntStream.range(0, 10)
                .map(i -> ThreadLocalRandom.current().nextInt(1, 10))
                .toArray();
            long endTime = System.currentTimeMillis();
            System.out.println("Масив згенеровано за: " + (endTime - startTime) + " мс");
            return array;
        });

        CompletableFuture<int[]> addTenFuture = generateArrayFuture.thenApplyAsync(array ->
        {
            long startTime = System.currentTimeMillis();
            for(int i = 0; i < array.length; i++)
            {
                array[i] += 10;
            }
            long endTime = System.currentTimeMillis();
            System.out.println("Додавання 10 завершено за: " + (endTime - startTime) + " мс");
            return array;
        });

        CompletableFuture<double[]> divideByTwoFuture = addTenFuture.thenApplyAsync(array ->
        {
            long startTime = System.currentTimeMillis();
            double[] resultArray = new double[array.length];
            for(int i = 0; i < array.length; i++)
            {
                resultArray[i] = array[i] / 2.0;
            }
            long endTime = System.currentTimeMillis();
            System.out.println("Ділення на 2 завершено за: " + (endTime - startTime) + " мс");
            return resultArray;
        });

        CompletableFuture<Void> printInitialArrayFuture = generateArrayFuture.thenAcceptAsync(array ->
        {
            System.out.print("Початковий масив: ");
            for(int num : array)
            {
                System.out.print("G[" + num + "] ");
            }
            System.out.println();
        });

        CompletableFuture<Void> printAddTenFuture = addTenFuture.thenAcceptAsync(array ->
        {
            System.out.print("Масив після додавання 10: ");
            for(int num : array)
            {
                System.out.print("A[" + num + "] ");
            }
            System.out.println();
        });

        CompletableFuture<Void> printDivideByTwoFuture = divideByTwoFuture.thenAcceptAsync(resultArray ->
        {
            System.out.print("Результат ділення: ");
            for (double num : resultArray)
            {
                System.out.printf("D[%.2f] ", num);
            }
            System.out.println();
        });

        CompletableFuture<Void> allTasksFuture = CompletableFuture.allOf
        (
            printInitialArrayFuture,
            printAddTenFuture,
            printDivideByTwoFuture
        );

        allTasksFuture.thenRun(() ->
        {
            System.out.println("Усі завдання виконано!");
        });

        allTasksFuture.join();
    }
}
