import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class RoundRobinRacingGame {
    public static int track_length = 100;
    public static int num_of_cars = 5;
    public static boolean raceInProgress = true;
    public static List<Integer> track = new ArrayList<>();
    public static List<Thread> carThreads = new ArrayList<>();
    public static String weatherCondition = "Clear";

    public static final Lock lock = new ReentrantLock();
    public static final Condition condition = lock.newCondition();

    public RoundRobinRacingGame(int track_length, int num_of_cars) {
        RoundRobinRacingGame.track_length = track_length;
        RoundRobinRacingGame.num_of_cars = num_of_cars;
    }

    public void startGame() {
        carThreads = createCarThreads();
        initializeTrack();
        startThreads(carThreads);
        startObstaclesThread();
        runRoundRobinScheduler(carThreads);
        announceRaceCompletion();
    }

    private void initializeTrack() {
        for (int i = 0; i < num_of_cars; i++) {
            track.add(0);
        }
    }

    private List<Thread> createCarThreads() {
        List<Thread> carThreads = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < num_of_cars; i++) {
            int carSpeed = random.nextInt(6) + 5;
            carThreads.add(new Car(i, carSpeed));
        }
        return carThreads;
    }

    private void startThreads(List<Thread> threads) {
        for (Thread thread : threads) {
            thread.start();
        }
    }

    private void startObstaclesThread() {
        Thread obstacles = new Obstacles();
        obstacles.setDaemon(true);
        obstacles.start();
    }

    private void runRoundRobinScheduler(List<Thread> carThreads) {
        while (raceInProgress) {
            for (Thread car : carThreads) {
                if (!raceInProgress) break;
                lock.lock();
                printRaceTrack();
                try {
                    condition.signalAll();
                } finally {
                    lock.unlock();
                }
                delayScheduler(500);
            }
        }
    }

    private void delayScheduler(int delayMs) {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void announceRaceCompletion() {
        System.out.println("Race finished!");
    }

    public static void printRaceTrack() {
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
