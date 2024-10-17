package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static chess.ChessGame.TeamColor;
import static chess.ChessPiece.PieceType;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] chessBoard;
    Collection<ChessPosition> blackPositions;
    Collection<ChessPosition> whitePositions;
    ChessPosition blackKing;
    ChessPosition whiteKing;

    public ChessBoard() {
        chessBoard = new ChessPiece[8][8];
    }

    public ChessBoard(ChessBoard other) {
        // Initialize a new chess board array
        this.chessBoard = new ChessPiece[8][8];

        // Copy each piece from the original board to the new board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (other.chessBoard[i][j] != null) {
                    ChessPiece originalPiece = other.chessBoard[i][j];
                    this.chessBoard[i][j] = new ChessPiece(originalPiece.getTeamColor(), originalPiece.getPieceType());
                }
            }
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        chessBoard[(position.getColumn()) - 1][(position.getRow()) - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        if (position.getRow() <= 0 || position.getRow() > 8 || position.getColumn() <= 0 || position.getColumn() > 8) {
            throw new IndexOutOfBoundsException("Index out of bounds: cannot access: " + (position.getRow()) + ", " + (position.getColumn()));
        }
        return chessBoard[(position.getColumn()) - 1][(position.getRow()) - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        chessBoard = new ChessPiece[8][8];
        resetPawns(1, ChessGame.TeamColor.WHITE);
        resetNonPawns(0, ChessGame.TeamColor.WHITE);
        resetPawns(6, ChessGame.TeamColor.BLACK);
        resetNonPawns(7, ChessGame.TeamColor.BLACK);
    }

    public void resetPawns(int row, ChessGame.TeamColor color) {
        for (int i = 0; i < 8; i++) {
            chessBoard[i][row] = new ChessPiece(color, ChessPiece.PieceType.PAWN);
        }
    }

    public void resetNonPawns(int row, ChessGame.TeamColor color) {
        chessBoard[0][row] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
        chessBoard[1][row] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
        chessBoard[2][row] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
        chessBoard[3][row] = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
        chessBoard[4][row] = new ChessPiece(color, ChessPiece.PieceType.KING);
        chessBoard[5][row] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
        chessBoard[6][row] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
        chessBoard[7][row] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
    }

    public void updateColorPositionsAndKings() {
        blackPositions = new ArrayList<>();
        whitePositions = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (chessBoard[i][j] != null) {
                    processPiece(chessBoard[i][j], i, j);
                }
            }
        }
    }

    private void processPiece(ChessPiece piece, int i, int j) {
        ChessPosition position = new ChessPosition(j + 1, i + 1);

        if (piece.getTeamColor() == TeamColor.WHITE) {
            whitePositions.add(position);
            if (piece.getPieceType() == PieceType.KING) {
                whiteKing = position;
            }
        } else {
            blackPositions.add(position);
            if (piece.getPieceType() == PieceType.KING) {
                blackKing = position;
            }
        }
    }

    final static Map<PieceType, Character> TYPE_TO_CHAR_MAP = Map.of(
            ChessPiece.PieceType.PAWN, 'p',
            ChessPiece.PieceType.KNIGHT, 'n',
            ChessPiece.PieceType.ROOK, 'r',
            ChessPiece.PieceType.QUEEN, 'q',
            ChessPiece.PieceType.KING, 'k',
            ChessPiece.PieceType.BISHOP, 'b');


    @Override
    public String toString() {
        StringBuilder boardString = new StringBuilder();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                if (getPiece(new ChessPosition(row, col)) == null) {
                    boardString.append(" ");
                } else {
                    Character piece = getPiece(new ChessPosition(row, col)).getTeamColor() == ChessGame.TeamColor.WHITE ? Character.toUpperCase(TYPE_TO_CHAR_MAP.get(((getPiece(new ChessPosition(row, col))).getPieceType()))) :
                            Character.toLowerCase(TYPE_TO_CHAR_MAP.get(((getPiece(new ChessPosition(row, col))).getPieceType())));
                    boardString.append(piece);
                }
            }
            boardString.append("\n");
        }
        return String.valueOf(boardString);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(this.chessBoard, that.chessBoard);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(chessBoard);
    }
}