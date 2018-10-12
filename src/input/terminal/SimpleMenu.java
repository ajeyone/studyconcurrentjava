package input.terminal;

import java.util.Scanner;

public class SimpleMenu {
    private static final String DEFAULT_INPUT_HINT = "input a menu index:";
    private static final String DEFAULT_ERROR_HINT = "invalid menu index";

    private final String[] items;
    private final String title;

    private String inputHint = DEFAULT_INPUT_HINT;
    private String errorHint = DEFAULT_ERROR_HINT;

    public void setInputHint(String inputHint) {
        this.inputHint = inputHint;
    }

    public void setErrorHint(String errorHint) {
        this.errorHint = errorHint;
    }

    public SimpleMenu(String[] items) {
        this(items, null);
    }

    public SimpleMenu(String[] items, String title) {
        this.items = items;
        this.title = title;
    }

    public int selectWithRetryCount(int retryTime) {
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
