package com.rain.boy.iptracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.*;
public final class Executor {
    private static Logger LOG = LoggerFactory.getLogger(Executor.class);
    private static ExecutorService executor = Executors.newFixedThreadPool(2);
    private static ScheduledExecutorService schedulerExecutor = Executors.newScheduledThreadPool(2);

    public static void run(Runnable runnable) throws ExecutionException, InterruptedException {
        getExecutor().submit(runnable).get();
    }

    public static void run(Runnable runnable, long timeout, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {

            getExecutor().submit(runnable).get(timeout, timeUnit);

    }

    public static void run(Runnable runnable, long duration) throws InterruptedException, ExecutionException, TimeoutException {
        run(runnable, duration, TimeUnit.SECONDS);
    }

    public static void exe(Runnable runnable) {
        getExecutor().execute(runnable);
    }

    public static Future<?> submit(Runnable runnable) {
        return getExecutor().submit(runnable);
    }

    public static <T> Future<T> submit(Callable<T> callable) {
        return getExecutor().submit(callable);
    }

    public static ScheduledFuture delay(Runnable runnable, long delay, TimeUnit timeUnit) {
        return getScheduledExecutor().schedule(runnable, delay, timeUnit);
    }


    public static <T> ScheduledFuture<T> delay(Callable<T> callable, long delay, TimeUnit timeUnit) {
        return getScheduledExecutor().schedule(callable, delay, timeUnit);
    }

    public static ScheduledFuture delay(Runnable runnable, long duration) {
        return delay(runnable, duration, TimeUnit.SECONDS);
    }

    public static <T> ScheduledFuture<T> delay(Callable<T> callable, long duration) {
        return delay(callable, duration, TimeUnit.SECONDS);
    }

    public static ScheduledFuture fixedRate(Runnable runnable, long initialDelay, long period, TimeUnit timeUnit) {
        return getScheduledExecutor().scheduleAtFixedRate(runnable, initialDelay, period, timeUnit);
    }

    public static ScheduledFuture fixedRate(Runnable runnable, int initialSecond, long duration) {
        return fixedRate(runnable, initialSecond, duration, TimeUnit.SECONDS);
    }

    public static ScheduledFuture fixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit) {
        return getScheduledExecutor().scheduleWithFixedDelay(runnable, initialDelay, delay, timeUnit);
    }

    public static ScheduledFuture fixedDelay(Runnable runnable, long initialDelay, long duration) {
        return fixedDelay(runnable, initialDelay, duration, TimeUnit.SECONDS);
    }

    public static ExecutorService getExecutor() {
        return executor;
    }

    public static ScheduledExecutorService getScheduledExecutor() {
        return schedulerExecutor;
    }


}
