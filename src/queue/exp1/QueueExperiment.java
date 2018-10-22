package queue.exp1;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class QueueExperiment {
    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);
        new Thread(new Consumer(queue)).start();
        new Thread(new Producer(queue)).start();
    }
}

class Consumer implements Runnable {
    private static final Random random = new Random();
    private final BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (queue.isEmpty()) {
                    System.out.println("consume: nothing to consume, wait");
                }
                Integer value = queue.take();
                System.out.println("consume: " + value);
                Thread.sleep(random.nextInt(500) + 5000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Producer implements Runnable {
    private static final Random random = new Random();
    private final BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Integer value = random.nextInt(9000) + 1000;
                System.out.println("produce: " + value);
                if (!queue.offer(value)) {
                    System.out.println("produce: " + value + ", but queue has no space to store it, wait");
                    queue.put(value);
                }
                Thread.sleep(random.nextInt(900) + 100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}