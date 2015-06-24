package ai;

import game.Board;
import game.Move;

public class DeepThought {
	// Constants
	private static final int MAX_DEPTH = 1000;
	private static final int UNDEFINED_BETA = Integer.MIN_VALUE;
	private static final int UNDEFINED_ALPHA = Integer.MAX_VALUE;
	private static final int OPPONENT_WIN = -3000000;
	private static final int DEEPTHOUGHT_WIN = 3000000;
	private static final int OPTIMAL_ROW = 4;
	private static final int OPTIMAL_COL = 4;
	
	// Attributes
	private Board board;
	private int deepthoughtColor;
	private int opponentColor;
	private long timeAllocated;
	private int baseOffset = 2;
	private int additionalOffset = 3;
	
	public DeepThought(Board initialBoard, int playerColor)
	{
		this.board = initialBoard;
		this.deepthoughtColor = playerColor;
		this.opponentColor = (playerColor == 2) ? 4 : 2;
	}
	
	// Finds the best move to play next
	public Move findBestMove(long time)
	{
		this.timeAllocated = time;
		int beta = UNDEFINED_BETA;
		int bestMoveIndex = 0;
		
		Move[] possibleMoves = this.board.getPossibleMoves(this.deepthoughtColor);
		
		for(int index = 0; index < possibleMoves.length; index++)
		{
			if(getMovePriority(possibleMoves[index]))
			{
				return possibleMoves[index];
			}
			
			this.board.executeMove(possibleMoves[index]);
			int moveValue = this.minimum(beta, 1);
			this.board.undoMove(possibleMoves[index]);
		
			if(moveValue > beta)
			{
				beta = moveValue;
				bestMoveIndex = index;
			}
		}
		
		return possibleMoves[bestMoveIndex];
	}
	
	// Finds the best move for the ai
	public int maximum(int alpha, int depth)
	{
		int boardValue = this.value();
		
		if(depth == MAX_DEPTH || boardValue == DEEPTHOUGHT_WIN || boardValue == OPPONENT_WIN || System.nanoTime() >= this.timeAllocated)
		{
			return boardValue;
		}
		
		Move[] possibleMoves = this.board.getPossibleMoves(this.deepthoughtColor);
		int beta = UNDEFINED_BETA;
		
		for(int index = 0; index < possibleMoves.length; index++)
		{
			this.board.executeMove(possibleMoves[index]);
			int moveValue = this.minimum(beta, depth+1);
			this.board.undoMove(possibleMoves[index]);
			
			if(moveValue > alpha)
			{
				return moveValue;
			}
			
			if(moveValue > beta && moveValue != UNDEFINED_ALPHA)
			{
				beta = moveValue;
			}
		}
		
		return beta;
	}
	
	// Finds the best move for the opponent
	public int minimum(int beta, int depth)
	{
		int boardValue = this.value();
		
		if(depth == MAX_DEPTH || boardValue == DEEPTHOUGHT_WIN || boardValue == OPPONENT_WIN || System.nanoTime() >= this.timeAllocated)
		{
			return boardValue;
		}
		
		Move[] possibleMoves = this.board.getPossibleMoves(this.opponentColor);
		int alpha = UNDEFINED_ALPHA;
		
		for(int index = 0; index < possibleMoves.length; index++)
		{
			this.board.executeMove(possibleMoves[index]);
			int moveValue = this.maximum(alpha, depth+1);
			this.board.undoMove(possibleMoves[index]);
			
			if(moveValue < beta)
			{
				return moveValue;
			}
			
			if(moveValue < alpha && moveValue != UNDEFINED_BETA)
			{
				alpha = moveValue;
			}
		}
		
		return alpha;
	}
	
	// Calculates the value of the game state. A higher value means the board is advantageous for the AI and a lower value means
	// the board is advantageous for the opponent
	public int value()
	{
		int boardValue = 0;
		int adjacentFriendlyCount = 0;
		int friendlyPawnCount = 0;
		int adjacentEnnemyCount = 0;
		int fiendPawnCount = 0;
		int[][] board = this.board.getSquares();
				
		for(int rowIndex = 0; rowIndex < 8; rowIndex++)
		{
			for(int colIndex = 0; colIndex < 8; colIndex++)
			{
				if (board[rowIndex][colIndex] == this.deepthoughtColor)
				{
					friendlyPawnCount++;
					int minIndex = (rowIndex > colIndex) ? colIndex : rowIndex;
					int maxIndex = (rowIndex > colIndex) ? rowIndex : colIndex;
					if(rowIndex < 4 || colIndex < 4)
					{
						boardValue += (minIndex + 1);
					}
					else if(rowIndex > 3 || colIndex > 3)
					{
						boardValue -= (8 - maxIndex);
					}
					for(int rowDecal = -1; rowDecal < 2; rowDecal++)
					{
						for(int colDecal = -1; colDecal < 2; colDecal++)
						{
							int adjacentRow = rowIndex + rowDecal;
							int adjacentCol = colIndex + colDecal;
							if(adjacentRow >= 0 && adjacentRow < 8 && adjacentCol >= 0 && adjacentCol < 8)
							{
								adjacentFriendlyCount += (board[adjacentRow][adjacentCol] == this.deepthoughtColor) ? 1 : 0;
							}
						}
					}
					
				}
				else if (board[rowIndex][colIndex] == this.opponentColor)
				{
					fiendPawnCount++;
					int minIndex = (rowIndex > colIndex) ? colIndex : rowIndex;
					int maxIndex = (rowIndex > colIndex) ? rowIndex : colIndex;
					if(rowIndex < 4 || colIndex < 4)
					{
						boardValue += (minIndex + 1);
					}
					else if(rowIndex > 3 || colIndex > 3)
					{
						boardValue -= (8 - maxIndex);
					}
					for(int rowDecal = -1; rowDecal < 2; rowDecal++)
					{
						for(int colDecal = -1; colDecal < 2; colDecal++)
						{
							int adjacentRow = rowIndex + rowDecal;
							int adjacentCol = colIndex + colDecal;
							if(adjacentRow >= 0 && adjacentRow < 8 && adjacentCol >= 0 && adjacentCol < 8)
							{
								adjacentEnnemyCount += (board[adjacentRow][adjacentCol] == this.deepthoughtColor) ? 1 : 0;
							}
						}
					}
				}
			}
		}
		int friendlyOffset = baseOffset;
		int ennemyOffset = baseOffset;
		if(additionalOffset >= 0) 
		{
			friendlyOffset += additionalOffset;
		}
		else
		{
			ennemyOffset -= additionalOffset;
		}
		friendlyPawnCount += 1;
		fiendPawnCount += 1;
		return 4*boardValue + friendlyOffset*(adjacentFriendlyCount/friendlyPawnCount) - ennemyOffset*(adjacentEnnemyCount/fiendPawnCount);
	}
	
	// We must give priority to moves that make pawns leave the edge. Then give priority to those further away from the optimal position.
	public boolean getMovePriority(Move lastMove) 
	{
		if((lastMove.getFromRowIndex() == 0 || lastMove.getFromRowIndex() == 8) && (lastMove.getFromColumnIndex() == 0 || lastMove.getFromColumnIndex() == 8))
		{
			return true;
		}
		
		return false;
	}
}
