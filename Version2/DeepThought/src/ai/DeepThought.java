package ai;

import java.util.Random;

import game.Board;
import game.CentralizationHeuristic;
import game.Move;
import game.MoveOrderer;
import game.QuadHeuristic;

public class DeepThought implements Runnable {
	// Constants
	private static final int MAX_DEPTH = 6;
	private static final int UNDEFINED_BETA = Integer.MIN_VALUE;
	private static final int UNDEFINED_ALPHA = Integer.MAX_VALUE;
	private static final int OPPONENT_WIN = Integer.MIN_VALUE / 2;
	private static final int DEEPTHOUGHT_WIN = Integer.MAX_VALUE / 2;
	
	// Variables holding the game state
	private Board board;
	private int deepthoughtColor;
	private int opponentColor;
	
	// Heuristics
	private QuadHeuristic quadHeuristic;
	private CentralizationHeuristic centralizationHeuristic;
	private MoveOrderer moveOrderer;
	
	// Variables used in the search
	private int currentMaxDepth;
	private boolean runSearch;
	private boolean lastRunFinished;
	private Move bestMove;
	
	// Constructor
	public DeepThought(Board initialBoard, int playerColor)
	{
		this.board = initialBoard;
		this.deepthoughtColor = playerColor;
		this.opponentColor = (playerColor == 2) ? 4 : 2;
		
		this.quadHeuristic = this.board.getQuadHeuristic();
		this.centralizationHeuristic = this.board.getCentralizationHeuristic();
		this.moveOrderer = new MoveOrderer();
		
		this.runSearch = false;
		this.lastRunFinished = true;
		this.bestMove = null;
	}
	
