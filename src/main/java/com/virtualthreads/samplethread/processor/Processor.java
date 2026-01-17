package com.virtualthreads.samplethread.processor;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Processor {

    public Runnable taskSingleThread(int processId){

        return () -> {
            System.out.println(Thread.currentThread() + "Executing task " + processId);

            try {
                Thread.sleep(Duration.ofSeconds(1));

            } catch (InterruptedException e){
                throw new RuntimeException(e);
            }
        };
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        //Executors.newSingleThreadExecutor()
        //Executors.newVirtualThreadPerTaskExecutor()
        try(ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()){
            for (int i = 0; i < 1000; i++) {
                var process = new Processor().taskSingleThread(i);
                executor.submit(process);
            }
        }

        long endTime = System.currentTimeMillis();
         long timeElapsed = endTime - startTime;
        System.out.println(String.format("Time elapsed, %s",Duration.ofMillis(timeElapsed).toSeconds()));
    }
}
