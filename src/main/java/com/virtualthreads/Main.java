package com.virtualthreads;

import com.virtualthreads.metrics.ThreadMemoryMetrics;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Total Memory Used: Single Platform Thread vs Single Virtual Thread ===\n");

        // Memory comparison for single threads
        compareSingleThreadMemory();
            }

    private static void compareSingleThreadMemory() throws InterruptedException {
        System.out.println("--- Total Memory Used by Single Thread ---\n");

        ThreadMemoryMetrics metrics = new ThreadMemoryMetrics();

        // Measure Platform Thread total memory
        System.out.println("Measuring total memory used by single Platform Thread...");
        long platformMemory = metrics.measureSinglePlatformThread();

        // Give JVM time to clean up between measurements
        System.gc();
        Thread.sleep(1000);

        // Measure Virtual Thread total memory
        System.out.println("\nMeasuring total memory used by single Virtual Thread...");
        long virtualMemory = metrics.measureSingleVirtualThread();

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

        metrics.measureCachedThread();
    }


}