	@Override
	public void run() {
		try {
			this.searchBestMove();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Searches the best move possible. This method stops when stopSearchBestMove is called.
	public void searchBestMove() throws InterruptedException
	{
		// Waits that the last execution finished to avoid errors
		while(!this.lastRunFinished)
		{
			Thread.sleep(5);
		}
		
		this.lastRunFinished = false;
		this.runSearch = true;
		
		this.bestMove = null;
		
		// Searches the best move possible using iterative deepening
		for(currentMaxDepth = 1; this.runSearch; currentMaxDepth++)
		{
			System.out.println("Depth: " + currentMaxDepth);
			//this.moveOrderer.resetKillerMoves();
			
			Move[] possibleMoves = this.board.getPossibleMoves(this.deepthoughtColor);
			int bestMoveIndex = 0;
			double beta = UNDEFINED_BETA;
			int betaMoveCount = 0;
			
			// Finds the best move at current max depth
			for(int index = 0; index < possibleMoves.length && this.runSearch; index++)
			{
				// Evaluates the move
				this.board.makeMove(possibleMoves[index]);
				double moveValue = this.minimum(beta, 1);
				int moveCount = this.board.getPossibleMoves(this.deepthoughtColor).length;
				this.board.unmakeMove(possibleMoves[index]);
			
				// A winning move has been found, so we stop searching
				if(moveValue == DEEPTHOUGHT_WIN)
				{
					this.bestMove = possibleMoves[index];
					this.lastRunFinished = true;
					return;
				}
				
				// Prioritize moves which havn't moved during the game.
				if(possibleMoves[index].getFromRowIndex() == 0 || possibleMoves[index].getFromRowIndex() == 7 || possibleMoves[index].getFromColumnIndex() == 0 || possibleMoves[index].getFromColumnIndex() == 7)
				{
					this.bestMove = possibleMoves[index];
				}
				
				// Determines if the move is a better move
				if(moveValue > beta)
				{
					beta = moveValue;
					bestMoveIndex = index;
					betaMoveCount = moveCount;
				}
				else if(moveValue == beta && moveCount > betaMoveCount)
				{
					bestMoveIndex = index;
					betaMoveCount = moveCount;
				}
			}
			
			this.bestMove = possibleMoves[bestMoveIndex];
		}
		
		this.lastRunFinished = true;
	}
	
	// Finds the best move for DeepThought
	public double maximum(double alpha, int depth)
	{
		// Stop condition
		double boardValue = this.value();
		
		if(depth == this.currentMaxDepth || boardValue == DEEPTHOUGHT_WIN || boardValue == OPPONENT_WIN)
		{
			return boardValue;
		}
		
		
		Move[] possibleMoves = this.board.getPossibleMoves(this.deepthoughtColor);
		double beta = UNDEFINED_BETA;
		int betaMoveCount = 0;
		
		// Orders moves to maximize early cutoffs
		this.moveOrderer.orderMoves(possibleMoves, depth);
		
		// Searches the best move for DeepThought
		for(int index = 0; index < possibleMoves.length && this.runSearch; index++)
		{
			// Evaluates the move
			this.board.makeMove(possibleMoves[index]);
			double moveValue = this.minimum(beta, depth+1);
			int moveCount = this.board.getPossibleMoves(this.deepthoughtColor).length;
			this.board.unmakeMove(possibleMoves[index]);
			
			// Alpha-Beta prunning
			if(moveValue > alpha)
			{
				this.moveOrderer.signalCutoff(possibleMoves[index], depth);
				return moveValue;
			}
			
			// Determines if the move is better
			if(moveValue > beta && moveValue != UNDEFINED_ALPHA)
			{
				beta = moveValue;
				betaMoveCount = moveCount;
			}
		}
		
		return beta;
	}
	
	// Finds the best move for the opponent
	public double minimum(double beta, int depth)
	{
		// Stop condition
		double boardValue = this.value();
		
		if(depth == this.currentMaxDepth || boardValue == DEEPTHOUGHT_WIN || boardValue == OPPONENT_WIN)
		{
			return boardValue;
		}
		
		Move[] possibleMoves = this.board.getPossibleMoves(this.opponentColor);
		double alpha = UNDEFINED_ALPHA;
		
		// Orders moves to maximize early cutoffs
		this.moveOrderer.orderMoves(possibleMoves, depth);
		
		// Searches the best move for the opponent
		for(int index = 0; index < possibleMoves.length && this.runSearch; index++)
		{
			// Evaluates the move
			this.board.makeMove(possibleMoves[index]);
			double moveValue = this.maximum(alpha, depth+1);
			this.board.unmakeMove(possibleMoves[index]);
			
			// Alpha-Beta prunning
			if(moveValue < beta)
			{
				this.moveOrderer.signalCutoff(possibleMoves[index], depth);
				return moveValue;
			}
			
			// Determines if the move is better
			if(moveValue < alpha && moveValue != UNDEFINED_BETA)
			{
				alpha = moveValue;
			}
		}
		
		return alpha;
	}
	
	// Calculates the value of the game state. A higher value means the board is advantageous for the AI and a lower value means
	// the board is advantageous for the opponent
	public double value()
	{
		float blackPawnsCentralizationValue = this.centralizationHeuristic.getBlackPawnsValue();
		float whitePawnsCentralizationValue = this.centralizationHeuristic.getWhitePawnsValue();
		float blackPawnsEulerNumber = this.quadHeuristic.getBlackPawnsValue();
		float whitePawnsEulerNumber = this.quadHeuristic.getWhitePawnsValue();
		
		// Determines if there's a win. A win is only possible if the euler number is lower or equals than 1.
		if(blackPawnsEulerNumber <= 1 && this.board.isWinning(Board.BLACK_PLAYER))
		{
			if(this.deepthoughtColor == Board.BLACK_PLAYER)
			{
				return DEEPTHOUGHT_WIN;
			}
			else
			{
				return OPPONENT_WIN;
			}
		}
		
		// Determines if there's a win. A win is only possible if the euler number is lower or equals than 1.
		if(whitePawnsEulerNumber <= 1 && this.board.isWinning(Board.WHITE_PLAYER))
		{
			if(this.deepthoughtColor == Board.WHITE_PLAYER)
			{
				return DEEPTHOUGHT_WIN;
			}
			else
			{
				return OPPONENT_WIN;
			}
		}
		
		// Calculates the board value
		if(this.deepthoughtColor == Board.BLACK_PLAYER)
		{
			return whitePawnsEulerNumber - blackPawnsEulerNumber + blackPawnsCentralizationValue - whitePawnsCentralizationValue;
		}
		else
		{
			return blackPawnsEulerNumber - whitePawnsEulerNumber + whitePawnsCentralizationValue - blackPawnsCentralizationValue;
		}
	}
	
	// Terminates the best move search
	public void stopSearchBestMove()
	{
		this.runSearch = false;
	}
	
	// Gets the best move found
	public Move getBestMoveFound()
	{
		return this.bestMove;
	}
}
