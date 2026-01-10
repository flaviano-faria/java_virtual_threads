package com.virtualthreads.metrics;

import com.virtualthreads.service.ThreadCreationService;
import com.virtualthreads.service.ThreadWorkService;

/**
 * Provides memory measurement capabilities for comparing platform threads and virtual threads.
 * This class encapsulates the logic for measuring memory usage of different thread types.
 */
public class ThreadMemoryMetrics {

    private final ThreadCreationService threadCreationService;

    /**
     * Constructs a ThreadMemoryMetrics with default services.
     */
    public ThreadMemoryMetrics() {
        this.threadCreationService = new ThreadCreationService(new ThreadWorkService());
    }

    /**
     * Constructs a ThreadMemoryMetrics with the specified thread creation service.
     * This constructor allows for dependency injection and testing.
     *
     * @param threadCreationService the service for creating threads
     */
    public ThreadMemoryMetrics(ThreadCreationService threadCreationService) {
        this.threadCreationService = threadCreationService;
    }

    /**
     * Measures the total memory used by a single platform thread.
     * The measurement includes memory allocated for the thread stack and related structures.
     *
     * @return the total memory used in bytes
     * @throws InterruptedException if the measurement is interrupted
     */
    public long measureSinglePlatformThread() throws InterruptedException {
        Runtime runtime = Runtime.getRuntime();

        // Force GC and wait before measurement to get baseline
        System.gc();
        Thread.sleep(500);

        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        System.out.printf("Memory before thread creation: %,d KB (%,d bytes)%n", 
                memoryBefore / 1024, memoryBefore);

        // Create a single platform thread that does some work
        Thread platformThread = threadCreationService.createPlatformThread("platform-thread-test");

        // Wait for thread to start and allocate its stack memory
        Thread.sleep(200);

        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long totalMemoryUsed = memoryAfter - memoryBefore;

        platformThread.join();

        System.out.printf("Memory after thread creation:  %,d KB (%,d bytes)%n", 
                memoryAfter / 1024, memoryAfter);
        System.out.printf("Total memory used by thread:   %,d KB (%,d bytes)%n", 
                totalMemoryUsed / 1024, totalMemoryUsed);

        return totalMemoryUsed;
    }

    /**
     * Measures the total memory used by a single virtual thread.
     * Virtual threads typically use significantly less memory than platform threads
     * as they don't pre-allocate a fixed stack size.
     *
     * @return the total memory used in bytes
     * @throws InterruptedException if the measurement is interrupted
     */
    public long measureSingleVirtualThread() throws InterruptedException {
        Runtime runtime = Runtime.getRuntime();

        // Force GC and wait before measurement to get baseline
        System.gc();
        Thread.sleep(500);

        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        System.out.printf("Memory before thread creation: %,d KB (%,d bytes)%n", 
                memoryBefore / 1024, memoryBefore);

        // Create a single virtual thread that does some work
        Thread virtualThread = threadCreationService.createVirtualThread("virtual-thread-test");

        // Wait for thread to start and allocate memory
        Thread.sleep(200);

        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long totalMemoryUsed = memoryAfter - memoryBefore;

        virtualThread.join();

        System.out.printf("Memory after thread creation:  %,d KB (%,d bytes)%n", 
                memoryAfter / 1024, memoryAfter);
        System.out.printf("Total memory used by thread:   %,d KB (%,d bytes)%n", 
                totalMemoryUsed / 1024, totalMemoryUsed);

        return totalMemoryUsed;
    }
}

