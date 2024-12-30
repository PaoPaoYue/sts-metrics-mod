package com.github.paopaoyue.metrics_local.utility;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Async {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void run(Runnable runnable) {
        executor.submit(runnable);
    }

    public static void shutdown() {
        executor.shutdown();
    }
}
