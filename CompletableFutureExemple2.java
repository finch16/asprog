import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CompletableFutureExemple2
{
    public static void main(String[] args) throws ExecutionException, InterruptedException
    {
        CompletableFuture<Integer> dbFetchFuture = CompletableFuture.supplyAsync(() ->
        {
            try
            {
                TimeUnit.SECONDS.sleep(2);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            Random random = new Random();
            System.out.println("Дані отримано з бази");
            return random.nextInt(10);
        });

        CompletableFuture<Integer> processFuture = dbFetchFuture.thenApplyAsync(result ->
        {
            System.out.println("Обробка даних: " + result);
            if((result % 2) == 0) return result / 2;
            else return result + 3;
        });

        CompletableFuture<Integer> combinedFuture = processFuture.thenCombine(CompletableFuture.supplyAsync(() ->
        {
            try
            {
                TimeUnit.SECONDS.sleep(2);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            return 21;
        }), (dbResult, additionalValue) ->
        {
            System.out.println("Поєднання результатів: " + dbResult + " і " + additionalValue);
            return dbResult + additionalValue;
        });

        CompletableFuture<Void> paralelProcessFuture = CompletableFuture.runAsync(() ->
        {
            System.out.println("Паралений процес");
            try
            {
                TimeUnit.SECONDS.sleep(2);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            System.out.println("Завершення параленого процесу");
        });

        CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(combinedFuture, paralelProcessFuture);

        allOfFuture.join();
        System.out.println("Всі завдання завершено.");

        int resultFromCombined = combinedFuture.get();
        System.out.println("Результат: " + resultFromCombined);


        System.out.println("Нові завдання.");
        CompletableFuture<Integer> newProcess1Future = CompletableFuture.supplyAsync(() ->
        {
            Random random = new Random();
            int num = random.nextInt(10);
            System.out.println("Дані 1 отримано з бази [" + num + "]");
            return num;
        });
        CompletableFuture<Integer> newProcess2Future = CompletableFuture.supplyAsync(() ->
        {
            Random random = new Random();
            int num = random.nextInt(10);
            System.out.println("Дані 2 отримано з бази [" + num + "]");
            return num;
        });

        CompletableFuture<Object> anyOfFuture = CompletableFuture.anyOf(newProcess1Future, newProcess2Future);
        
        Object resultFromAnyOf = anyOfFuture.get();
        System.out.println("Результат: " + resultFromAnyOf);

    }
}
