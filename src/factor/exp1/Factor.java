package factor.exp1;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import algo.prime.FactorCalculator;

public class Factor {
    private static final int CALCULATION_COUNT_PER_THREAD = 50000;

    public static void main(String[] args) {
        int n = Runtime.getRuntime().availableProcessors();
        Factorizer ucf = new SafeCountingFactorizer1();
        Thread[] threads = new Thread[n];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread() {
                public void run() {
                    for (int j = 0; j < CALCULATION_COUNT_PER_THREAD; j++) {
                        int number = new Random().nextInt(Factorizer.MAX_PRIME_NUMBER);
                        ucf.calculateFactor(number);
                    }
                }
            };
            threads[i].start();
        }

        try {
            for (int i = 0; i < threads.length; i++) {
                threads[i].join();
            }

            int expectedCount = threads.length * CALCULATION_COUNT_PER_THREAD;
            System.out.println("final count=" + ucf.getCount() + ", supposed to be " + expectedCount);
            String message = ucf.getCount() == expectedCount ? "Correct, it is thread safe."
                    : "Wrong, it is not thread safe.";
            System.out.println(message);
        } catch (Throwable e) {
        }
    }
}

abstract class Factorizer {
    public static final int MAX_PRIME_NUMBER = 1000;

    private FactorCalculator calculator = new FactorCalculator(MAX_PRIME_NUMBER);

    protected int[] factor(int n) {
        return calculator.factor(n);
    }

    abstract public int[] calculateFactor(int n);

    abstract public long getCount();
}

class UnsafeCountingFactorizer extends Factorizer {
    private long count = 0;

    public long getCount() {
        return count;
    }

    public int[] calculateFactor(int n) {
        int[] result = factor(n);
        count++;
        return result;
    }
}

class SafeCountingFactorizer1 extends Factorizer {
    private AtomicLong count = new AtomicLong(0);

    public long getCount() {
        return count.longValue();
    }

    public int[] calculateFactor(int n) {
        int[] result = factor(n);
        count.incrementAndGet();
        return result;
    }
}

class SafeCountingFactorizer2 extends Factorizer {
    private long count = 0;

    public long getCount() {
        return count;
    }

    public int[] calculateFactor(int n) {
        int[] result = factor(n);
        synchronized (this) {
            count++;
        }
        return result;
    }
}