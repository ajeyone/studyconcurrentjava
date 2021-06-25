package wait.exp1;

import java.util.Date;

import input.terminal.SimpleMenu;

/**
 * Experiment1 先调用了 Thread.sleep() 再调用了 notify()，使得 wait() 全部被调用了之后，才进入的
 * notify() 所在的 synchronized 块。由于 wait() 与 notify() 的调用数量相同，所以每一个 notify()
 * 都会唤醒一个 wait()，程序能正常结束。
 * 
 * 而 Experiment2 是在 synchronized 块中调用的 Thread.sleep()，导致这个 synchronized 块立刻与
 * WaitThread 中的 synchronized 块竞争锁。如果被主线程的这个 synchronized 块先得到锁，那么就会发生 notify()
 * 调用于 wait() 之前，导致有 wait() 没有被唤醒，最终程序不会退出。
 */
public class WaitExperiment {
    private static Experiment selectExperiment() {
        SimpleMenu menu = new SimpleMenu(new String[] { "Sleep outside synchronized", "Sleep inside synchronized" },
                "Select an experiment");
        int index = menu.selectWithRetryCount(3);
        switch (index) {
        case 0:
            return new Experiment1();
        case 1:
            return new Experiment2();
        default:
            return null;
        }
    }

    public static void main(String[] args) {
        Experiment experiment = selectExperiment();
        if (experiment == null) {
            return;
        }
        experiment.execute();
    }
}

class Experiment1 extends Experiment {
    public void execute() {
        final Object monitor = new Object();
        for (int i = 0; i < NCPU; i++) {
            new WaitThread(monitor, "" + i).start();
        }
        System.out.println("threads started");

        for (int i = 0; i < NCPU; i++) {
            unsafeSleep(1000);
            synchronized (monitor) {
                System.out.println("notify");
                monitor.notify();
            }
        }
    }
}

class Experiment2 extends Experiment {
    public void execute() {
        final Object monitor = new Object();
        for (int i = 0; i < NCPU; i++) {
            new WaitThread(monitor, "" + i).start();
        }
        System.out.println("threads started");

        for (int i = 0; i < NCPU; i++) {
            synchronized (monitor) {
                unsafeSleep(1000);
                System.out.println("notify");
                monitor.notify();
            }
        }
    }
}

abstract class Experiment {
    static final int NCPU = Runtime.getRuntime().availableProcessors();

    protected void unsafeSleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    abstract public void execute();
}

class WaitThread extends Thread {
    private final Object lock;

    public WaitThread(Object lock, String name) {
        super(name);
        this.lock = lock;
    }

    @Override
    public void run() {
        synchronized (lock) {
            long startTimestamp = new Date().getTime();
            System.out.println("[" + getName() + "]: invoke wait");
            try {
                lock.wait();
                System.out.println("[" + getName() + "]: wait end, takes: " + (new Date().getTime() - startTimestamp));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}