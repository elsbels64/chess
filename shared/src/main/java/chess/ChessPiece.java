package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;
    private final int[][] diagonals = {{1,1},{-1,1},{1,-1},{-1,-1}};
    private final int[][] sides = {{1,0},{0,1},{-1,0},{0,-1}};
    private final int[][] Ls = {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{-1,2},{1,-2},{-1,-2}};

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
        //throw new RuntimeException("Not implemented");
        if(type == PieceType.KING){
            return kingMoves(board, myPosition);
        }
        else if(type == PieceType.QUEEN){
            return queenMoves(board, myPosition);
        }
        else if(type == PieceType.BISHOP){
            return bishopMoves(board, myPosition);
        }
        else if(type == PieceType.ROOK){
            return rookMoves(board, myPosition);
        }
        else if(type == PieceType.KNIGHT){
            return knightMoves(board, myPosition);
        }
        else{
            return pawnMoves(board, myPosition);
        }
    }

    private boolean checkPositionInBounds(ChessPosition myPosition, int moveRow, int moveCol){
        return myPosition.getRow() + moveRow <= 8 && myPosition.getRow() + moveRow > 0 &&
                myPosition.getColumn() + moveCol <= 8 && myPosition.getColumn() + moveCol > 0;
    }

    private String checkPositionPiece(ChessBoard board, ChessPosition myPosition, int moveRow, int moveCol){
        ChessPosition newPosition = new ChessPosition(myPosition.getRow() + moveRow, myPosition.getColumn() + moveCol);
        if(board.getPiece(newPosition) == null){return "empty";}
        else if (board.getPiece(myPosition).pieceColor==(board.getPiece(newPosition).pieceColor)) {
            return "friendly";
        }
        else{ return "enemy"; }
    }

    private Collection<ChessPosition> checkDirections(ChessBoard board, ChessPosition myPosition, int[][] directions, int howFarToCheck){
        Collection<ChessPosition> viablePositions = new ArrayList<>(); // I might need to change this to a collection later
        for(int[] direction : directions){
            for(int i = 1; i <= howFarToCheck; i++) {
                if (checkPositionInBounds(myPosition, i * direction[0], i * direction[1])) {
                    if (checkPositionPiece(board, myPosition, i * direction[0], i * direction[1]).equals("empty")) {
                        viablePositions.add(new ChessPosition(myPosition.getRow() + i * direction[0], myPosition.getColumn() + i * direction[1]));
                    }
                    else if (checkPositionPiece(board, myPosition, i * direction[0], i * direction[1]).equals("enemy")){
                        viablePositions.add(new ChessPosition(myPosition.getRow() + i * direction[0], myPosition.getColumn() + i * direction[1]));
                        break;
                    }
                    else{
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        return viablePositions;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>(); // I might need to change this to a collection later
        Collection<ChessPosition> viablePositions;
        viablePositions = checkDirections(board, myPosition, diagonals,  1);
        for(ChessPosition position : viablePositions){
            moves.add(new ChessMove(myPosition, position));
        }
        viablePositions = checkDirections(board, myPosition, sides,  1);
        for(ChessPosition position : viablePositions){
            moves.add(new ChessMove(myPosition, position));
        }
        return moves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>(); // I might need to change this to a collection later
        Collection<ChessPosition> viablePositions;
        viablePositions = checkDirections(board, myPosition, diagonals,  8);
        for(ChessPosition position : viablePositions){
            moves.add(new ChessMove(myPosition, position));
        }
        viablePositions = checkDirections(board, myPosition, sides,  8);
        for(ChessPosition position : viablePositions){
            moves.add(new ChessMove(myPosition, position));
        }
        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>(); // I might need to change this to a collection later
        Collection<ChessPosition> viablePositions;
        viablePositions = checkDirections(board, myPosition, diagonals,  8);
        for(ChessPosition position : viablePositions){
            moves.add(new ChessMove(myPosition, position));
        }
        return moves;
    }



    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>(); // I might need to change this to a collection later
        Collection<ChessPosition> viablePositions;
        viablePositions = checkDirections(board, myPosition, sides,  8);
        for(ChessPosition position : viablePositions){
            moves.add(new ChessMove(myPosition, position));
        }
        return moves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>(); // I might need to change this to a collection later
        Collection<ChessPosition> viablePositions;
        viablePositions = checkDirections(board, myPosition, Ls,  1);
        for(ChessPosition position : viablePositions){
            moves.add(new ChessMove(myPosition, position));
        }
        return moves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>(); // I might need to change this to a collection later
        int direction = 1;
        if(board.getPiece(myPosition).pieceColor== ChessGame.TeamColor.BLACK){
            direction = -1;
        }

        if((myPosition.getRow()<=2 && board.getPiece(myPosition).pieceColor== ChessGame.TeamColor.BLACK)
                ||(myPosition.getRow()>=7 && board.getPiece(myPosition).pieceColor== ChessGame.TeamColor.WHITE)){
            if(checkPositionPiece(board, myPosition, direction, 0).equals("empty")) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn()), PieceType.ROOK));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn()), PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn()), PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn()), PieceType.QUEEN));
            }
            if(checkPositionPiece(board, myPosition, direction, 1).equals("enemy")){
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+direction,myPosition.getColumn()+1) , PieceType.ROOK));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+direction,myPosition.getColumn()+1) , PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+direction,myPosition.getColumn()+1) , PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+direction,myPosition.getColumn()+1) , PieceType.QUEEN));
            }
            if(checkPositionPiece(board, myPosition, direction, -1).equals("enemy")){
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+ direction,myPosition.getColumn()-1),PieceType.ROOK));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+ direction,myPosition.getColumn()-1),PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+ direction,myPosition.getColumn()-1),PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+ direction,myPosition.getColumn()-1),PieceType.QUEEN));
            }
        }

        for(int i = 0; i < 1; i++){
            if(checkPositionPiece(board, myPosition, direction, 0).equals("empty")){
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+ direction,myPosition.getColumn())));
            }
            else{
                break;
            }
            if((myPosition.getRow()==2 && board.getPiece(myPosition).pieceColor== ChessGame.TeamColor.WHITE)
                    ||(myPosition.getRow()==7 && board.getPiece(myPosition).pieceColor== ChessGame.TeamColor.BLACK)){
                if(checkPositionPiece(board, myPosition, 2 * direction, 0).equals("empty")){
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2 * direction,myPosition.getColumn())));
                }
            }
        }
        if(checkPositionPiece(board, myPosition, direction, 1).equals("enemy")){
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+direction,myPosition.getColumn()+1)));
        }
        if(checkPositionPiece(board, myPosition, direction, -1).equals("enemy")){
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+ direction,myPosition.getColumn()-1)));
        }
        return moves;
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
