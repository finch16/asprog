import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledTaskDemo
{
    public static void main(String[] args)
    {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        Scanner scanner = new Scanner(System.in);
        Instant startTime = Instant.now();
        Random random = new Random();
        
        Runnable userTask = () ->
        {
            System.out.println("Виконати задачу? Введіть Y/N:");
            String response = scanner.nextLine();
            
            if("Y".equals(response))
            {
                System.out.println("Задача виконується...");
            }
            else if("N".equals(response))
            {
                System.out.println("Задача пропущена.");
            }
            else
            {
                System.out.println("Некоректне введення. Спробуйте знову.");
            }
        };

        Runnable timeTask = new Runnable()
        {
            @Override
            public void run()
            {
                long elapsedSeconds = Duration.between(startTime, Instant.now()).getSeconds();
                System.out.println("Час виконання: " + elapsedSeconds + " секунд.");

                scheduler.schedule(this, random.nextInt(10) + 1, TimeUnit.SECONDS);
            }
        };

        scheduler.scheduleAtFixedRate(userTask, 0, 10, TimeUnit.SECONDS);
        scheduler.schedule(timeTask, random.nextInt(10) + 1, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            scanner.close();
            scheduler.shutdown();
            System.out.println("Програма завершена.");
        }));
    }
}
