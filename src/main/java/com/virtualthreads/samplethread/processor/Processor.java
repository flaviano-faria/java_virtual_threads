package com.virtualthreads.samplethread.processor;

import java.time.Duration;

public class Processor {

    public Runnable task(int processId){
        return () -> {
            System.out.println(Thread.currentThread() + "Executing task " + processId);

            try {
                Thread.sleep(Duration.ofSeconds(1));

            } catch (InterruptedException e){
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread() + "Executed task " + processId);
        };
    }
}
