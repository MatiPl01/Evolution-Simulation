package my.project.simulation.utils;

import java.util.concurrent.TimeUnit;

public class Time {
    public static void sleep(int milliseconds) {
        try {
            Thread sleepingThread = new Thread(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(milliseconds);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            sleepingThread.start();
            sleepingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void setTimeout(Runnable runnable, int delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }
}
