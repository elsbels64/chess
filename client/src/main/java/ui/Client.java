package ui;

import java.util.concurrent.atomic.AtomicReference;

public interface Client {
    State getState();
    String help();
    String eval(String line);
}
