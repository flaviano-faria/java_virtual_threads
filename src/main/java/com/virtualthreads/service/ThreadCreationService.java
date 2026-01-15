package com.virtualthreads.service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

/**
 * Service responsible for creating and starting threads for memory measurement purposes.
 * This service provides factory methods for creating both platform and virtual threads
 * with consistent work execution.
 */
public class ThreadCreationService {

    private final ThreadWorkService workService;

    /**
     * Constructs a ThreadCreationService with the specified work service.
     *
     * @param workService the service that provides the work logic for threads
     */
    public ThreadCreationService(ThreadWorkService workService) {
        this.workService = workService;
    }

    /**
     * Creates and starts a platform thread with the specified name that executes work logic.
     *
     * @param threadName the name to assign to the thread
     * @return the started platform thread
     */
    public Thread createPlatformThread(String threadName) {
        return Thread.ofPlatform()
                .name(threadName)
                .start(() -> {
                    try {
                        workService.executeWork();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
    }

    /**
     * Creates and starts a virtual thread with the specified name that executes work logic.
     *
     * @param threadName the name to assign to the thread
     * @return the started virtual thread
     */
    public Thread createVirtualThread(String threadName) {
        return Thread.ofVirtual()
                .name(threadName)
                .start(() -> {
                    try {
                        workService.executeWork();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
    }

    /**
     * Creates and submits 1000 tasks to a cached thread pool and measures the execution time.
     * This method demonstrates the usage of ExecutorService with platform threads.
     * The executor is automatically closed using try-with-resources, which waits for
     * all submitted tasks to complete before shutdown.
     *
     * @param threadName the name identifier for this thread pool execution (used for logging)
     */
    public void createCachedThread(String threadName) {
        var begin = Instant.now();
        try (var executor = Executors.newCachedThreadPool()) {
            IntStream.range(0, 1000).forEach(
                    i -> executor.submit(() -> {
                        try {
                            Thread.sleep(Duration.ofSeconds(1));
                            return i;
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return -1;
                        }
                    }));
        }
        var end = Instant.now();
        System.out.printf("Cached thread pool (%s) completed in %s%n", threadName, Duration.between(begin, end));
    }

    public void createFixedThreadPool(String threadName) {
        var begin = Instant.now();
        try (var executor = Executors.newFixedThreadPool(1000)) {
            IntStream.range(0, 1000).forEach(
                    i -> executor.submit(() -> {
                        try {
                            Thread.sleep(Duration.ofSeconds(1));
                            return i;
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return -1;
                        }
                    }));
        }
        var end = Instant.now();
        System.out.printf("Fixed thread pool (%s) completed in %s%n", threadName, Duration.between(begin, end));
    }


    public void createVirtualThreadPoc(String threadName) {
        var begin = Instant.now();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 1000).forEach(
                    i -> executor.submit(() -> {
                        try {
                            Thread.sleep(Duration.ofSeconds(1));
                            return i;
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return -1;
                        }
                    }));
        }
        var end = Instant.now();
        System.out.printf("virtual thread poc (%s) completed in %s%n", threadName, Duration.between(begin, end));
    }

    public void checkVirtualThread(String threadName) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 1000).forEach(
                    i -> executor.submit(() -> {
                            System.out.println("Thread " + threadName + "is virtual:" + Thread.currentThread().isVirtual());
                            try {
                                Thread.sleep(Duration.ofSeconds(1));
                                return i;
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return -1;
                            }
                        }
                    ));
        }
    }

    public void pocVirtualThreadsJdk24(String threadName) {
        var begin = Instant.now();
        ThreadFactory factory = Thread.ofVirtual().factory();
        final Object lock = new Object();
        try (var executor = Executors.newThreadPerTaskExecutor(factory)) {
            IntStream.range(0, 10000).forEach(
                    i -> executor.submit(() -> {
                                System.out.println("Thread " + threadName + "is virtual:" + Thread.currentThread().isVirtual());

                                synchronized (lock){
                                    try {
                                        Thread.sleep(Duration.ofSeconds(1));
                                        return i;
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        return -1;
                                    }
                                }

                            }
                    ));
        }
        var end = Instant.now();
        System.out.printf("Poc virtual thread java 24 (%s) completed in %s%n", threadName, Duration.between(begin, end));
    }
}


