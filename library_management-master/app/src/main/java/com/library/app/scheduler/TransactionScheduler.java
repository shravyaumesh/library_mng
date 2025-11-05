package com.library.app.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TransactionScheduler {
    @Scheduled(fixedRate = 60000)
    public void run() {
        System.out.println("45855585");
    }
}
