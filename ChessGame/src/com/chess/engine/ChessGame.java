package com.chess.engine;

import com.chess.engine.board.Board;

public class ChessGame {
    public static void main(String[] args) {
        Board board = Board.CreateStandardBoard();

        System.out.println(board);
    }
}
