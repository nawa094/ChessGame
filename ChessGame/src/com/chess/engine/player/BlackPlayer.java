package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlackPlayer extends Player {

    public BlackPlayer(Board board, Collection<Move> whiteStandardLegalMoves, Collection<Move> blackStandardLegalMoves) {

        super(board, blackStandardLegalMoves, whiteStandardLegalMoves);
    }

    @Override
    protected Collection<Move> calculateKingsCastles(Collection<Move> playerLegals, Collection<Move> opponentLegal) {
        // The available castling moves to the black king.
        final List<Move> kingCastles = new ArrayList<>();

        // Precondition for castling is the it is the king's first move and the king is not in check, that's what we're checking here.
        if (this.playerKing.isFirstMove() && !isInCheck()) {
            // We're checking if the two adjacent tiles to the king are occupied; in order to castle they need to be empty of course
            if (!this.board.getTile(5).isTileOccupied() && !this.board.getTile(6).isTileOccupied()) {
                // This is the tile that our king-side rook should be on.
                final Tile rookTile = this.board.getTile(7);

                // It should also be the rook's first move
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {

                    // We need to check that the king is not passing over any attacked squares.
                    if (Player.calculateAttacksOnTile(5, opponentLegal).isEmpty() &&
                            Player.calculateAttacksOnTile(6, opponentLegal).isEmpty() && rookTile.getPiece().getPieceType().isRook()) {
                        // Then we're all good to castle
                        kingCastles.add(null);
                    }

                }
            }

            // We're working on queen-side castling here. There are three tiles between the rook and the king that need to be empty.
            if (!this.board.getTile(1).isTileOccupied() &&
                    !this.board.getTile(2).isTileOccupied() &&
                    !this.board.getTile(3).isTileOccupied()) {

                final Tile rookTile = this.board.getTile(0);

                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    kingCastles.add(null);
                }
            }
        }

        return ImmutableList.copyOf(kingCastles);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.whitePlayer();
    }
}
