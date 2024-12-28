import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.*;

public class WeatherComparison
{
    public static void main(String[] args)
    {
        CompletableFuture<WeatherData> c1f = CompletableFuture.supplyAsync(() -> fetchWeatherData("City1"));
        CompletableFuture<WeatherData> c2f = CompletableFuture.supplyAsync(() -> fetchWeatherData("City2"));
        CompletableFuture<WeatherData> c3f = CompletableFuture.supplyAsync(() -> fetchWeatherData("City3"));

        CompletableFuture<Void> comparisonFuture = CompletableFuture.allOf(c1f, c2f, c3f).thenRunAsync(() ->
        {
            try
            {
                WeatherData city1 = c1f.get();
                WeatherData city2 = c2f.get();
                WeatherData city3 = c3f.get();

                analyzeWeather(city1, city2, city3);
            } 
            catch(Exception e)
            {
                e.printStackTrace();
            }
        });

        comparisonFuture.join();
    }

    private static WeatherData fetchWeatherData(String city)
    {
        System.out.println("Fetching weather data for " + city);
        Random rand = new Random();
        try
        {
            Thread.sleep((long)(rand.nextInt(3000)));
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        return new WeatherData(city, 20 + rand.nextInt(15), 50 + rand.nextInt(20), 5 + rand.nextInt(10));
    }

    private static void analyzeWeather(WeatherData... cities)
    {
        Arrays.sort(cities, Comparator.comparingDouble(WeatherData::getTemperature).reversed());
        System.out.println("Cities sorted by temperature (highest to lowest):");
        for(WeatherData city : cities)
        {
            System.out.println(city);
        }
    }
}

class WeatherData
{
    private final String city;
    private final double temperature;
    private final double humidity;
    private final double windSpeed;

    public WeatherData(String city, double temperature, double humidity, double windSpeed)
    {
        this.city = city;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }

    public String getCity()
    {
        return city;
    }

    public double getTemperature()
    {
        return temperature;
    }

    public double getHumidity()
    {
        return humidity;
    }

    public double getWindSpeed()
    {
        return windSpeed;
    }

    @Override
    public String toString()
    {
        return String.format("%s -> T: %.2f°C, H: %.2f%%, WS: %.2f m/s", city, temperature, humidity, windSpeed);
    }
}
