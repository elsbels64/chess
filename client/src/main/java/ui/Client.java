package ui;

public interface Client {
    State getState();
    String help();
    String eval(String line);
}
