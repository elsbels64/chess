package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

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

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        chessBoard[(position.getColumn())-1][(position.getRow())-1] = piece;
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
        return chessBoard[(position.getColumn())-1][(position.getRow())-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        chessBoard = new ChessPiece[8][8];

        chessBoard[0][0] = new ChessPiece(TeamColor.WHITE, PieceType.ROOK);
        chessBoard[1][0] = new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT);
        chessBoard[2][0] = new ChessPiece(TeamColor.WHITE, PieceType.BISHOP);
        chessBoard[3][0] = new ChessPiece(TeamColor.WHITE, PieceType.QUEEN);
        chessBoard[4][0] = new ChessPiece(TeamColor.WHITE, PieceType.KING);
        chessBoard[5][0] = new ChessPiece(TeamColor.WHITE, PieceType.BISHOP);
        chessBoard[6][0] = new ChessPiece(TeamColor.WHITE, PieceType.KNIGHT);
        chessBoard[7][0] = new ChessPiece(TeamColor.WHITE, PieceType.ROOK);

        for (int i = 0; i < 8; i++) {
            chessBoard[i][1] = new ChessPiece(TeamColor.WHITE, PieceType.PAWN);
        }

        chessBoard[0][7] = new ChessPiece(TeamColor.BLACK, PieceType.ROOK);
        chessBoard[1][7] = new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT);
        chessBoard[2][7] = new ChessPiece(TeamColor.BLACK, PieceType.BISHOP);
        chessBoard[3][7] = new ChessPiece(TeamColor.BLACK, PieceType.QUEEN);
        chessBoard[4][7] = new ChessPiece(TeamColor.BLACK, PieceType.KING);
        chessBoard[5][7] = new ChessPiece(TeamColor.BLACK, PieceType.BISHOP);
        chessBoard[6][7] = new ChessPiece(TeamColor.BLACK, PieceType.KNIGHT);
        chessBoard[7][7] = new ChessPiece(TeamColor.BLACK, PieceType.ROOK);

        for (int i = 0; i < 8; i++) {
            chessBoard[i][6] = new ChessPiece(TeamColor.BLACK, PieceType.PAWN);
        }
    }

    public void updateColorPositionsAndKings(){
        blackPositions = new ArrayList<>();
        whitePositions = new ArrayList<>();
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(chessBoard[i][j] != null){
                    if(chessBoard[i][j].getTeamColor()==TeamColor.WHITE){
                        whitePositions.add(new ChessPosition(j + 1 ,i + 1 ));
                        if(chessBoard[i][j].getPieceType()==PieceType.KING){
                            whiteKing = new ChessPosition(j + 1 ,i + 1);
                        }
                    }else{
                        blackPositions.add(new ChessPosition(j + 1 ,i + 1));
                        if(chessBoard[i][j].getPieceType()==PieceType.KING){
                            blackKing = new ChessPosition(j + 1 ,i + 1);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(this.chessBoard, that.chessBoard);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(chessBoard);
    }
}
