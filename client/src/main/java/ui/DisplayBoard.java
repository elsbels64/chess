package ui;

import chess.*;

import java.util.*;

public class DisplayBoard {
    final static Map<ChessPiece.PieceType, String> TYPE_TO_CHAR_MAP = Map.of(
            ChessPiece.PieceType.PAWN, " P ",
            ChessPiece.PieceType.KNIGHT, " N ",
            ChessPiece.PieceType.BISHOP, " B ",
            ChessPiece.PieceType.ROOK, " R ",
            ChessPiece.PieceType.QUEEN," Q ",
            ChessPiece.PieceType.KING, " K "
    );

    public String printGameBlack(ChessBoard board){
        StringBuilder boardString = new StringBuilder();
        boardString.append("\n");
        boardString.append(EscapeSequences.SET_BG_COLOR_WHITE);
        boardString.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        boardString.append("    h  g  f  e  d  c  b  a    ")
                .append(EscapeSequences.RESET_BG_COLOR)
                .append("\n");

        for (int row = 1; row <= 8; row++) {
            boardString.append(EscapeSequences.SET_BG_COLOR_WHITE);
            boardString.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
            boardString.append(" ").append(row).append(" ");
            for (int col = 8; col >= 1; col--) {
                setSpacePlusValidMoveCheck(board, boardString, row, col, null);
            }
            boardString.append(EscapeSequences.SET_BG_COLOR_WHITE);
            boardString.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
            boardString.append(" ").append(row).append(" ");
            boardString.append(EscapeSequences.RESET_BG_COLOR);
            boardString.append("\n");
        }

        boardString.append(EscapeSequences.SET_BG_COLOR_WHITE);
        boardString.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        boardString.append("    h  g  f  e  d  c  b  a    ")
                .append(EscapeSequences.RESET_BG_COLOR)
                .append("\n");
        return String.valueOf(boardString);
    }

    public String printGameWhite(ChessBoard board){
        StringBuilder boardString = new StringBuilder();
        boardString.append("\n");
        boardString.append(EscapeSequences.SET_BG_COLOR_WHITE);
        boardString.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        boardString.append("    a  b  c  d  e  f  g  h    ")
                .append(EscapeSequences.RESET_BG_COLOR)
                .append("\n");

        for (int row = 8; row >= 1; row--) {
            boardString.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
            boardString.append(EscapeSequences.SET_BG_COLOR_WHITE);
            boardString.append(" ").append(row).append(" ");
            for (int col = 1; col <= 8; col++) {
                setSpacePlusValidMoveCheck(board, boardString, row, col, null);
            }
            boardString.append(EscapeSequences.SET_BG_COLOR_WHITE);
            boardString.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
            boardString.append(" ").append(row).append(" ");
            boardString.append(EscapeSequences.RESET_BG_COLOR);
            boardString.append("\n");
        }

        boardString.append(EscapeSequences.SET_BG_COLOR_WHITE);
        boardString.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        boardString.append("    a  b  c  d  e  f  g  h    ")
                .append(EscapeSequences.RESET_BG_COLOR)
                .append("\n");
        return String.valueOf(boardString);
    }

    public String printValidMovesBlack(ChessBoard board, Collection<ChessMove> validMoves) {
        StringBuilder boardString = new StringBuilder();
        boardString.append("\n");
        boardString.append(EscapeSequences.SET_BG_COLOR_WHITE);
        boardString.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        boardString.append("    h  g  f  e  d  c  b  a    ")
                .append(EscapeSequences.RESET_BG_COLOR)
                .append("\n");
        for (int row = 1; row <= 8; row++) {
            boardString.append(EscapeSequences.SET_BG_COLOR_WHITE);
            boardString.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
            boardString.append(" ").append(row).append(" ");
            for (int col = 8; col >= 1; col--) {
                setSpacePlusValidMoveCheck(board, boardString, row, col, validMoves);
            }
            boardString.append(EscapeSequences.SET_BG_COLOR_WHITE);
            boardString.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
            boardString.append(" ").append(row).append(" ");
            boardString.append(EscapeSequences.RESET_BG_COLOR);
            boardString.append("\n");
        }

        boardString.append(EscapeSequences.SET_BG_COLOR_WHITE);
        boardString.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        boardString.append("    h  g  f  e  d  c  b  a    ")
                .append(EscapeSequences.RESET_BG_COLOR)
                .append("\n");
        return String.valueOf(boardString);
    }

    private void setSpacePlusValidMoveCheck(ChessBoard board, StringBuilder boardString, int row, int col, Collection<ChessMove> validMoves) {
        List<String> backgroundColors = new ArrayList<>();
        backgroundColors.add(EscapeSequences.SET_BG_COLOR_LIGHT_BROWN);
        backgroundColors.add(EscapeSequences.SET_BG_COLOR_BROWN);
        List<String> backgroundValidMoveColors = new ArrayList<>();
        backgroundValidMoveColors.add("\u001B[102m");
        backgroundValidMoveColors.add("\u001B[42m");
        boolean isHighlighted = false;
        if(validMoves!=null) {
            for (ChessMove move : validMoves) {
                if (move.getEndPosition().equals(new ChessPosition(row, col))) {
                    isHighlighted = true;
                    break;
                }
            }
        }
        if(isHighlighted) {
            boardString.append(backgroundValidMoveColors.get((row + col + 1) % 2));
        }else{
            boardString.append(backgroundColors.get((row + col + 1) % 2));
        }
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


    public String printValidMovesWhite(ChessBoard board, Collection<ChessMove> chessMoves) {
        StringBuilder boardString = new StringBuilder();
        boardString.append("\n");
        boardString.append(EscapeSequences.SET_BG_COLOR_WHITE);
        boardString.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        boardString.append("    a  b  c  d  e  f  g  h    ")
                .append(EscapeSequences.RESET_BG_COLOR)
                .append("\n");

        for (int row = 8; row >= 1; row--) {
            boardString.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
            boardString.append(EscapeSequences.SET_BG_COLOR_WHITE);
            boardString.append(" ").append(row).append(" ");
            for (int col = 1; col <= 8; col++) {
                setSpacePlusValidMoveCheck(board, boardString, row, col, chessMoves);
            }
            boardString.append(EscapeSequences.SET_BG_COLOR_WHITE);
            boardString.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
            boardString.append(" ").append(row).append(" ");
            boardString.append(EscapeSequences.RESET_BG_COLOR);
            boardString.append("\n");
        }

        boardString.append(EscapeSequences.SET_BG_COLOR_WHITE);
        boardString.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        boardString.append("    a  b  c  d  e  f  g  h    ")
                .append(EscapeSequences.RESET_BG_COLOR)
                .append("\n");
        return String.valueOf(boardString);
    }
}
