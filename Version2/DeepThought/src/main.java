import ai.DeepThought;
import game.Board;
import game.Move;
import game.QuadHeuristic;


public class main {
	
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		int[][] squares = new int[][] {
				{0,2,2,2,2,2,2,0},
				{4,0,0,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{0,2,2,2,2,2,2,0},
		};
		
		Board board = new Board(squares);
		DeepThought ai = new DeepThought(board, 2);
		
		for(int i=0; i < 2; i++)
		{
		long startNano = System.nanoTime();
		
		//Move move = ai.findBestMove();
		
		long endNano = System.nanoTime();
		
		System.out.println("Took : " + (double)(endNano - startNano) / 1000000000.0);
		}
		
		/*
		int[][] squares = new int[][] {
				{0,2,2,2,2,2,2,0},
				{4,0,0,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{4,0,0,0,0,0,0,4},
				{0,2,2,2,2,2,2,0}
		};
		
		Board board = new Board(squares);
		QuadHeuristic qh = new QuadHeuristic(board);
		
		Move move = new Move(1,0,2,2);
		
		long startNano = System.nanoTime();
		for(int i=0; i < 10000000; i++)
		{
			qh.update(move);
		}
		long endNano = System.nanoTime();
		System.out.println("Took : " + (double)(endNano - startNano) / 1000000000.0);
		*/
	}
}
