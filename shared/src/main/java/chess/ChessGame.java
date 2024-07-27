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
    private Collection<ChessPosition> whitePositions;// there might be problems with removing from this list later
    private Collection<ChessPosition> blackPositions;
    private ChessPosition whiteKing;
    private ChessPosition blackKing;
    private Collection<ChessPosition> doubleMovePawns;
    private Collection<ChessPosition> unmovedRooksAndKings;



    public ChessGame() {
        System.out.println("creating chess board");
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
        whitePositions = new ArrayList<>();
        blackPositions = new ArrayList<>();
        doubleMovePawns = new ArrayList<>();
        unmovedRooksAndKings = new ArrayList<>();
        for(int i = 1; i < 3; i++){ // these might cause problems later
            for(int j = 1; j < 9; j++){
                whitePositions.add(new ChessPosition(i, j));
            }
        }
        for(int i = 1; i < 3; i++){
            for(int j = 1; j < 9; j++){
                blackPositions.add(new ChessPosition(i, j));
            }
        }
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
//        if (isInCheck(board.getPiece(startPosition).getTeamColor())){
//            if(!(piece.getPieceType() == ChessPiece.PieceType.KING)){return null;}}
        Collection<ChessMove> validMoves = piece.pieceMoves(board, startPosition);
        //add enpasant and castling

        //remove moves that leave the king exposed/incheck

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if(board.getPiece(move.getStartPosition())== null){
            throw new InvalidMoveException("Move not valid");
        }
        if (!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("Move not valid");
        }
        if(board.getPiece(move.getStartPosition()).getTeamColor()!=teamTurn){
            throw new InvalidMoveException("Move not valid");
        }
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());;
        if(move.getPromotionPiece()!=null){ // promote the piece if necessary
            movingPiece = new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece());
        }
        //update the white and black positions
        if(movingPiece.getTeamColor()==TeamColor.WHITE){
            whitePositions.remove(move.getStartPosition());
            whitePositions.add(move.getEndPosition());
        } else {
            blackPositions.remove(move.getStartPosition());
            blackPositions.add(move.getEndPosition());
        }
        //update the unmoved rooks and kings list if it is a king or a rook that is moving for the first time
        if(movingPiece.getPieceType()== ChessPiece.PieceType.KING || movingPiece.getPieceType()== ChessPiece.PieceType.ROOK && unmovedRooksAndKings.contains(move.getStartPosition())){
            unmovedRooksAndKings.remove(move.getStartPosition());
        }
        //find some way to update pawn for enpasant

        board.addPiece(move.getStartPosition(), null);//empty where the piece was
        board.addPiece(move.getEndPosition(), movingPiece); //put the piece in its new position
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


        if(teamColor==TeamColor.WHITE){
            for(ChessPosition position : blackPositions){
                Collection<ChessMove> pieceMoves = validMoves(position);
                for(ChessMove move : pieceMoves){
                    if(move.getEndPosition()==whiteKing){
                        return true;
                    }
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
