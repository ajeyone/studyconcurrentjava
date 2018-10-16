package container.exp1;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import input.terminal.SimpleMenu;

/**
 * Select one of the 2 Lists and one of the 3 ListOperations to run the
 * experiment. The only safe combination is ("Synchronized List", "Operation
 * synchronized with list")
 */
public class ContainerExperiments {
    private static List<Integer> selectList() {
        SimpleMenu menu = new SimpleMenu(new String[] { "Tread-unsafe List", "Synchronized List" }, "Select a List");
        int index = menu.selectWithRetryCount(3);
        switch (index) {
        case 0:
            return new LinkedList<>();
        case 1:
            return Collections.synchronizedList(new LinkedList<>());
        default:
            return null;
        }
    }

    private static ListOperation<Integer> selectOperation() {
        SimpleMenu menu = new SimpleMenu(
                new String[] { "Operation without synchronization", "Operation synchronized with list", "Operation synchronized with operation", },
                "Select an Operation");
        int index = menu.selectWithRetryCount(3);
        switch (index) {
        case 0:
            return new ListOperationNoSynchronization<Integer>();
        case 1:
            return new ListOperationSynchronizedWithList<Integer>();
        case 2:
            return new ListOperationSynchronizedWithOperation<Integer>();
        default:
            return null;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        List<Integer> list = selectList();
        ListOperation<Integer> operation = selectOperation();
        ContainerExperiment experiment = new ContainerExperiment();
        experiment.execute(list, operation);
    }
}

class ContainerExperiment {
    public final void execute(List<Integer> list, ListOperation<Integer> operation) throws InterruptedException {
        if (list == null || operation == null) {
            return;
        }
        resetList(list);
        Thread thread = new ContainerReadThread(list, operation);
        thread.start();
        while (operation.deleteLast(list) != null) {
            list.remove(0);
            if (thread.isAlive()) {
                Thread.sleep(1);
            } else {
                break;
            }
        }
        thread.interrupt();
    }

    private static Random random = new Random();

    private void resetList(List<Integer> list) {
        list.clear();
        for (int i = 0; i < 500; i++) {
            list.add(random.nextInt(9000) + 1000);
        }
    }
}

class ContainerReadThread extends Thread {
    private final List<Integer> list;
    private final ListOperation<Integer> operation;

    public ContainerReadThread(List<Integer> list, ListOperation<Integer> operation) {
        this.list = list;
        this.operation = operation;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                Integer value = operation.getLast(list);
                System.out.println(value);
                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

interface ListOperation<T> {
    public T getLast(List<T> list);

    public T deleteLast(List<T> list);
}

class ListOperationNoSynchronization<T> implements ListOperation<T> {
    @Override
    public T getLast(List<T> list) {
        int size = list.size();
        return size > 0 ? list.get(size - 1) : null;
    }

    @Override
    public T deleteLast(List<T> list) {
        int size = list.size();
        return size > 0 ? list.remove(size - 1) : null;
    }
}

class ListOperationSynchronizedWithList<T> implements ListOperation<T> {
    @Override
    public T getLast(List<T> list) {
        synchronized (list) {
            int size = list.size();
            return size > 0 ? list.get(size - 1) : null;
        }
    }

    @Override
    public T deleteLast(List<T> list) {
        synchronized (list) {
            int size = list.size();
            return size > 0 ? list.remove(size - 1) : null;
        }
    }
}

class ListOperationSynchronizedWithOperation<T> implements ListOperation<T> {
    @Override
    public T getLast(List<T> list) {
        synchronized (this) {
            int size = list.size();
            return size > 0 ? list.get(size - 1) : null;
        }
    }

    @Override
    public T deleteLast(List<T> list) {
        synchronized (this) {
            int size = list.size();
            return size > 0 ? list.remove(size - 1) : null;
        }
    }
}
