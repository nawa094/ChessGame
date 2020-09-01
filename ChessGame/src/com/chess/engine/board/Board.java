package com.chess.engine.board;

import com.chess.engine.Alliance;
import com.chess.engine.pieces.*;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.lang.reflect.Array;
import java.util.*;

public class Board {

    private final List<Tile> gameBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;

    private final WhitePlayer whiteplayer;
    private final BlackPlayer blackplayer;
    private final Player currentPlayer;

    private Board(final Builder builder) {
        this.gameBoard = createGameBoard(builder);

        whitePieces = calculateActivePieces(this.gameBoard, Alliance.WHITE);
        blackPieces = calculateActivePieces(this.gameBoard, Alliance.BLACK);

        final Collection<Move> whiteStandardLegalMoves = calculateLegalMoves(whitePieces);
        final Collection<Move> blackStandardLegalMoves = calculateLegalMoves(blackPieces);

        this.whiteplayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.blackplayer = new BlackPlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);

        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whiteplayer, this.blackplayer);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            final String tileText = this.gameBoard.get(i).toString();
            sb.append(String.format("%3s", tileText));

            if ((i + 1) % BoardUtils.NUM_TILES_PER_ROW == 0) {
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }

    public Collection<Piece> getBlackPieces() {
        return this.blackPieces;
    }

    public Collection<Piece> getWhitePieces() {

        return this.whitePieces;
    }

    public Player whitePlayer() {
        return this.whiteplayer;
    }

    public Player blackPlayer() {
        return this.blackplayer;
    }

    public Player currentPlayer() {
        return this.currentPlayer;
    }

    private Collection<Move> calculateLegalMoves(Collection<Piece> pieces) {

        final List<Move> legalMoves = new ArrayList<>();

        for (final Piece piece : pieces) {

            legalMoves.addAll(piece.calculateLegalMoves(this));
        }

        return ImmutableList.copyOf(legalMoves);
    }

    private static  Collection<Piece> calculateActivePieces(final List<Tile> gameBoard, final Alliance alliance) {
        List<Piece> activePieces = new ArrayList<>();

        for(Tile tile : gameBoard) {
            if (tile.isTileOccupied()) {
                final Piece piece = tile.getPiece();
                if (piece.getPieceAlliance() == alliance) {
                    activePieces.add(piece);
                }
            }
        }

        return ImmutableList.copyOf(activePieces);
    }

    public Tile getTile(final int tileCoordinate) {

        return gameBoard.get(tileCoordinate);
    }

    private static List<Tile> createGameBoard(Builder builder) {
        final Tile[] tiles = new Tile[BoardUtils.NUM_TILES];

        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            tiles[i] = Tile.createTile(i, builder.BoardConfig.get(i));
        }

        return ImmutableList.copyOf(tiles);
    }

    public static Board CreateStandardBoard() {

        final Builder builder = new Builder();

        // Black Pieces
        builder.setPiece(new Rook(0, Alliance.BLACK));
        builder.setPiece(new Knight(2, Alliance.BLACK));
        builder.setPiece(new Bishop(1, Alliance.BLACK));
        builder.setPiece(new Queen(3, Alliance.BLACK));
        builder.setPiece(new King(4, Alliance.BLACK));
        builder.setPiece(new Bishop(5, Alliance.BLACK));
        builder.setPiece(new Knight(6, Alliance.BLACK));
        builder.setPiece(new Rook(7, Alliance.BLACK));
        builder.setPiece(new Pawn(8, Alliance.BLACK));
        builder.setPiece(new Pawn(9, Alliance.BLACK));
        builder.setPiece(new Pawn(10, Alliance.BLACK));
        builder.setPiece(new Pawn(11, Alliance.BLACK));
        builder.setPiece(new Pawn(12, Alliance.BLACK));
        builder.setPiece(new Pawn(13, Alliance.BLACK));
        builder.setPiece(new Pawn(14, Alliance.BLACK));
        builder.setPiece(new Pawn(15, Alliance.BLACK));

        // White Pieces
        builder.setPiece(new Rook(56, Alliance.WHITE));
        builder.setPiece(new Knight(57, Alliance.WHITE));
        builder.setPiece(new Bishop(58, Alliance.WHITE));
        builder.setPiece(new Queen(59, Alliance.WHITE));
        builder.setPiece(new King(60, Alliance.WHITE));
        builder.setPiece(new Bishop(61, Alliance.WHITE));
        builder.setPiece(new Knight(62, Alliance.WHITE));
        builder.setPiece(new Rook(63, Alliance.WHITE));
        builder.setPiece(new Pawn(48, Alliance.WHITE));
        builder.setPiece(new Pawn(49, Alliance.WHITE));
        builder.setPiece(new Pawn(50, Alliance.WHITE));
        builder.setPiece(new Pawn(51, Alliance.WHITE));
        builder.setPiece(new Pawn(52, Alliance.WHITE));
        builder.setPiece(new Pawn(53, Alliance.WHITE));
        builder.setPiece(new Pawn(54, Alliance.WHITE));
        builder.setPiece(new Pawn(55, Alliance.WHITE));

        builder.setMoveMaker(Alliance.WHITE);
        return builder.build();
    }

    public Iterable<Move> getAllLegalMoves() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.whiteplayer.getLegalMoves(), this.blackplayer.getLegalMoves()));
    }

    public static class Builder {

        Map<Integer, Piece> BoardConfig;
        Alliance nextMoveMaker;
        Pawn enPassantPawn;

        public Builder() {
            this.BoardConfig = new HashMap<>();
        }

        public Builder setPiece(final Piece piece) {
            this.BoardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        public Builder setMoveMaker(final Alliance alliance) {
            this.nextMoveMaker = alliance;
            return this;
        }
        public Board build() {
            return new Board(this);
        }


        public void setEnPassantPawn(Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
        }
    }
}
