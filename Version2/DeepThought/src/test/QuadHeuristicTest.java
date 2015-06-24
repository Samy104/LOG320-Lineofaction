package test;

import static org.junit.Assert.*;
import game.Board;
import game.Move;
import game.QuadHeuristic;

import org.junit.Test;

public class QuadHeuristicTest 
{
	private static final double DELTA = 1/1000;
	
	@Test
	public void initialBoard() 
	{
		int[][] squares = new int[][] {
				{0,4,4,4,4,4,4,0},
				{2,0,0,0,0,0,0,2},
				{2,0,0,0,0,0,0,2},
				{2,0,0,0,0,0,0,2},
				{2,0,0,0,0,0,0,2},
				{2,0,0,0,0,0,0,2},
				{2,0,0,0,0,0,0,2},
				{0,4,4,4,4,4,4,0}
			};
		
		Board board = new Board(squares);
		QuadHeuristic heuristic = new QuadHeuristic(board);
		
		assertEquals(2, heuristic.getBlackPawnsValue(), DELTA);
		assertEquals(2, heuristic.getWhitePawnsValue(), DELTA);
	}
	
	@Test
	public void oneWhitePawn() 
	{
		int[][] squares = new int[][] {
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,4,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0}
			};
		
		Board board = new Board(squares);
		QuadHeuristic heuristic = new QuadHeuristic(board);
		
		assertEquals(0, heuristic.getBlackPawnsValue(), DELTA);
		assertEquals(1, heuristic.getWhitePawnsValue(), DELTA);
	}

	@Test
	public void oneBlackPawn() 
	{
		int[][] squares = new int[][] {
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,2,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0}
			};
		
		Board board = new Board(squares);
		QuadHeuristic heuristic = new QuadHeuristic(board);
		
		assertEquals(1, heuristic.getBlackPawnsValue(), DELTA);
		assertEquals(0, heuristic.getWhitePawnsValue(), DELTA);
	}
	
	@Test
	public void verticallyAlignedPawns() 
	{
		int[][] squares = new int[][] {
				{0,0,0,0,0,0,0,0},
				{2,0,0,0,0,0,0,4},
				{2,0,0,0,0,0,0,4},
				{2,0,0,0,0,0,0,4},
				{2,0,0,0,0,0,0,4},
				{2,0,0,0,0,0,0,4},
				{2,0,0,0,0,0,0,4},
				{0,0,0,0,0,0,0,0}
			};
		
		Board board = new Board(squares);
		QuadHeuristic heuristic = new QuadHeuristic(board);
		
		assertEquals(1, heuristic.getBlackPawnsValue(), DELTA);
		assertEquals(1, heuristic.getWhitePawnsValue(), DELTA);
	}
	
	@Test
	public void horizontallyAlignedPawns() 
	{
		int[][] squares = new int[][] {
				{0,0,0,0,0,0,0,0},
				{0,2,2,2,2,2,2,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0},
				{0,4,4,4,4,4,0,0},
				{0,0,0,0,0,0,0,0}
			};
		
		Board board = new Board(squares);
		QuadHeuristic heuristic = new QuadHeuristic(board);
		
		assertEquals(1, heuristic.getBlackPawnsValue(), DELTA);
		assertEquals(1, heuristic.getWhitePawnsValue(), DELTA);
	}
	
	@Test
	public void mixedBoard1() 
	{
		int[][] squares = new int[][] {
				{0,0,0,0,0,0,0,0},
				{0,2,2,2,2,2,2,0},
				{0,0,2,4,0,0,0,0},
				{0,0,4,0,0,4,0,0},
				{0,0,0,2,0,4,4,0},
				{0,0,2,0,2,0,0,0},
				{0,4,4,4,4,4,0,0},
				{0,0,0,0,0,0,0,0}
			};
		
		Board board = new Board(squares);
		QuadHeuristic heuristic = new QuadHeuristic(board);
		
		assertEquals(2, heuristic.getBlackPawnsValue(), DELTA);
		assertEquals(3, heuristic.getWhitePawnsValue(), DELTA);
	}
	
	@Test
	public void updateVerticalMoveWhite() 
	{
		int[][] squares = new int[][] {
				{0,4,4,4,4,4,4,0},
				{2,0,0,0,0,0,0,2},
				{2,0,0,0,0,0,0,2},
				{2,0,0,0,0,0,0,2},
				{2,0,0,0,0,0,0,2},
				{2,0,0,0,0,0,0,2},
				{2,0,0,0,0,0,0,2},
				{0,4,4,4,4,4,4,0}
			};
		
		Board board = new Board(squares);
		QuadHeuristic heuristic = new QuadHeuristic(board);
		
		assertEquals(2, heuristic.getBlackPawnsValue(), DELTA);
		assertEquals(2, heuristic.getWhitePawnsValue(), DELTA);
		
		Move move = new Move(0, 2, 2, 2, 4, 0);
		board.makeMove(move);
		heuristic.update(move);
		
		assertEquals(2, heuristic.getBlackPawnsValue(), DELTA);
		assertEquals(4, heuristic.getWhitePawnsValue(), DELTA);
	}
	
	@Test
	public void updateVerticalMoveBlack() 
	{
		int[][] squares = new int[][] {
				{0,2,2,2,2,2,2,0},
				{4,0,0,0,0,0,0,4},
				{4,0,4,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{0,2,2,2,2,2,2,0}
			};
		
		Board board = new Board(squares);
		QuadHeuristic heuristic = new QuadHeuristic(board);
		
		assertEquals(2, heuristic.getBlackPawnsValue(), DELTA);
		assertEquals(3, heuristic.getWhitePawnsValue(), DELTA);
		
		Move move = new Move(0, 2, 2, 2, 2, 4);
		board.makeMove(move);
		heuristic.update(move);
		
		assertEquals(4, heuristic.getBlackPawnsValue(), DELTA);
		assertEquals(2, heuristic.getWhitePawnsValue(), DELTA);
		
		move.setReversed(true);
		board.unmakeMove(move);
		heuristic.update(move);
		
		assertEquals(2, heuristic.getBlackPawnsValue(), DELTA);
		assertEquals(3, heuristic.getWhitePawnsValue(), DELTA);
	}
	
	@Test
	public void updateVerticalMoveBlack2() 
	{
		int[][] squares = new int[][] {
				{0,2,2,2,2,2,2,0},
				{4,0,0,0,0,0,0,4},
				{4,0,4,2,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{0,2,2,2,2,2,2,0}
			};
		
		Board board = new Board(squares);
		QuadHeuristic heuristic = new QuadHeuristic(board);
		
		assertEquals(3, heuristic.getBlackPawnsValue(), DELTA);
		assertEquals(3, heuristic.getWhitePawnsValue(), DELTA);
		
		Move move = new Move(0, 2, 2, 2, 2, 4);
		board.makeMove(move);
		heuristic.update(move);
		
		assertEquals(4, heuristic.getBlackPawnsValue(), DELTA);
		assertEquals(2, heuristic.getWhitePawnsValue(), DELTA);
		
		move.setReversed(true);
		board.unmakeMove(move);
		heuristic.update(move);
		
		assertEquals(3, heuristic.getBlackPawnsValue(), DELTA);
		assertEquals(3, heuristic.getWhitePawnsValue(), DELTA);
	}
}
