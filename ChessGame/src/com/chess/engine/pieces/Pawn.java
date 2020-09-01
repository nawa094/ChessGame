package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn extends Piece {
    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = { 8, 16, 7, 9 };

    public Pawn(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.PAWN, piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {

        final List<Move> legalMoves = new ArrayList<>();

        for (final int candidateCoordinateOffset : CANDIDATE_MOVE_VECTOR_COORDINATES) {

            final int candidateDestinationCoordinate = this.piecePosition + (this.getPieceAlliance().getDirection() * candidateCoordinateOffset);

            if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                continue;
            }

            if (candidateCoordinateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                // TODO: more work
                legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));

            } else if ((candidateCoordinateOffset == 16 && this.IsFirstMove() && BoardUtils.SECOND_ROW[this.piecePosition] && this.getPieceAlliance().isBlack()) || (
                    BoardUtils.SEVENTH_ROW[this.piecePosition] && this.getPieceAlliance().isWhite())) {

                final int behindCandidateDestination = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if (!board.getTile(behindCandidateDestination).isTileOccupied() && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
                }
            } else if (candidateCoordinateOffset == 7 &&
                    !(BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite() ||
                    BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack())) {
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceCandidate.getPieceAlliance()) {
                        // TODO: more work here
                        legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
                    }
                }

            } else if (candidateCoordinateOffset == 9 && !((BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()) ||
                    (BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack())) ) {
                final Piece pieceCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                if (this.pieceAlliance != pieceCandidate.getPieceAlliance()) {
                    // TODO: more work here
                    legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
                }
            }
        }

        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }
}
