package input.terminal;

import java.util.Scanner;

public class SimpleMenu {
    private final String[] items;
    private final String title;

    public SimpleMenu(String[] items) {
        this(items, null);
    }

    public SimpleMenu(String[] items, String title) {
        this.items = items;
        this.title = title;
    }
    
    public int select(String inputHint, String errorHint, int retryTime) {
        int id = -1;
        int i = 0;
        while ((retryTime <= 0 || i < retryTime) && (id = show(inputHint, errorHint)) < 0) {
            i++;
        }
        return id;
    }

    private int show(String inputHint, String errorHint) {
        if (title != null) {
            System.out.println(title);
        }
        if (items != null) {
            for (int i = 0; i < items.length; i++) {
                System.out.println(i + " - " + items[i]);
            }
        }
        System.out.println(inputHint);

        int id = handleInput();
        if (id < 0) {
            System.out.println(errorHint);
        }
        return id;
    }

    private int handleInput() {
        int id = -1;
        try {
            String input = new Scanner(System.in).next();
            int index = Integer.parseInt(input);
            if (index >= 0 && index < items.length) {
                id = index;
            }
        } catch (Exception e) {
        }
        return id;
    }
}
