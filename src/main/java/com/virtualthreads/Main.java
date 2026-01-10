package com.virtualthreads;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Total Memory Used: Single Platform Thread vs Single Virtual Thread ===\n");

        // Memory comparison for single threads
        compareSingleThreadMemory();
    }

    private static void compareSingleThreadMemory() throws InterruptedException {
        System.out.println("--- Total Memory Used by Single Thread ---\n");

        // Measure Platform Thread total memory
        System.out.println("Measuring total memory used by single Platform Thread...");
        long platformMemory = measureSinglePlatformThread();

        // Give JVM time to clean up between measurements
        System.gc();
        Thread.sleep(1000);

        // Measure Virtual Thread total memory
        System.out.println("\nMeasuring total memory used by single Virtual Thread...");
        long virtualMemory = measureSingleVirtualThread();

        // Results Summary
        System.out.println("\n=== Total Memory Comparison Results ===");
        System.out.printf("Total Memory - Single Platform Thread: %,d KB (%,d bytes)%n", 
                platformMemory / 1024, platformMemory);
        System.out.printf("Total Memory - Single Virtual Thread:  %,d KB (%,d bytes)%n", 
                virtualMemory / 1024, virtualMemory);
        System.out.printf("Memory Difference: %,d KB (%,d bytes)%n", 
                (platformMemory - virtualMemory) / 1024, platformMemory - virtualMemory);
        
        if (virtualMemory > 0) {
            System.out.printf("Platform thread uses ~%,.0fx more total memory than virtual thread%n",
                    (double) platformMemory / virtualMemory);
        }
        System.out.println("\nNote: Platform threads allocate a fixed ~1MB stack per thread,");
        System.out.println("      while virtual threads only allocate memory as needed (typically a few KB).");
    }

    private static long measureSinglePlatformThread() throws InterruptedException {
        Runtime runtime = Runtime.getRuntime();

        // Force GC and wait before measurement to get baseline
        System.gc();
        Thread.sleep(500);

        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        System.out.printf("Memory before thread creation: %,d KB (%,d bytes)%n", 
                memoryBefore / 1024, memoryBefore);

        // Create a single platform thread that does some work
        Thread platformThread = Thread.ofPlatform()
                .name("platform-thread-test")
                .start(() -> {
                    // Keep thread alive and do some work
                    try {
                        Thread.sleep(100);
                        int sum = 0;
                        for (int i = 0; i < 1000; i++) {
                            sum += i;
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });

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

    private static long measureSingleVirtualThread() throws InterruptedException {
        Runtime runtime = Runtime.getRuntime();

        // Force GC and wait before measurement to get baseline
        System.gc();
        Thread.sleep(500);

        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        System.out.printf("Memory before thread creation: %,d KB (%,d bytes)%n", 
                memoryBefore / 1024, memoryBefore);

        // Create a single virtual thread that does some work
        Thread virtualThread = Thread.ofVirtual()
                .name("virtual-thread-test")
                .start(() -> {
                    // Keep thread alive and do some work
                    try {
                        Thread.sleep(100);
                        int sum = 0;
                        for (int i = 0; i < 1000; i++) {
                            sum += i;
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });

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