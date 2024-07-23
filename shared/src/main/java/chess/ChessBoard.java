package chess;

import java.util.Arrays;
import java.util.Objects;

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

    public ChessBoard() {
        chessBoard = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        chessBoard[position.getColumn()-1][position.getRow()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return chessBoard[position.getColumn()-1][position.getRow()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        chessBoard = new ChessPiece[8][8];

        chessBoard[0][0] = new ChessPiece(TeamColor.WHITE, PieceType.ROOK);
        chessBoard[0][1] = new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT);
        chessBoard[0][2] = new ChessPiece(TeamColor.WHITE, PieceType.BISHOP);
        chessBoard[0][3] = new ChessPiece(TeamColor.WHITE, PieceType.QUEEN);
        chessBoard[0][4] = new ChessPiece(TeamColor.WHITE, PieceType.KING);
        chessBoard[0][5] = new ChessPiece(TeamColor.WHITE, PieceType.BISHOP);
        chessBoard[0][6] = new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT);
        chessBoard[0][7] = new ChessPiece(TeamColor.WHITE, PieceType.ROOK);

        for (int i = 0; i < 8; i++) {
            chessBoard[1][i] = new ChessPiece(TeamColor.WHITE, PieceType.PAWN);
        }

        chessBoard[7][0] = new ChessPiece(TeamColor.BLACK, PieceType.ROOK);
        chessBoard[7][1] = new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT);
        chessBoard[7][2] = new ChessPiece(TeamColor.BLACK, PieceType.BISHOP);
        chessBoard[7][3] = new ChessPiece(TeamColor.BLACK, PieceType.QUEEN);
        chessBoard[7][4] = new ChessPiece(TeamColor.BLACK, PieceType.KING);
        chessBoard[7][5] = new ChessPiece(TeamColor.BLACK, PieceType.BISHOP);
        chessBoard[7][6] = new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT);
        chessBoard[7][7] = new ChessPiece(TeamColor.BLACK, PieceType.ROOK);

        for (int i = 0; i < 8; i++) {
            chessBoard[6][i] = new ChessPiece(TeamColor.BLACK, PieceType.PAWN);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(chessBoard, that.chessBoard);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(chessBoard);
    }
}
