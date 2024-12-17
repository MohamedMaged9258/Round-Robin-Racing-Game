public class Car extends Thread {
    private final int id;
    private int speed;

    public Car(int id, int speed) {
        this.id = id;
        this.speed = speed;
    }

    public void run() {
        int position = 0;

        while (RoundRobinRacingGame.raceInProgress) {
            RoundRobinRacingGame.lock.lock();
            try {
                RoundRobinRacingGame.condition.await();

                if (!RoundRobinRacingGame.raceInProgress) break;

                if ("Rain".equals(RoundRobinRacingGame.weatherCondition)) {
                    speed = Math.max(speed - 1, 1);
                } else if ("Fog".equals(RoundRobinRacingGame.weatherCondition)) {
                    speed = Math.max(speed - 2, 1);
                }

                position += speed;
                RoundRobinRacingGame.track.set(id, position);

//                System.out.println("Car " + id + " moved to position " + position);

                if (position >= RoundRobinRacingGame.track_length) {
                    RoundRobinRacingGame.raceInProgress = false;
                    System.out.println("Car " + id + " wins the race!");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                RoundRobinRacingGame.lock.unlock();
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}