package chess;

import java.util.Collection;
import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

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
        return myPosition.getRow() + moveRow < 8 && myPosition.getRow() + moveRow > 0 &&
                myPosition.getColumn() + moveCol < 8 && myPosition.getColumn() + moveCol > 0;
    }

    private String checkPositionPiece(ChessBoard board, ChessPosition myPosition, int moveRow, int moveCol){
        ChessPosition newPosition = new ChessPosition(myPosition.getRow() + moveRow, myPosition.getColumn() + moveCol);
        if(board.getPiece(newPosition) == null){
            return "empty";}
        if (board.getPiece(newPosition).pieceColor == board.getPiece(myPosition).pieceColor){
            return "friendly";
        }
        else return "enemy";
    }

    private Collection<ChessPosition> checkDiagonals(ChessBoard board, ChessPosition myPosition, int howFarToCheck) {
        Collection<ChessPosition> viablePositions = new ArrayList<>();

        // Check all four diagonals up to the given distance
        for (int i = 1; i <= howFarToCheck; i++) {
            // Diagonal up-right
            if (checkPositionInBounds(myPosition, i, i)) {
                System.out.println("reviewing: " + i + " " + i);
                String piece = checkPositionPiece(board, myPosition, i, i);
                if (piece.equals("empty")) {
                    System.out.println("adding: " + i + " " + i);
                    viablePositions.add(new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + i));
                } else if (piece.equals("enemy")) {
                    System.out.println("adding: " + i + " " + i);
                    viablePositions.add(new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + i));
                    System.out.println("exiting");
                    break; // Stop if we hit an enemy piece
                } else {
                    System.out.println("exiting");
                    break; // Stop if we hit a piece that is neither empty nor enemy
                }
            } else {
                break; // Stop if the position is out of bounds
            }

            // Diagonal up-left
            if (checkPositionInBounds(myPosition, i, -i)) {
                System.out.println("reviewing: " + i + " " + -i);
                String piece = checkPositionPiece(board, myPosition, i, -i);
                if (piece.equals("empty")) {
                    System.out.println("adding: " + i + " " + -i);
                    viablePositions.add(new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - i));
                } else if (piece.equals("enemy")) {
                    System.out.println("adding: " + i + " " + -i);
                    viablePositions.add(new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - i));
                    System.out.println("exiting");
                    break; // Stop if we hit an enemy piece
                } else {
                    System.out.println("exiting");
                    break; // Stop if we hit a piece that is neither empty nor enemy
                }
            } else {
                break; // Stop if the position is out of bounds
            }

            // Diagonal down-right
            if (checkPositionInBounds(myPosition, -i, i)) {
                System.out.println("reviewing: " + -i + " " + i);
                String piece = checkPositionPiece(board, myPosition, -i, i);
                if (piece.equals("empty")) {
                    System.out.println("adding: " + -i + " " + i);
                    viablePositions.add(new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() + i));
                } else if (piece.equals("enemy")) {
                    System.out.println("adding: " + -i + " " + i);
                    viablePositions.add(new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() + i));
                    System.out.println("exiting");
                    break; // Stop if we hit an enemy piece
                } else {
                    System.out.println("exiting");
                    break; // Stop if we hit a piece that is neither empty nor enemy
                }
            } else {
                break; // Stop if the position is out of bounds
            }

            // Diagonal down-left
            if (checkPositionInBounds(myPosition, -i, -i)) {
                System.out.println("reviewing: " + -i + " " + -i);
                String piece = checkPositionPiece(board, myPosition, -i, -i);
                if (piece.equals("empty")) {
                    System.out.println("adding: " + -i + " " + -i);
                    viablePositions.add(new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() - i));
                } else if (piece.equals("enemy")) {
                    System.out.println("adding: " + -i + " " + -i);
                    viablePositions.add(new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() - i));
                    System.out.println("exiting");
                    break;
                } else {
                    System.out.println("exiting");
                    break; // Stop if we hit a piece that is neither empty nor enemy
                }
            } else {
                break; // Stop if the position is out of bounds
            }
        }

        return viablePositions;
    }

//    private Collection<ChessPosition> checkSides(ChessBoard board, ChessPosition myPosition, int howFarToCheck){
//        Collection<ChessPosition> viablePositions = new ArrayList<>(); // I might need to change this to a collection later
//        for(int i = 0; i <= howFarToCheck; i++){
//            if(checkPositionInBounds(myPosition, 0, i)){
//                if(checkPositionPiece(board, myPosition, 0, i).equals("empty")){
//                    viablePositions.add(new ChessPosition(myPosition.getRow(), myPosition.getColumn() + i));
//                }
//                if(checkPositionPiece(board, myPosition, 0, i).equals("enemy")){
//                    viablePositions.add(new ChessPosition(myPosition.getRow(), myPosition.getColumn() + i));
//                    break;
//                }
//               else{break;}
//            }
//            else{
//                break;
//            }
//            if(checkPositionInBounds(myPosition, 0, -i)){
//                if(checkPositionEmpty(board, myPosition, 0, -i).equals("empty")){
//                    viablePositions.add(new ChessPosition(myPosition.getRow(), myPosition.getColumn() - i));
//                }
//            }
//            else{
//                break;
//            }
//            if(checkPositionInBounds(myPosition, i, 0)){
//                if(checkPositionEmpty(board, myPosition, i, 0).equals("empty")){
//                    viablePositions.add(new ChessPosition(myPosition.getRow() + i, myPosition.getColumn()));
//                }
//            }
//            else{
//                break;
//            }
//            if(checkPositionInBounds(myPosition, -i, 0)){
//                if(checkPositionEmpty(board, myPosition, -i, 0).equals("empty")){
//                    viablePositions.add(new ChessPosition(myPosition.getRow() - i, myPosition.getColumn()));
//                }
//            }
//            else{
//                break;
//            }
//        }
//        return viablePositions;
//    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>(); // I might need to change this to a collection later
        Collection<ChessPosition> viablePositions;
        viablePositions = checkDiagonals(board, myPosition, 1);
        for(ChessPosition position : viablePositions){
            moves.add(new ChessMove(myPosition, position));
        }
//        viablePositions = checkSides(board, myPosition, 1);
//        for(ChessPosition position : viablePositions){
//            moves.add(new ChessMove(myPosition, position));
//        }
        return moves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>(); // I might need to change this to a collection later

        //check if the space is empty
        // check if the space is out of bounds
        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>(); // I might need to change this to a collection later
        Collection<ChessPosition> viablePositions;
        viablePositions = checkDiagonals(board, myPosition, 8);
        for(ChessPosition position : viablePositions){
            moves.add(new ChessMove(myPosition, position));
        }
        return moves;
    }



    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>(); // I might need to change this to a collection later

        //check if the space is empty
        // check if the space is out of bounds
        return moves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>(); // I might need to change this to a collection later

        //check if the space is empty
        // check if the space is out of bounds
        return moves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>(); // I might need to change this to a collection later

        //check if the space is empty
        // check if the space is out of bounds
        return moves;
    }
}
