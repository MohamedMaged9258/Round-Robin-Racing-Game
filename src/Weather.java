import java.util.Random;

public class Weather extends Thread{
    private final Random random = new Random();

    @Override
    public void run() {
        String[] weatherConditions = {"Clear", "Rain", "Fog"};

        while (RoundRobinRacingGame.raceInProgress) {
            try {
                Thread.sleep(10000);
                RoundRobinRacingGame.weatherCondition = weatherConditions[random.nextInt(weatherConditions.length)];
                System.out.println("Weather updated: " + RoundRobinRacingGame.weatherCondition);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
