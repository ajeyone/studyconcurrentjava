package interrupt.exp1;

import java.util.Random;

import algo.prime.FactorCalculator;

public class StopWork {
    private static final int MAX_PRIME_NUMBER = 1000;
    private static FactorCalculator calculator = new FactorCalculator(MAX_PRIME_NUMBER);

    public static void main(String[] args) {
        // Thread workThread = new EndlessWorkThread("endless", calculator);
        // Thread workThread = new NonBlockWorkThread("nonblock", calculator);
        // Thread workThread = new BlockWorkThread("block", calculator);
        Thread workThread = new EndlessBlockWorkThread("endless", calculator);
        workThread.start();
        try {
            Thread.sleep(1000);
            workThread.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("main thread ended");
    }
}

class WorkThread extends Thread {
    protected final FactorCalculator calculator;
    protected final Random random;

    public WorkThread(String name, FactorCalculator calculator) {
        super(name);
        this.calculator = calculator;
        this.random = new Random();
    }
}

/**
 * This thread will run forever because method interrupt() does not really stop
 * the thread but sets a flag. The thread itself uses the flag to determine when
 * to stop.
 */
class EndlessWorkThread extends WorkThread {
    public EndlessWorkThread(String name, FactorCalculator calculator) {
        super(name, calculator);
    }

    @Override
    public void run() {
        int number;
        int[] factors;
        int i = 0;
        while (true) {
            number = random.nextInt(calculator.getMaxPrimeNumber());
            factors = calculator.factor(number);
            System.out.println(getName() + ":[" + i + "]:" + FactorCalculator.buildFactorFormula(number, factors));
            System.out.println(getName() + ":[" + i + "]:" + "interrupted flag: " + isInterrupted());
            i++;
        }
    }
}

/**
 * This thread will stop after interrupt() invoked, isInterrupted() is used to
 * check the flag and stop. There is no block operation.
 */
class NonBlockWorkThread extends WorkThread {
    public NonBlockWorkThread(String name, FactorCalculator calculator) {
        super(name, calculator);
    }

    @Override
    public void run() {
        int number;
        int[] factors;
        int i = 0;
        while (!isInterrupted()) {
            number = random.nextInt(calculator.getMaxPrimeNumber());
            factors = calculator.factor(number);
            System.out.println(getName() + ":[" + i + "]:" + FactorCalculator.buildFactorFormula(number, factors));
            System.out.println(getName() + ":[" + i + "]:" + "interrupted flag: " + isInterrupted());
            i++;
        }
    }
}

/**
 * This thread uses Thread.sleep() to block itself. An InterruptedException will
 * be received in the thread if interrupt() is invoked when it is in block
 * state.
 */
class BlockWorkThread extends WorkThread {
    public BlockWorkThread(String name, FactorCalculator calculator) {
        super(name, calculator);
    }

    @Override
    public void run() {
        int number;
        int[] factors;
        int i = 0;
        try {
            while (!isInterrupted()) {
                number = random.nextInt(calculator.getMaxPrimeNumber());
                factors = calculator.factor(number);
                System.out.println(getName() + ":[" + i + "]:" + FactorCalculator.buildFactorFormula(number, factors));
                System.out.println(getName() + ":[" + i + "]:" + "interrupted flag: " + isInterrupted());
                i++;
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            System.out.println(getName() + ":InterruptedException:" + "interrupted flag: " + isInterrupted());
        }
    }
}

/**
 * This thread will run forever because the interrupt flag will be clear when
 * received an InterruptedException. So isInterrupted() still return false and
 * cannot be used to stop the loop.
 */
class EndlessBlockWorkThread extends WorkThread {
    public EndlessBlockWorkThread(String name, FactorCalculator calculator) {
        super(name, calculator);
    }

    @Override
    public void run() {
        int number;
        int[] factors;
        int i = 0;
        while (!isInterrupted()) {
            try {
                number = random.nextInt(calculator.getMaxPrimeNumber());
                factors = calculator.factor(number);
                System.out.println(getName() + ":[" + i + "]:" + FactorCalculator.buildFactorFormula(number, factors));
                System.out.println(getName() + ":[" + i + "]:" + "interrupted flag: " + isInterrupted());
                i++;
                Thread.sleep(200);
            } catch (InterruptedException e) {
                System.out.println(getName() + ":InterruptedException:" + "interrupted flag: " + isInterrupted());
            }
        }
    }
}