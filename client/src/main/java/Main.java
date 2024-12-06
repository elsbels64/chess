import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import ui.DisplayBoard;
import ui.Repl;

public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client: ");
        Repl repl = new Repl("http://localhost:" + 8080);
        ChessGame chessGame = new ChessGame();
        var validMoves = chessGame.validMoves(new ChessPosition(2,1));
        System.out.print(new DisplayBoard().printValidMovesWhite(chessGame.getBoard(),validMoves));
        System.out.println("Running Repl:");
        repl.run();
    }
}