package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DisplayBoard {
    final static Map<ChessPiece.PieceType, String> TYPE_TO_CHAR_MAP = Map.of(
            ChessPiece.PieceType.PAWN, " P ",
            ChessPiece.PieceType.KNIGHT, " N ",
            ChessPiece.PieceType.BISHOP, " B ",
            ChessPiece.PieceType.ROOK, " R ",
            ChessPiece.PieceType.QUEEN," Q ",
            ChessPiece.PieceType.KING, " K "
    );

    private void setSpace(ChessBoard board, StringBuilder boardString, int row, int col) {
        List<String> backgroundColors = new ArrayList<>();
        backgroundColors.add(EscapeSequences.SET_BG_COLOR_LIGHT_BROWN);
        backgroundColors.add(EscapeSequences.SET_BG_COLOR_BROWN);
        boardString.append(backgroundColors.get((row+col)%2));
        if (board.getPiece(new ChessPosition(row, col)) == null) {
            boardString.append("   ");
        } else {
            String white = EscapeSequences.SET_TEXT_COLOR_WHITE;
            String black = EscapeSequences.SET_TEXT_COLOR_BLACK;
            String color = board.getPiece(new ChessPosition(row, col)).getTeamColor() == ChessGame.TeamColor.WHITE
                    ? white
                    : black;
            boardString.append(color);
            boardString.append(TYPE_TO_CHAR_MAP.get(board.getPiece(new ChessPosition(row, col)).getPieceType()));
        }
    }

    public String printGameBlack(ChessBoard board){
        StringBuilder boardString = new StringBuilder();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                setSpace(board, boardString, row, col);
            }
            boardString.append(EscapeSequences.RESET_BG_COLOR);
            boardString.append("\n");
        }
        return String.valueOf(boardString);
    }

    public String printGameWhite(ChessBoard board){
        StringBuilder boardString = new StringBuilder();
        for (int row = 8; row >= 1; row--) {
            for (int col = 8; col >= 1; col--) {
                setSpace(board, boardString, row, col);
            }
            boardString.append(EscapeSequences.RESET_BG_COLOR);
            boardString.append("\n");
        }
        return String.valueOf(boardString);
    }
}
