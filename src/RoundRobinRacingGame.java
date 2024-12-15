import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RoundRobinRacingGame {
    public static int track_length;
    public static int num_of_cars;
    public static boolean raceInProgress = true;
    public static List<Integer> track = new ArrayList<>();
    public static String weatherCondition = "Clear";

    public static final Lock lock = new ReentrantLock();
    public static final Condition condition = lock.newCondition();

    public RoundRobinRacingGame() {
        for (int i = 0; i < num_of_cars; i++) {
            track.add(0);
        }

        List<Thread> carThreads = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < num_of_cars; i++) {
            int speed = random.nextInt(6) + 5;
            carThreads.add(new Car(i, speed));
        }

        for (Thread car : carThreads) {
            car.start();
        }

        Thread obstacles = new Obstacles();
        obstacles.setDaemon(true);
        obstacles.start();

        roundRobinScheduler(carThreads);


        for (Thread car : carThreads) {
            try {
                car.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Race finished!");
    }
    private static void roundRobinScheduler(List<Thread> carThreads) {
        while (raceInProgress) {
            for (Thread car : carThreads) {
                if (!raceInProgress) break;

                lock.lock();
                try {
                    condition.signalAll();
                } finally {
                    lock.unlock();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                printRaceTrack();
            }
        }
    }

    private static void printRaceTrack() {
        StringBuilder trackDisplay = new StringBuilder("\nRace Track:\n");
        for (int i = 0; i < track.size(); i++) {
            trackDisplay.append("Track ").append(i).append(": ");
            int position = track.get(i);
            for (int j = 0; j < track_length; j++) {
                if (j == position) {
                    trackDisplay.append("|>");
                } else {
                    trackDisplay.append("-");
                }
            }
            trackDisplay.append("\n");
        }
        System.out.println(trackDisplay);
    }
}
