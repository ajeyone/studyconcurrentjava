package wait.exp2;

import input.terminal.SimpleMenu;

/**
 * wait() can be interrupted
 */
public class WaitExperiment {
    public static void main(String[] args) {
        final Object monitor = new Object();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (monitor) {
                    try {
                        System.out.println("wait");
                        monitor.wait();
                        System.out.println("wait has been notified");
                    } catch (InterruptedException e) {
                        System.out.println("wait has been interrupted");
                    }
                }
            }
        });
        thread.start();
        
        SimpleMenu menu = new SimpleMenu(new String[] { "interrupt the thread", "notify the thread" },
                "select a method to stop the thread");
        int index = menu.selectWithRetryCount(3);
        if (index == 0) {
            thread.interrupt();
        } else {
            synchronized (monitor) {
                monitor.notify();
            }
        }
    }
}
