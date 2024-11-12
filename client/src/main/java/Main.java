import chess.*;
import ui.Repl;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
//        server = new Server();
//        var port = server.run(8080);
//        System.out.println("Started test HTTP server on " + port);
        Repl repl = new Repl("localhost:8080");
        System.out.println("Running Repl:");
        repl.run();
    }
}