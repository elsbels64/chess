import chess.*;
import server.Server;
import serverFacade.ServerFacade;

import ui.Repl;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        Server server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        ServerFacade serverFacade = new ServerFacade("http://localhost:" + port);
        Repl repl = new Repl("http://localhost:" + port);
        System.out.println("Running Repl:");
        repl.run();
    }
}