package test;

import static org.junit.Assert.*;
import game.Board;
import game.QuadHeuristic;

import org.junit.Test;

public class BoardTest {

	@Test
	public void initialBoard() 
	{
		int[][] squares = new int[][] {
				{0,4,4,4,4,4,4,0},
				{0,2,0,0,0,0,0,0},
				{0,2,0,0,0,0,0,0},
				{0,0,2,0,0,0,0,0},
				{2,0,0,2,0,0,0,0},
				{2,2,0,0,2,0,0,0},
				{2,2,2,0,0,0,0,0},
				{2,4,4,4,4,4,4,0}
			};
		
		Board board = new Board(squares);
		
		assertFalse(board.isWinning(4));
		assertTrue(board.isWinning(2));
	}
}
