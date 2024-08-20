package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;
    private ChessPosition doubleMovePawn;
    private Collection<ChessPosition> unmovedRooksAndKings;



    public ChessGame() {
        System.out.println("creating chess board");
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
        unmovedRooksAndKings = new ArrayList<>();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece= board.getPiece(startPosition);
        if(piece==null){
            return null;
        }
        Collection<ChessMove> validMoves = piece.pieceMoves(board, startPosition);

        //EnPassant
        if(piece.getPieceType() == ChessPiece.PieceType.PAWN){
            ChessPosition checkEnPassantLeft = new ChessPosition(startPosition.getRow(), startPosition.getColumn() - 1);
            ChessPosition checkEnPassantRight = new ChessPosition(startPosition.getRow(), startPosition.getColumn() + 1);

            if(checkEnPassantLeft.equals(doubleMovePawn) ) {
                if (piece.getTeamColor() == TeamColor.WHITE) {
                    validMoves.add(new ChessMove(startPosition, new ChessPosition(checkEnPassantLeft.getRow() + 1, checkEnPassantLeft.getColumn()), null, "EnPassant", doubleMovePawn));
                }else{
                    validMoves.add(new ChessMove(startPosition, new ChessPosition(checkEnPassantLeft.getRow() - 1, checkEnPassantLeft.getColumn()), null, "EnPassant", doubleMovePawn));
                }
            }
            else if (checkEnPassantRight.equals(doubleMovePawn)){
                if (piece.getTeamColor() == TeamColor.WHITE) {
                    validMoves.add(new ChessMove(startPosition, new ChessPosition(checkEnPassantRight.getRow() + 1, checkEnPassantRight.getColumn()), null, "EnPassant", doubleMovePawn));
                }else{
                    validMoves.add(new ChessMove(startPosition, new ChessPosition(checkEnPassantRight.getRow() - 1, checkEnPassantRight.getColumn()), null, "EnPassant", doubleMovePawn));
                }
            }
        }

        //castling


//        remove moves that leave the king exposed/in check
        Collection<ChessMove> toRemove = new ArrayList<>();
        for(ChessMove move : validMoves){
            var newGame = new ChessGame();
            newGame.setBoard(new ChessBoard(this.board));
            newGame.makeTestMove(move, piece);

            if(newGame.isInCheck(piece.getTeamColor())){
                toRemove.add(move);
            }
        }
        validMoves.removeAll(toRemove);
        return validMoves;
    }

    private void makeTestMove(ChessMove move, ChessPiece piece) {
        if(move.getSpecialMoveType().equals("EnPassant")){
            board.addPiece(move.getStartPosition(), null);//empty where the piece was
            board.addPiece(move.getEndPosition(), piece);
            board.addPiece(move.getOtherPiecePositionInSpecialMove(), null);
        }
        board.addPiece(move.getStartPosition(), null);//empty where the piece was
        board.addPiece(move.getEndPosition(), piece); //put the piece in its new position
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if(board.getPiece(move.getStartPosition())== null){
            throw new InvalidMoveException("Move not valid. No piece there.");
        }
        if (!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("Move not valid");
        }
        if(board.getPiece(move.getStartPosition()).getTeamColor()!=teamTurn){
            throw new InvalidMoveException("Move not valid. Not the teams turn.");
        }
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());
        if(move.getPromotionPiece()!=null){ // promote the piece if necessary
            movingPiece = new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece());
        }
        //update the unmoved rooks and kings list if it is a king or a rook that is moving for the first time
        if(movingPiece.getPieceType()== ChessPiece.PieceType.KING || movingPiece.getPieceType()== ChessPiece.PieceType.ROOK && unmovedRooksAndKings.contains(move.getStartPosition())){
            unmovedRooksAndKings.remove(move.getStartPosition());
        }

        //find some way to update pawn for EnPassant
        if(movingPiece.getPieceType()== ChessPiece.PieceType.PAWN){
            ChessPosition checkEnPassantLeft = new ChessPosition(move.getStartPosition().getRow(), move.getStartPosition().getColumn() - 1);
            ChessPosition checkEnPassantRight = new ChessPosition(move.getStartPosition().getRow(), move.getStartPosition().getColumn() + 1);

            if(checkEnPassantLeft.equals(doubleMovePawn) || checkEnPassantRight.equals(doubleMovePawn)){
                board.addPiece(doubleMovePawn, null);
                doubleMovePawn = null;
            }
            else if (Math.abs(move.getEndPosition().getRow() - move.getStartPosition().getRow()) > 1) {
                doubleMovePawn = move.getEndPosition();
            }
            else{
                // if you aren't moving a pawn double then you are missing your chance for EnPassant so double move pawn should be cleared out
                doubleMovePawn = null;
            }
        }
        else{
            // if you aren't moving a pawn double then you are missing your chance for EnPassant so double move pawn should be cleared out
            doubleMovePawn = null;
        }

        if(move.getSpecialMoveType().equals("Castling")){
            throw new RuntimeException("Castling is Not implemented yet.");
        }
        else{
            //it's a normal move
            board.addPiece(move.getStartPosition(), null);//empty where the piece was
            board.addPiece(move.getEndPosition(), movingPiece); //put the piece in its new position
        }

        board.printBoard();

        //update whose turn it is///////////////////////////////////////////////////////////////////////////////
        if(teamTurn == TeamColor.WHITE){
            teamTurn = TeamColor.BLACK;
        }else{teamTurn = TeamColor.WHITE;}
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        board.updateColorPositionsAndKings();
        ChessPosition kingPosition = (teamColor == TeamColor.WHITE) ? board.whiteKing : board.blackKing;
        Collection<ChessPosition> opponentPositions = (teamColor == TeamColor.WHITE) ? board.blackPositions : board.whitePositions;

        for (ChessPosition position : opponentPositions) {
            ChessPiece piece = board.getPiece(position);
//            System.out.println("Inspecting oponent: " + piece.getPieceType() + " -at: " + position.getRow() + ", " + position.getColumn());
            Collection<ChessMove> pieceMoves = piece.pieceMoves(board, position);

            // Check if any move of the piece can reach the king's position
            for (ChessMove move : pieceMoves) {
//                System.out.println("Move being considered: " + move.getEndPosition().getRow() + ", " + move.getEndPosition().getColumn());
                if (move.getEndPosition().equals(kingPosition)) {
//                    System.out.println("King is in check by: " + piece.getPieceType() + " from position: " + position.getRow() + ", " + position.getColumn());
                    return true; // King is in check
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        board.updateColorPositionsAndKings();
        Collection<ChessPosition> teamPositions = (teamColor == TeamColor.BLACK) ? board.blackPositions : board.whitePositions;

        for (ChessPosition position : teamPositions) {
            ChessPiece piece = board.getPiece(position);
            Collection<ChessMove> pieceMoves = validMoves(position);

            // Check if any move of the piece can reach the king's position
            for (ChessMove move : pieceMoves) {
                var newGame = new ChessGame();
                newGame.setBoard(new ChessBoard(this.board));
                newGame.makeTestMove(move, piece);

                if(!newGame.isInCheck(teamColor)){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(teamColor.equals(teamTurn)) {
            if (!isInCheck(teamColor)) {
                board.updateColorPositionsAndKings();
                Collection<ChessPosition> teamPositions = (teamColor == TeamColor.BLACK) ? board.blackPositions : board.whitePositions;
                Collection<ChessMove> pieceMoves = new ArrayList<>();

                for (ChessPosition position : teamPositions) {
                    pieceMoves.addAll(validMoves(position));
                }
                if (pieceMoves.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }



    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board=board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
