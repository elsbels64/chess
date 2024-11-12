package ui;

public interface Client {
    State state = null;
    String help();
    String eval(String line);
}
