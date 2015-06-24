package game;


public class QuadHeuristic
{
	private Board board;
	
	private float whiteEulerNumber;
	private float blackEulerNumber;
	
	public QuadHeuristic(Board board)
	{
		this.board = board;
		
		this.calculate();
	}
	
	public void update(Move move)
	{
		int[][] squares = this.board.getSquares();
		
		int tempFromValue = squares[move.getFromRowIndex()][move.getFromColumnIndex()];
		int tempToValue = squares[move.getToRowIndex()][move.getToColumnIndex()];
		
		float fromValueBefore = 0;
		float toValueBefore = 0;
		float toValueAfter = 0;
		
		// Calculates quads value before moving the pawn
		squares[move.getFromRowIndex()][move.getFromColumnIndex()] = move.getOverwrittenFromSquare();
		squares[move.getToRowIndex()][move.getToColumnIndex()] = move.getOverwrittenToSquare();
		
		fromValueBefore = this.getPawnQuadsValue(move.getFromRowIndex(), move.getFromColumnIndex(), move.getOverwrittenFromSquare(), squares);
		toValueBefore = this.getPawnQuadsValue(move.getToRowIndex(), move.getToColumnIndex(), move.getOverwrittenToSquare(), squares);
		toValueAfter = this.getPawnQuadsValue(move.getToRowIndex(), move.getToColumnIndex(), move.getOverwrittenFromSquare(), squares);
		
		// Resets squares value
		squares[move.getFromRowIndex()][move.getFromColumnIndex()] = tempFromValue;
		squares[move.getToRowIndex()][move.getToColumnIndex()] = tempToValue;
		
		// Changes euler number values
		if(move.isReversed())
		{
			fromValueBefore *= -1;
			toValueBefore *= -1;
		}
		else
		{
			toValueAfter *= -1;
		}
		
		if(move.getOverwrittenFromSquare() == Board.BLACK_PLAYER)
		{
			this.blackEulerNumber -= fromValueBefore + toValueAfter;
			this.whiteEulerNumber -= toValueBefore;
		}
		else
		{
			this.whiteEulerNumber -= fromValueBefore + toValueAfter;
			this.blackEulerNumber -= toValueBefore;
		}
	}
	
	private float getPawnQuadsValue(int rowIndex, int columnIndex, int playerColor, int[][] squares)
	{	
		if(playerColor == 0)
		{
			return 0;
		}
		
		boolean topLeftQuadHasElements = false;
		boolean topRightQuadHasElements = false;
		boolean bottomLeftQuadHasElements = false;
		boolean bottomRightQuadHasElements = false;
		
		int lines = 0;
		
		if(rowIndex != 0 && squares[rowIndex-1][columnIndex] == playerColor)
		{
			bottomLeftQuadHasElements = true;
			bottomRightQuadHasElements = true;
		}
		else
		{
			lines += 2;
		}
		
		if(rowIndex != 7 && squares[rowIndex+1][columnIndex] == playerColor)
		{
			topLeftQuadHasElements = true;
			topRightQuadHasElements = true;
		}
		else
		{
			lines += 2;
		}
		
		if(columnIndex != 0 && squares[rowIndex][columnIndex-1] == playerColor)
		{
			topLeftQuadHasElements = true;
			bottomLeftQuadHasElements = true;
		}
		else
		{
			lines += 2;
		}
		
		if(columnIndex != 7 && squares[rowIndex][columnIndex+1] == playerColor)
		{
			topRightQuadHasElements = true;
			bottomRightQuadHasElements = true;
		}
		else
		{
			lines += 2;
		}
		
		if(rowIndex != 0 && columnIndex != 0 && squares[rowIndex-1][columnIndex-1] == playerColor)
		{
			bottomLeftQuadHasElements = true;
		}
		
		if(rowIndex != 0 && columnIndex != 7 && squares[rowIndex-1][columnIndex+1] == playerColor)
		{
			bottomRightQuadHasElements = true;
		}
		
		if(rowIndex != 7 && columnIndex != 0 && squares[rowIndex+1][columnIndex-1] == playerColor)
		{
			topLeftQuadHasElements = true;
		}
		
		if(rowIndex != 7 && columnIndex != 7 && squares[rowIndex+1][columnIndex+1] == playerColor)
		{
			topRightQuadHasElements = true;
		}
		
		int value = 0;
		
		if(!bottomLeftQuadHasElements) value++;
		if(!bottomRightQuadHasElements) value++;
		if(!topLeftQuadHasElements) value++;
		if(!topRightQuadHasElements) value++;
		
		return value - lines * 0.5f + 1;
	}
	
