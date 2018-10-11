package factor.exp2;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import algo.prime.FactorCalculator;

public class Factor {
    private static final int CALCULATION_COUNT_PER_THREAD = 50000;

    public static void main(String[] args) {
        int n = Runtime.getRuntime().availableProcessors();
        Factorizer ucf = new UnsafeCachingFactorizer1();
        Thread[] threads = new Thread[n];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread() {
                public void run() {
                    for (int j = 0; j < CALCULATION_COUNT_PER_THREAD; j++) {
                        int number = new Random().nextInt(Factorizer.MAX_PRIME_NUMBER);
                        int[] factors = ucf.calculateFactor(number);
                        assertFactors(number, factors);
                    }
                }
            };
            threads[i].start();
        }

        try {
            for (int i = 0; i < threads.length; i++) {
                threads[i].join();
            }
        } catch (Throwable e) {
        }
    }

    private static void assertFactors(int n, int[] factors) {
        int p = 1;
        for (int f : factors) {
            p *= f;
        }
        if (p != n) {
            StringBuilder sb = new StringBuilder();
            sb.append("Wrong factors: ").append(n).append(" != ");
            for (int i = 0; i < factors.length; i++) {
                sb.append(factors[i]);
                if (i < factors.length - 1) {
                    sb.append(" * ");
                }
            }
            sb.append(" = ").append(p);
            System.out.println(sb.toString());
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
}

class UnsafeCachingFactorizer1 extends Factorizer {
    private final AtomicReference<Integer> lastNumber = new AtomicReference<Integer>();
    private final AtomicReference<int[]> lastFactors = new AtomicReference<int[]>();

    public int[] calculateFactor(int n) {
        Integer last = lastNumber.get();
        if (last != null && last == n) {
            return lastFactors.get();
        } else {
            int[] factors = factor(n);
            lastNumber.set(n);
            lastFactors.set(factors);
            return factors;
        }
    }
}

class UnsafeCachingFactorizer2 extends Factorizer {
    private long lastNumber = -1;
    private int[] lastFactors;

    public int[] calculateFactor(int n) {
        if (lastNumber == n) {
            return lastFactors;
        }
        int[] factors = factor(n);
        lastNumber = n;
        lastFactors = factors;
        return factors;
    }
}

class SafeCachingFactorizer1 extends Factorizer {
    private long lastNumber = -1;
    private int[] lastFactors;

    public int[] calculateFactor(int n) {
        int[] factors = null;
        synchronized (this) {
            if (lastNumber == n) {
                factors = lastFactors;
            }
        }
        if (factors == null) {
            factors = factor(n);
            synchronized (this) {
                lastNumber = n;
                lastFactors = factors;
            }
        }
        return factors;
    }
}

class SafeCachingFactorizer2 extends Factorizer {
    private long lastNumber = -1;
    private int[] lastFactors;

    public synchronized int[] calculateFactor(int n) {
        if (lastNumber == n) {
            return lastFactors;
        }
        int[] factors = factor(n);
        lastNumber = n;
        lastFactors = factors;
        return factors;
    }
}
