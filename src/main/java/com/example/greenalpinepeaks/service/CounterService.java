package com.example.greenalpinepeaks.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CounterService {

    private static final Logger LOG = LoggerFactory.getLogger(CounterService.class);

    private int unsafeCounter = 0;
    private int synchronizedCounter = 0;
    private final AtomicInteger atomicCounter = new AtomicInteger(0);

    public int incrementUnsafe() {
        unsafeCounter++;
        return unsafeCounter;
    }

    public synchronized int incrementSynchronized() {
        synchronizedCounter++;
        return synchronizedCounter;
    }

    public int incrementAtomic() {
        return atomicCounter.incrementAndGet();
    }

    public int getUnsafeCounter() {
        return unsafeCounter;
    }

    public int getSynchronizedCounter() {
        return synchronizedCounter;
    }

    public int getAtomicCounter() {
        return atomicCounter.get();
    }

    public void resetCounters() {
        unsafeCounter = 0;
        synchronizedCounter = 0;
        atomicCounter.set(0);
        LOG.info("Все счётчики сброшены");
    }
}