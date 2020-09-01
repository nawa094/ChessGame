package com.chess.engine.board;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

// Represents a chess tile. We can't instantiate this class (It is abstract).
public abstract class Tile {

    // protected and final for immutability. It can only be set once
    protected final int tileCoordinate;
    // Pretty much a cache
    private static final Map<Integer, EmptyTile> EMPTY_TILE_CACHE = createEmptyTiles();

    private static Map<Integer, EmptyTile> createEmptyTiles() {

        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();

        for (int i = 0; i < 64; i++) {
            emptyTileMap.put(i, new EmptyTile(i));
        }

        // We don't want someone to be able to change the map after we've created it.
        // Could have also returned Collections.unmodifiableMap(emptyTileMap); instead of Guava's Immutable map.
        return ImmutableMap.copyOf(emptyTileMap);
    }

    private Tile(final int tileCoordinate) {
        this.tileCoordinate = tileCoordinate;
    }

    // This is pretty much the only method someone could call.
    public static Tile createTile(final int coordinate, final Piece piece) {
        return piece != null ? new OccupiedTile(coordinate, piece) : EMPTY_TILE_CACHE.get(coordinate);
    }

    public abstract boolean isTileOccupied();
    public abstract Piece getPiece();

    // Defines an empty tile behaviour.
    public static final class EmptyTile extends Tile {

        private EmptyTile(int coordinate) {
            super(coordinate);
        }

        @Override
        public String toString() {
            return "-";
        }

        @Override
        public boolean isTileOccupied() {
            return false;
        }

        // There is no piece on an empty tile to return.
        @Override
        public Piece getPiece() {
            return null;
        }
    }

    public static final class OccupiedTile extends Tile {

        // Class private, no way to reference this member.
        private final Piece pieceOnTile;

        private OccupiedTile(int tileCoordinate, Piece piece) {
            super(tileCoordinate);
            this.pieceOnTile = piece;
        }

        @Override
        public String toString() {
            return getPiece().getPieceAlliance().isWhite() ? getPiece().toString().toLowerCase() : getPiece().toString();
        }

        @Override
        public boolean isTileOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return this.pieceOnTile;
        }
    }
}
