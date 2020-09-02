package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WhitePlayer extends Player {
    public WhitePlayer(final Board board, final Collection<Move> whiteStandardLegalMoves, final Collection<Move> blackStandardLegalMoves) {
        super(board, whiteStandardLegalMoves, blackStandardLegalMoves);
    }

    // We're trying to calculate the white king's castles moves.
    @Override
    protected Collection<Move> calculateKingsCastles(Collection<Move> playerLegals, Collection<Move> opponentLegal) {

        // The available castling moves to the white king.
        final List<Move> kingCastles = new ArrayList<>();

        // Precondition for castling is the it is the king's first move and the king is not in check, that's what we're checking here.
        if (this.playerKing.isFirstMove() && !isInCheck()) {
            // We're checking if the two adjacent tiles to the king are occupied; in order to castle they need to be empty of course
            if (!this.board.getTile(61).isTileOccupied() && !this.board.getTile(62).isTileOccupied()) {
                // This is the tile that our king-side rook should be on.
                final Tile rookTile = this.board.getTile(63);

                // It should also be the rook's first move
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {

                    // We need to check that the king is not passing over any attacked squares.
                    if (Player.calculateAttacksOnTile(61, opponentLegal).isEmpty() &&
                            Player.calculateAttacksOnTile(62, opponentLegal).isEmpty() && rookTile.getPiece().getPieceType().isRook()) {
                        // Then we're all good to castle
                        kingCastles.add(new Move.KingSideCastleMove(this.board, this.playerKing,
                                62,
                                (Rook)rookTile.getPiece(),
                                rookTile.getTileCoordinate(),
                                61));
                    }
                }
            }

            // We're working on queen-side castling here. There are three tiles between the rook and the king that need to be empty.
            if (!this.board.getTile(59).isTileOccupied() &&
                    !this.board.getTile(58).isTileOccupied() &&
                    !this.board.getTile(57).isTileOccupied()) {

                final Tile rookTile = this.board.getTile(56);

                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove() && Player.calculateAttacksOnTile(58, opponentLegal).isEmpty() &&
                        Player.calculateAttacksOnTile(59, opponentLegal).isEmpty() && rookTile.getPiece().getPieceType().isRook()) {
                    kingCastles.add(new Move.QueenSideCastleMove(this.board,
                            this.playerKing,
                            58,
                            (Rook)rookTile.getPiece(),
                            rookTile.getTileCoordinate(),
                            59));
                }
            }
        }

        return ImmutableList.copyOf(kingCastles);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }
}
