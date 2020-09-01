package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;

public abstract class Piece {

    protected final int piecePosition;
    // Black or White
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;
    protected final PieceType pieceType;
    private final int cachedHashCode;

    Piece(final PieceType pieceType, final int piecePosition, final Alliance pieceAlliance) {

        this.pieceAlliance = pieceAlliance;
        this.piecePosition = piecePosition;
        //TODO: more work
        this.isFirstMove = false;
        this.pieceType = pieceType;
        cachedHashCode = computedHashCode();
    }

    // When you interact with collections of objects you want to override the hashCode and equals methods. We want Object equality and not reference equality.
    private int computedHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);

        // Because our object is immutable, we don't have to calculate this each time.
        return result;
    }

    // We need to define how to do an object equality test. For correctness. References equality is the default implementation which we don't want.
    @Override
    public boolean equals(final Object other) {

        // This is why we had to override the hashCode method.
        if (this == other) {
            return true;
        }

        // If we aren't looking at a piece don't even bother.
        if (!(other instanceof Piece)) {
            return false;
        }

        // Since we know it's a piece we can safely make this cast.
        final Piece otherPiece = (Piece) other;

        return piecePosition == otherPiece.getPiecePosition() && pieceType == otherPiece.getPieceType() && pieceAlliance == otherPiece.getPieceAlliance()
                && isFirstMove == otherPiece.isFirstMove();
    }

    // Returns the address of the object in memory. https://www.journaldev.com/21095/java-equals-hashcode
    @Override
    public int hashCode() {
        return this.cachedHashCode;
    }

    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    public Alliance getPieceAlliance() {
        return this.pieceAlliance;
    }

    public int getPiecePosition() {
        return piecePosition;
    }

    public abstract Collection<Move> calculateLegalMoves(final Board board);

    public abstract Piece movePiece(Move move);

    public PieceType getPieceType() {
        return this.pieceType;
    }

    public enum PieceType {

        PAWN("P") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KING("K") {
            @Override
            public boolean isKing() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        BISHOP("B") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KNIGHT("N") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        ROOK("R") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }
        },
        QUEEN("Q") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        };

        private String pieceName;
        PieceType(final String pieceName) {
            this.pieceName = pieceName;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }

        public abstract boolean isKing();
        public abstract boolean isRook();
    }
}
