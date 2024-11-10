import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.*;

public class MainPt2
{
    public static void main(String[] args)
    {
        if(args.length < 2)
        {
            System.out.println("Введіть межі");
            System.out.println("java MainPt2 [lowerBound] [upperBound]");
            return;
        }
        int lowerBound = Integer.parseInt(args[0]);
        int upperBound = Integer.parseInt(args[1]);
        Random random = new Random();
        final int arraySize = 60;

        List<Integer> array = new ArrayList<>();
        for(int i = 0; i < arraySize; i++)
        {
            array.add(lowerBound + random.nextInt(upperBound - lowerBound + 1));
        }
        System.out.println("Результати обробки масиву: " + array);

        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<Set<Integer>>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        final int chunkSize = 10;
        for(int i = 0; i < array.size(); i += chunkSize)
        {
            int end = Math.min(i + chunkSize, array.size());
            List<Integer> sublist = array.subList(i, end);

            Callable<Set<Integer>> task = new ArrayMultiplier(sublist);
            Future<Set<Integer>> future = executor.submit(task);
            futures.add(future);
        }
        
        for(boolean quit = false; !quit;)
        {
            System.out.println("Обробка масивів...");
            quit = true;
            for(Future<Set<Integer>> future : futures)
            {
                quit = quit && future.isDone();
            }

            try { Thread.sleep(1000); }
            catch(InterruptedException e) { e.printStackTrace(); }
        }

        Set<Integer> finalResults = new CopyOnWriteArraySet<>();
        for(Future<Set<Integer>> future : futures)
        {
            try
            {
                if(!future.isCancelled())
                {
                    finalResults.addAll(future.get());
                }
            }
            catch(InterruptedException | ExecutionException e)
            {
                e.printStackTrace();
            }
        }

        executor.shutdown();

        System.out.println("Результати обробки масиву: " + finalResults);
        System.out.println("Час роботи програми: " + (System.currentTimeMillis() - startTime) + " мс");
    }

    static class ArrayMultiplier implements Callable<Set<Integer>>
    {
        private final List<Integer> numbers;
    
        public ArrayMultiplier(List<Integer> numbers)
        {
            this.numbers = numbers;
        }
    
        @Override
        public Set<Integer> call()
        {
            Set<Integer> results = new CopyOnWriteArraySet<>();
            for(int i = 0; i < numbers.size() - 1; i += 2)
            {
                int product = numbers.get(i) * numbers.get(i + 1);
                results.add(product);
            }
            return results;
        }
    }
}

