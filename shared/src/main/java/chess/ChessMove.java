package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    // use rectange example
    private ChessPosition startPosition;
    private ChessPosition endPosition;
    private ChessPiece.PieceType promotionPiece;
    private String specialMoveType;
    private ChessPosition otherPiecePositionInSpecialMove;

    public ChessMove() {
        this.startPosition = new ChessPosition();
        this.endPosition = new ChessPosition(); // automatically sets the point at 1,1
        this.specialMoveType = "Normal";
    }

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = null; // promotionPiece is optional
        this.specialMoveType = "Normal";
        this.otherPiecePositionInSpecialMove = null;
    }

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
        this.specialMoveType = "Normal";
        this.otherPiecePositionInSpecialMove = null;
    }

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece, String specialMoveType, ChessPosition otherPiecePositionInSpecialMove) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
        this.specialMoveType = specialMoveType;
        this.otherPiecePositionInSpecialMove = otherPiecePositionInSpecialMove;
    }

    public String getSpecialMoveType() {
        return specialMoveType;
    }

    public ChessPosition getOtherPiecePositionInSpecialMove() {
        return otherPiecePositionInSpecialMove;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPosition, chessMove.startPosition) && Objects.equals(endPosition, chessMove.endPosition) && promotionPiece == chessMove.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }


    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        if (promotionPiece == null){
            return null;
        }
        return this.promotionPiece;
    }
}
