package com.virtualthreads.service;

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
}

