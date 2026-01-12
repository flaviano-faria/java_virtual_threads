package com.virtualthreads.service;

/**
 * Service that encapsulates the work logic executed by threads during memory measurements.
 * This work is designed to keep threads alive and consume some CPU time to ensure
 * proper memory allocation is measured.
 */
public class ThreadWorkService {

    /**
     * Executes work that keeps a thread alive and performs some computation.
     * This method simulates typical thread work: a brief sleep followed by a computation loop.
     *
     * @throws InterruptedException if the thread is interrupted during execution
     */
    public void executeWork() throws InterruptedException {
        // Keep thread alive and do some work
        Thread.sleep(100);
        int sum = 0;
        for (int i = 0; i < 1000; i++) {
            sum += i;
        }
    }
}


