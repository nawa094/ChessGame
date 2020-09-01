package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player {

    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    Player(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentMoves) {
        this.board = board;
        this.playerKing = establishKing();
        this.legalMoves = legalMoves;
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
    }

    protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> moves) {
        final List<Move> attackMoves = new ArrayList<>();
        for (final Move move : moves) {
            if (piecePosition == move.getDestinationCoordinate()) {
                attackMoves.add(move);
            }
        }

        return ImmutableList.copyOf(attackMoves);
    }

    protected King establishKing() {
        for (final Piece piece : getActivePieces()) {
            if (piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }

        throw new RuntimeException("No King on the board.");
    }

    public boolean isMoveLegal(final Move move) {
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck() {
        return this.isInCheck;
    }

    public boolean isInCheckMate() {
        return this.isInCheck && !hasEscapeMoves();
    }

    public boolean isInStaleMate() {
        return !this.isInCheck && !hasEscapeMoves();
    }

    protected boolean hasEscapeMoves() {
        for (final Move move : legalMoves) {
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                return true;
            }
        }

        return false;
    }

    public boolean isCastled() {
        return false;
    }

    // Critical method.
    public MoveTransition makeMove(final Move move) {

        // First we completely rule out the move if it isn't a legal move.
        if (!isMoveLegal(move)) {
            // Since the move is invalid we'll return the a new move transition with the current board (no change) and a status of ILLEGAL_MOVE
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }

        // The move was not illegal so we make the move and get back a new board that needs to be evaluated
        final Board transitionBoard = move.execute(move);

        // Calculates whether or not there are currents attacks on the current player's king. If there are then that move was illegal.
        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                transitionBoard.currentPlayer().getLegalMoves());

        // The candidate move would leave the player in check so it isn't a valid move, so we return the current board again with a status of LEAVES_PLAYER_IN_CHECK
        if (!kingAttacks.isEmpty()) {
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }

        // There were no problems with the candidate move so we return the new board which re have transitioned to.
        // Our Board class is immutable remember, so we cannot change it; instead we create a new one with the new piece position.
        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }

    public King getPlayerKing() {
        return this.getPlayerKing();
    }

    public Collection<Move> getLegalMoves() {
        return this.legalMoves;
    }

    protected abstract Collection<Move> calculateKingsCastles(Collection<Move> playerLegals, Collection<Move> opponentLegal);
    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
}
