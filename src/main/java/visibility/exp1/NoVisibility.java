package visibility.exp1;

import input.terminal.SimpleMenu;

public class NoVisibility {
    private static NoVisibilityExperiment selectExperiment() {
        SimpleMenu menu = new SimpleMenu(new String[] {
                "Experiment with no volatile key word",
                "Experiment with volatile key word",
                "Experiment with 2 variants",
                "Experiment with 2 variants, change the read order",
        }, "Select a NoVisibility Experiment");
        
        int index = menu.selectWithRetryCount(3);
        switch (index) {
        case 0:
            return new NoVisibilityExperimentNoVolatile();
        case 1:
            return new NoVisibilityExperimentWithVolatile();
        case 2:
            return new NoVisibilityExperiment3();
        case 3:
            return new NoVisibilityExperiment4();
        default:
            return null;
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        NoVisibilityExperiment experiment = selectExperiment();
        experiment.execute();
    }
}

interface NoVisibilityExperiment {
    void execute() throws InterruptedException;
}

/**
 * This may run forever because the result of write operation is not visible to
 * the reader thread.
 */
class NoVisibilityExperimentNoVolatile implements NoVisibilityExperiment {
    private static boolean ready;

    @Override
    public void execute() throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (ready) {
                        System.out.println("Reader Thread - Flag change received. Finishing thread.");
                        break;
                    }
                }
            }
        }).start();

        Thread.sleep(100);
        System.out.println("Writer thread - Changing flag...");
        ready = true;
    }
}

/**
 * This will terminate as expected because the volatile key word guarantee the
 * visibility.
 */
class NoVisibilityExperimentWithVolatile implements NoVisibilityExperiment {
    private static volatile boolean ready;

    @Override
    public void execute() throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (ready) {
                        System.out.println("Reader Thread - Flag change received. Finishing thread.");
                        break;
                    }
                }
            }
        }).start();

        Thread.sleep(100);
        System.out.println("Writer thread - Changing flag...");
        ready = true;
    }
}

/**
 * This terminated all the time but I don't known the reason. An assumption is
 * made that the "number" becomes visible after the reading of visible "ready".
 */
class NoVisibilityExperiment3 implements NoVisibilityExperiment {
    private static volatile boolean ready;
    private static int number;

    @Override
    public void execute() throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (ready && number == 42) { // notice the order of the 2 conditions
                        System.out.println("Reader Thread - Flag change received. Finishing thread.");
                        break;
                    }
                }
            }
        }).start();

        Thread.sleep(100);
        System.out.println("Writer thread - Changing flag...");
        ready = true;
        number = 42;
    }
}

/**
 * According to the assumption in NoVisibilityExperiment3, the order of reading
 * "number" and "ready" is changed in this experiment. As a result, it
 * terminates as expected to support the assumption.
 */
class NoVisibilityExperiment4 implements NoVisibilityExperiment {
    private static volatile boolean ready;
    private static int number;

    @Override
    public void execute() throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (number == 42 && ready) { // NOTICE the order of the 2 conditions
                        System.out.println("Reader Thread - Flag change received. Finishing thread.");
                        break;
                    }
                }
            }
        }).start();

        Thread.sleep(100);
        System.out.println("Writer thread - Changing flag...");
        ready = true;
        number = 42;
    }
}
