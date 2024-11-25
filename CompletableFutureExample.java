import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

class Tuple
{
    public String str;
    public int num1;
    public int num2;

    public Tuple(String str, int num1, int num2)
    {
        this.str = str;
        this.num1 = num1;
        this.num2 = num2;
    }
}

public class CompletableFutureExample
{
    public static void main(String[] args)
    {
        System.out.println("Старт програми...");

        //Не повертає дані після виконання завдання
        CompletableFuture<Void> startTask = CompletableFuture.runAsync(() ->
        {
            System.out.println("runAsync: Асинхронна задача стартувала!");
            sleep(2); //Симуляція довгої операції
        });

        //Повертає дані після виконання завдання 
        CompletableFuture<Tuple> dataTask = CompletableFuture.supplyAsync(() ->
        {
            System.out.println("supplyAsync: Завантажуємо дані...");
            try(BufferedReader br = new BufferedReader(new FileReader("data.txt")))
            {
                String line = br.readLine();
                if(line != null)
                {
                    String[] parts = line.split(" ");
                    if(parts.length >= 3)
                    {
                        String str = parts[0];
                        int num1 = Integer.parseInt(parts[1]);
                        int num2 = Integer.parseInt(parts[2]);
                        sleep(3); //Симуляція довгої операції
                        return new Tuple(str, num1, num2);
                    }
                }
                throw new IOException("Некоректний формат даних.");
            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }
        });
        
        dataTask.exceptionally(ex -> {
            System.err.println("Помилка: " + ex.getMessage());
            return null;
        });

        //Бере значення попереднього завдання та повертає дані після виконання нового завдання
        CompletableFuture<Tuple> modifiedDataTask = dataTask.thenApplyAsync(data ->
        {
            System.out.println("thenApplyAsync: Модифікуємо дані...");
            sleep(2); //Симуляція довгої операції
            return new Tuple(data.str, data.num1 + data.num2, data.num1 * data.num2) ;
        });

        //Бере значення попереднього завдання та не повертає дані після виконання нового завдання
        CompletableFuture<Void> printTask = modifiedDataTask.thenAcceptAsync(mData ->
        {
            System.out.print("thenAcceptAsync: Результат: ");
            System.out.print("рядок [" + mData.str + "] ");
            System.out.print("сума [" + mData.num1 + "] ");
            System.out.print("добуток [" + mData.num2 + "] ");
            System.out.println();
        });

        //Не бере значення попереднього завдання та не повертає дані після виконання нового завдання
        CompletableFuture<Void> finalTask = printTask.thenRunAsync(() ->
        {
            System.out.println("thenRunAsync: Усі фонові задачі завершено.");
        });

        //Чекаємо на завершення всіх завдань
        CompletableFuture.allOf(startTask, finalTask).join();

        System.out.println("Програма завершена.");
    }

    private static void sleep(int seconds)
    {
        try
        {
            TimeUnit.SECONDS.sleep(seconds);
        }
        catch(InterruptedException e)
        {
            System.err.println("Помилка при паузі: " + e.getMessage());
        }
    }
}

