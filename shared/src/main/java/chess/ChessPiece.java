package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor pieceColor;
    ChessPiece.PieceType type;
    private final int[][] bishopDirections = {{1,1},{-1,1},{1,-1},{-1,-1}};
    private final int[][] rookDirections = {{1,0},{0,1},{-1,0},{0,-1}};
    private final int[][] knightDirections = {{1,2},{2,1},{-1,2},{-2,1},{1,-2},{2,-1},{-1,-2},{-2,-1}};

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece.PieceType type = board.getPiece(myPosition).getPieceType();
        if(type.equals(PieceType.QUEEN)){
            moves = queenMoves(board, myPosition);
        }
        if(type.equals(PieceType.KING)){
            moves = kingMoves(board, myPosition);
        }
        if(type.equals(PieceType.BISHOP)){
            moves = bishopMoves(board, myPosition);
        }
        if(type.equals(PieceType.ROOK)){
            moves = rookMoves(board, myPosition);
        }
        if(type.equals(PieceType.KNIGHT)){
            moves = knightMoves(board, myPosition);
        }
        if(type.equals(PieceType.PAWN)){
            moves = pawnMoves(board, myPosition);
        }
        return moves;
    }

    private Boolean outOfBounds(ChessPosition checkingPosition) {
        return(checkingPosition.getRow()>8||checkingPosition.getColumn()>8)||
                (checkingPosition.getRow()<1||checkingPosition.getColumn()<1);
    }

    private String checkSpace(ChessBoard board, ChessPosition myPosition, ChessPosition checkingPosition) {
        if(outOfBounds(checkingPosition)){
            return "stop";
        }
        ChessPiece piece = board.getPiece(myPosition);
        ChessPiece otherSpace = board.getPiece(checkingPosition);
        if(otherSpace==null){
            return "empty";
        }
        if(otherSpace.getTeamColor().equals(piece.getTeamColor())){
            return "stop";
        }
        return "enemy";
    }

    private Collection<ChessMove> checkDirections(ChessBoard board, ChessPosition myPosition, int[][] directions, int howFar) {
        Collection<ChessMove> moves = new ArrayList<>();
        for(int[] direction: directions){
            for(int i = 1; i<=howFar; i++){
                ChessPosition checkingPosition = new ChessPosition(myPosition.getRow()+(i*direction[0]), myPosition.getColumn()+(i*direction[1]));
                String checkSpaceResult = checkSpace(board,myPosition,checkingPosition);
                if(checkSpaceResult.equals("stop")){
                    break;
                } else if (checkSpaceResult.equals("empty")) {
                    moves.add(new ChessMove(myPosition,checkingPosition,null));
                }else{
                    moves.add(new ChessMove(myPosition,checkingPosition,null));
                    break;
                }
            }
        }
        return moves;
    }

    public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = checkDirections(board, myPosition, bishopDirections,8);
        moves.addAll(checkDirections(board, myPosition, rookDirections, 8));
        return moves;
    }

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = checkDirections(board, myPosition, bishopDirections,1);
        moves.addAll(checkDirections(board, myPosition, rookDirections, 1));
        return moves;
    }

    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        return checkDirections(board, myPosition, bishopDirections,8);
    }

    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        return checkDirections(board, myPosition, rookDirections,8);
    }

    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        return checkDirections(board, myPosition, knightDirections,1);
    }

    private Collection<ChessMove> checkPawnDirections(ChessBoard board, ChessPosition myPosition, int direction, int startingRow, int promotionRow) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[] pawnCaptureColumnDirections = {1,-1};
        Collection<ChessPiece.PieceType> promotionTypes = new ArrayList<>();
        ChessPosition checkingPosition = new ChessPosition(myPosition.getRow()+(direction), myPosition.getColumn());
        if(myPosition.getRow()==promotionRow){
            promotionTypes.add(PieceType.QUEEN);
            promotionTypes.add(PieceType.ROOK);
            promotionTypes.add(PieceType.BISHOP);
            promotionTypes.add(PieceType.KNIGHT);
        }else{
            promotionTypes.add(null);
        }
        if(checkSpace(board,myPosition, checkingPosition).equals("empty")){
            for(PieceType type:promotionTypes){
                moves.add(new ChessMove(myPosition,checkingPosition,type));
            }
            if(myPosition.getRow()==startingRow){
                checkingPosition = new ChessPosition(myPosition.getRow()+(2*direction), myPosition.getColumn());
                if(checkSpace(board,myPosition, checkingPosition).equals("empty")) {
                    moves.add(new ChessMove(myPosition, checkingPosition));
                }
            }
        }
        for(int colDirection:pawnCaptureColumnDirections){
            checkingPosition = new ChessPosition(myPosition.getRow()+(direction), myPosition.getColumn()+colDirection);
            if(checkSpace(board,myPosition, checkingPosition).equals("enemy")) {
                for (PieceType type : promotionTypes) {
                    moves.add(new ChessMove(myPosition, checkingPosition, type));
                }
            }
        }
        return moves;
    }


    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        if(board.getPiece(myPosition).getTeamColor()== ChessGame.TeamColor.WHITE){
            moves = checkPawnDirections(board, myPosition, 1, 2, 7);
        }else{
            moves = checkPawnDirections(board, myPosition, -1, 7, 2);
        }
        return moves;
    }

    @Override
    public String toString() {
        return "ChessPiece{" + pieceColor +
                ", " + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
