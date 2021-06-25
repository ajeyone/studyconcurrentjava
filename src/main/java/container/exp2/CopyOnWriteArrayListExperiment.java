package container.exp2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import input.terminal.SimpleMenu;

public class CopyOnWriteArrayListExperiment {
    private static List<Integer> selectList() {
        SimpleMenu menu = new SimpleMenu(new String[] { "ArrayList", "CopyOnWriteArrayList" }, "Select a list");

        int index = menu.selectWithRetryCount(3);
        switch (index) {
        case 0:
            return new ArrayList<>();
        case 1:
            return new CopyOnWriteArrayList<>();
        default:
            return null;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        List<Integer> list = selectList();
        Experiment experiment = new Experiment();
        experiment.execute(list);
    }
}

class Experiment {
    private static Random random = new Random();

    public void execute(List<Integer> list) throws InterruptedException {
        Thread thread = new ReadThread(list);
        thread.start();
        int value;
        for (int i = 0; i < 100; i++) {
            value = random.nextInt(9000) + 1000;
            list.add(value);
            Thread.sleep(10);
        }
        thread.interrupt();
    }
}

class ReadThread extends Thread {
    private final List<Integer> list;

    public ReadThread(List<Integer> list) {
        this.list = list;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            System.out.println(listToString());
        }
    }

    private String listToString() {
        StringBuilder sb = new StringBuilder();
        for (Integer value : list) {
            sb.append(value);
            sb.append(" ");
        }
        return sb.toString();
    }
}