	public void calculate()
	{
		this.blackEulerNumber = 0;
		this.whiteEulerNumber = 0;
		
		int[][] squares = this.board.getSquares();
		
		// Iterates through all quads
		for(int rowIndex = 0; rowIndex <= 8; rowIndex++)
		{
			for(int columnIndex = 0; columnIndex <= 8; columnIndex++)
			{
				int[][] quad = new int[2][2];
				
				// Determines if the element is within the board bounds
				if(rowIndex >= 0 && rowIndex <= 7)
				{
					if(columnIndex >= 0 && columnIndex <= 7)
					{
						quad[0][0] = squares[rowIndex][columnIndex];
					}
					
					if(columnIndex - 1 >= 0 && columnIndex - 1 <= 7)
					{
						quad[0][1] = squares[rowIndex][columnIndex-1];
					}
				}
				
				// Determines if the element is within the board bounds
				if(rowIndex - 1 >= 0 && rowIndex - 1 <= 7)
				{
					if(columnIndex >= 0 && columnIndex <= 7)
					{
						quad[1][0] = squares[rowIndex-1][columnIndex];
					}
					
					if(columnIndex - 1 >= 0 && columnIndex - 1 <= 7)
					{
						quad[1][1] = squares[rowIndex-1][columnIndex-1];
					}
				}
				
				// Counts pawns
				int blackPawnsCount = 0;
				int whitePawnsCount = 0;
				
				for(int quadRowIndex = 0; quadRowIndex < 2; quadRowIndex++)
				{
					for(int quadColumnIndex = 0; quadColumnIndex < 2; quadColumnIndex++)
					{
						if(quad[quadRowIndex][quadColumnIndex] == Board.BLACK_PLAYER)
						{
							blackPawnsCount++;
						}
						else if(quad[quadRowIndex][quadColumnIndex] == Board.WHITE_PLAYER)
						{
							whitePawnsCount++;
						}
					}
				}
				
				// Calculates black pawns score
				if(blackPawnsCount == 1)
				{
					this.blackEulerNumber += 0.25;
				}
				
				if(blackPawnsCount == 2 &&
					((quad[0][0] == Board.BLACK_PLAYER && quad[0][0] == quad[1][1]) || 
					(quad[0][1] == Board.BLACK_PLAYER && quad[0][1] == quad[1][0])))
				{
					this.blackEulerNumber -= 0.5;
				}
				
				if(blackPawnsCount == 3)
				{
					this.blackEulerNumber -= 0.25;
				}
				
				// Calculates white pawns score
				if(whitePawnsCount == 1)
				{
					this.whiteEulerNumber += 0.25;
				}
				
				if(whitePawnsCount == 2 &&
					((quad[0][0] == Board.WHITE_PLAYER && quad[0][0] == quad[1][1]) || 
					(quad[0][1] == Board.WHITE_PLAYER && quad[0][1] == quad[1][0])))
				{
					this.whiteEulerNumber -= 0.5;
				}
				
				if(whitePawnsCount == 3)
				{
					this.whiteEulerNumber -= 0.25;
				}
			}
		}
	}
	
	public float getWhitePawnsValue()
	{
		return this.whiteEulerNumber;
	}
	
	public float getBlackPawnsValue()
	{
		return this.blackEulerNumber;
	}
}
