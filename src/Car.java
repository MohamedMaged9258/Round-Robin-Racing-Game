import java.util.Random;

public class Car extends Thread {
    private final int id;
    private int speed;
    private int track;

    public Car(int id, int speed) {
        this.id = id;
        this.speed = speed;
        this.track = id;
    }

    public void run() {
        int position = 0;
        int num_of_cars = RoundRobinRacingGame.num_of_cars - 1;
        Random random = new Random();

        while (RoundRobinRacingGame.raceInProgress) {
            RoundRobinRacingGame.lock.lock();
            try {
                boolean canMove = true;
                RoundRobinRacingGame.condition.await();

                if (!RoundRobinRacingGame.raceInProgress) break;

                if ("Rain".equals(RoundRobinRacingGame.weatherCondition)) {
                    speed = random.nextInt(3) + 5;
                } else if ("Fog".equals(RoundRobinRacingGame.weatherCondition)) {
                    speed = random.nextInt(3) + 4;
                }

                for (int i = 0; i < RoundRobinRacingGame.obstcalsTrack.size(); i++) {
                    if (RoundRobinRacingGame.obstcalsTrack.get(i) == track && RoundRobinRacingGame.obstcalsPosition.get(i) <= position + speed) {
                        canMove = false;
                        boolean canChangeTrack = true;
                        if (track == 0) {
                            for (int j = 0; j < RoundRobinRacingGame.carsTrack.size(); j++) {
                                if (RoundRobinRacingGame.carsTrack.get(j) == track + 1 && RoundRobinRacingGame.carsPosition.get(j) == position) {
                                    canChangeTrack = false;
                                    break;
                                }
                            }
                            if (canChangeTrack) {
                                speed = random.nextInt(6) + 5;
                                track = 1;
                                RoundRobinRacingGame.carsTrack.set(id, track);
                            } else speed = 0;
                        } else if (track == num_of_cars) {
                            for (int j = 0; j < RoundRobinRacingGame.carsTrack.size(); j++) {
                                if (RoundRobinRacingGame.carsTrack.get(j) == num_of_cars - 1 && RoundRobinRacingGame.carsPosition.get(j) == position) {
                                    canChangeTrack = false;
                                    break;
                                }
                            }
                            if (canChangeTrack) {
                                speed = random.nextInt(6) + 5;
                                track = track - 1;
                                RoundRobinRacingGame.carsTrack.set(id, track);
                            } else speed = 0;
                        } else {
                            boolean previos_track_empty = true;
                            for (int j = 0; j < RoundRobinRacingGame.carsTrack.size(); j++) {
                                if (RoundRobinRacingGame.carsTrack.get(j) == track - 1 && RoundRobinRacingGame.carsPosition.get(j) == position) {
                                    previos_track_empty = false;
                                    break;
                                }
                            }
                            if (previos_track_empty) {
                                speed = random.nextInt(6) + 5;
                                track = track - 1;
                                RoundRobinRacingGame.carsTrack.set(id, track);
                            } else {
                                for (int j = 0; j < RoundRobinRacingGame.carsTrack.size(); j++) {
                                    if (RoundRobinRacingGame.carsTrack.get(j) == track + 1 && RoundRobinRacingGame.carsPosition.get(j) == position) {
                                        canChangeTrack = false;
                                        break;
                                    }
                                }
                                if (canChangeTrack) {
                                    speed = random.nextInt(6) + 5;
                                    track = track + 1;
                                    RoundRobinRacingGame.carsTrack.set(id, track);
                                } else speed = 0;
                            }
                        }

                    } else canMove = true;
                }

                if (canMove) {
                    position += speed;
                    RoundRobinRacingGame.carsPosition.set(id, position);
                }

                System.out.println("Car " + id + " moved to position " + position);

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