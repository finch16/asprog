import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class AsyncComputation
{
    public static void main(String[] args)
    {
        
        CompletableFuture<List<Float>> generateNumbers = CompletableFuture.supplyAsync(() ->
        {
            long startTime = System.currentTimeMillis();
            List<Float> numbers = new ArrayList<>();
            Random random = new Random();
            for(int i = 0; i < 20; i++)
            {
                numbers.add(random.nextFloat() * 5);
            }
            long endTime = System.currentTimeMillis();
            System.out.println("Масив згенеровано за: " + (endTime - startTime) + " мс");
            return numbers;
        });

        CompletableFuture<Float> computeProduct = generateNumbers.thenApplyAsync(numbers ->
        {
            long startTime = System.currentTimeMillis();
            System.out.println("Обчислюємо добуток різниць...");
            float product = 1.0f;
            for(int i = 1; i < numbers.size(); i++)
            {
                product *= (numbers.get(i) - numbers.get(i - 1));
            }
            long endTime = System.currentTimeMillis();
            System.out.println("Добуток обчислюємо за: " + (endTime - startTime) + " мс");
            return product;
        });

        CompletableFuture<Void> printNumbers = generateNumbers.thenAcceptAsync(numbers ->
        {
            System.out.print("Початковий масив: ");
            for(int i = 1; i < numbers.size(); i++)
            {
                System.out.printf("%.2f ", numbers.get(i));
            }
            System.out.println();
        });

        CompletableFuture<Void> printResult = computeProduct.thenAcceptAsync(result ->
        {
            System.out.printf("Результат обчислень: [%.2f] ", result);
            System.out.println();
        });

        CompletableFuture<Void> finalStep = CompletableFuture.allOf(printNumbers, printResult).thenRunAsync(() ->
        {
            System.out.println("Усі асинхронні операції завершено");
        });

        finalStep.join();
    }
}
