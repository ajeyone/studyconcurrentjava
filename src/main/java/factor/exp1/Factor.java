package factor.exp1;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import algo.prime.FactorCalculator;
import input.terminal.SimpleMenu;

public class Factor {
    private static final int CALCULATION_COUNT_PER_THREAD = 50000;

    private static Factorizer selectFactorizer() {
        SimpleMenu menu = new SimpleMenu(new String[] { "Unsafe factorizer without any synchronization",
                "Safe factorizer with AtomicLong", "Safe factorizer with synchronized block" }, "Select a factorizer");

        int index = menu.selectWithRetryCount(3);
        switch (index) {
        case 0:
            return new UnsafeCountingFactorizer();
        case 1:
            return new SafeCountingFactorizer1();
        case 2:
            return new SafeCountingFactorizer2();
        default:
            return null;
        }
    }

    public static void main(String[] args) {
        int n = Runtime.getRuntime().availableProcessors();
        Factorizer factorizer = selectFactorizer();
        if (factorizer == null) {
            return;
        }
        Thread[] threads = new Thread[n];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread() {
                public void run() {
                    for (int j = 0; j < CALCULATION_COUNT_PER_THREAD; j++) {
                        int number = new Random().nextInt(Factorizer.MAX_PRIME_NUMBER);
                        factorizer.calculateFactor(number);
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
            System.out.println("final count=" + factorizer.getCount() + ", supposed to be " + expectedCount);
            String message = factorizer.getCount() == expectedCount ? "Correct, it is thread safe."
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