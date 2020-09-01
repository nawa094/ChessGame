package com.chess.engine.board;

import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

public abstract class Move {

    final Board board;
    final Piece movedPiece;
    final int destinationCoordinate;

    public static final Move NULL_MOVE = new NullMove();

    Move(final Board board, final Piece piece, final int destinationCoordinate) {
        this.board = board;
        this.movedPiece = piece;
        this.destinationCoordinate = destinationCoordinate;
    }

    public int getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    public int getCurrentCoordinate() {
        return this.movedPiece.getPiecePosition();
    }

    public boolean isAttack() {
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.destinationCoordinate;
        result = prime * result + this.movedPiece.hashCode();

        return result;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Move)) {
            return false;
        }

        final Move otherMove = (Move) other;

        return getDestinationCoordinate() == otherMove.getDestinationCoordinate()
                && getMovedPiece().equals(otherMove.getMovedPiece());
    }

    public Piece getMovedPiece() {
        return this.movedPiece;
    }

    public boolean isCastlingMove() {
        return false;
    }

    public Piece getAttackedPiece() {
        return null;
    }

    public Board execute(Move move) {

        // Making a move means creating a new board with the new position, it will not mutate the current board.
        final Board.Builder builder = new Board.Builder();

        // Place all of the current player's active pieces.
        for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
            // We're placing all of the pieces that weren't moved. Hash code and equals() to calculate equality.
            if (!this.movedPiece.equals(piece)) {
                builder.setPiece(piece);
            }
        }

        // Place all of the opponent's pieces on the board.
        for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
            // No check for the moved piece
            builder.setPiece(piece);
        }

        // setting the moved piece. A Piece is immutable remember so we cannot just change it's position, we create a new piece with the new position.
        builder.setPiece(this.movedPiece.movePiece(this));

        // Switch turn
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());

        // Return the new board.
        return builder.build();
    }

    // This is a non attacking move
    public static final class MajorMove extends Move {

        public MajorMove(final Board board, final Piece piece, final int destinationCoordinate) {
            super(board, piece, destinationCoordinate);
        }

    }

    public static class AttackMove extends Move {

        final Piece attackedPiece;

        public AttackMove(final Board board, final  Piece piece, int destinationCoordinate, final Piece attackedPiece) {
            super(board, piece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode() {
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals (final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AttackMove)) {
                return false;
            }

            AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        @Override
        public Board execute(Move move) {
            return null;
        }

        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }
    }

    // This is a non attacking move
    public static final class PawnMove extends Move {

        public PawnMove(final Board board, final Piece piece, final int destinationCoordinate) {
            super(board, piece, destinationCoordinate);
        }

        @Override
        public Board execute(Move move) {
            return null;
        }
    }

    // This is a non attacking move
    public static class PawnAttackMove extends AttackMove {

        public PawnAttackMove(final Board board, final Piece piece, final int destinationCoordinate, final Piece attackedPiece) {
            super(board, piece, destinationCoordinate, attackedPiece);
        }
    }

    public static final class PawnEnPassantAttackMove extends PawnAttackMove {

        public PawnEnPassantAttackMove(final Board board, final Piece piece, final int destinationCoordinate, final Piece attackedPiece) {
            super(board, piece, destinationCoordinate, attackedPiece);
        }
    }


    public static final class PawnJump extends Move {

        public PawnJump(final Board board, final Piece piece, final int destinationCoordinate) {
            super(board, piece, destinationCoordinate);
        }

        // When a pawn jump occurs, we're setting the newly moved pawn as an En Passant pawn
        @Override
        public Board execute(Move move) {
            final Board.Builder builder = new Board.Builder();

            for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }

            final Pawn movedPiece = (Pawn)this.movedPiece.movePiece(this);
            builder.setEnPassantPawn(movedPiece);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    static abstract class CastleMove extends Move {

        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;

        public CastleMove(final Board board,
                          final Piece piece,
                          final int destinationCoordinate,
                          final Rook castleRook,
                          final int castleRookStart,
                          final int castleRookDestination) {
            super(board, piece, destinationCoordinate);

            this.castleRook = castleRook;
            this.castleRookDestination = castleRookDestination;
            this.castleRookStart = castleRookStart;
        }

        public Rook getCastleRook() {
            return this.castleRook;
        }

        @Override
        public boolean isCastlingMove() {
            return true;
        }

        @Override
        public Board execute(Move move) {
            final Board.Builder builder = new Board.Builder();
            for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }

            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getPieceAlliance()));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    public static final class KingSideCastleMove extends CastleMove {

        public KingSideCastleMove(final Board board, final Piece piece, final int destinationCoordinate,
                                  final Rook castleRook,
                                  final int castleRookStart,
                                  final int castleRookDestination) {
            super(board, piece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString() {
            return "0-0";
        }
    }

    public static final class QueenSideCastleMove extends CastleMove {

        public QueenSideCastleMove(final Board board, final Piece piece, final int destinationCoordinate,
                                   final Rook castleRook,
                                   final int castleRookStart,
                                   final int castleRookDestination) {
            super(board, piece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString() {
            return "0-0-0";
        }
    }

    public static class NullMove extends Move {

        // This will throw an exception
        public NullMove() {
            super(null, null, -1);
        }

        @Override
        public Board execute(Move move) {
            throw new RuntimeException("Cannot execute null move.");
        }
    }

    public static class MoveFactory {
        private MoveFactory() {

        }

        public static Move createMove(final Board board, final int currentCoordinate, final int destinationCoordinate) {

            for (final Move move : board.getAllLegalMoves()) {
                if (move.getCurrentCoordinate() == destinationCoordinate) {
                    return move;
                }
            }
            return NULL_MOVE;
        }


    }
}
