import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import ui.DisplayBoard;
import ui.Repl;

public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client: ");
        Repl repl = new Repl("http://localhost:" + 8080);
        System.out.println("Running Repl:");
        repl.run();
    }
}