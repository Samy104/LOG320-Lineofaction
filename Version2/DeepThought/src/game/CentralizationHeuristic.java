package game;


public class CentralizationHeuristic
{
	private Board board;
	
	private int whitePawnsValue;
	private int blackPawnsValue;
	
	public CentralizationHeuristic(Board board)
	{
		this.board = board;
		
		this.calculate();
	}
	
	public void update(Move move)
	{
		int[][] squares = board.getSquares();
		
		if(!move.isReversed())
		{
			if(move.getOverwrittenFromSquare() == Board.BLACK_PLAYER)
			{
				this.blackPawnsValue -= this.getCentralizationValue(move.getFromRowIndex(), move.getFromColumnIndex());
				this.blackPawnsValue += this.getCentralizationValue(move.getToRowIndex(), move.getToColumnIndex());
				
				if(move.getOverwrittenToSquare() != Board.BLANK_SQUARE)
				{
					this.whitePawnsValue -= this.getCentralizationValue(move.getToRowIndex(), move.getToColumnIndex());
				}
			}
			else if(move.getOverwrittenFromSquare() == Board.WHITE_PLAYER)
			{
				this.whitePawnsValue -= this.getCentralizationValue(move.getFromRowIndex(), move.getFromColumnIndex());
				this.whitePawnsValue += this.getCentralizationValue(move.getToRowIndex(), move.getToColumnIndex());
				
				if(move.getOverwrittenToSquare() != Board.BLANK_SQUARE)
				{
					this.blackPawnsValue -= this.getCentralizationValue(move.getToRowIndex(), move.getToColumnIndex());
				}
			}
		}
		else
		{
			if(move.getOverwrittenFromSquare() == Board.BLACK_PLAYER)
			{
				this.blackPawnsValue += this.getCentralizationValue(move.getFromRowIndex(), move.getFromColumnIndex());
				this.blackPawnsValue -= this.getCentralizationValue(move.getToRowIndex(), move.getToColumnIndex());
				
				if(move.getOverwrittenToSquare() != Board.BLANK_SQUARE)
				{
					this.whitePawnsValue += this.getCentralizationValue(move.getToRowIndex(), move.getToColumnIndex());
				}
			}
			else if(move.getOverwrittenFromSquare() == Board.WHITE_PLAYER)
			{
				this.whitePawnsValue += this.getCentralizationValue(move.getFromRowIndex(), move.getFromColumnIndex());
				this.whitePawnsValue -= this.getCentralizationValue(move.getToRowIndex(), move.getToColumnIndex());
				
				if(move.getOverwrittenToSquare() != Board.BLANK_SQUARE)
				{
					this.blackPawnsValue += this.getCentralizationValue(move.getToRowIndex(), move.getToColumnIndex());
				}
			}
		}
	}
	
	private int getCentralizationValue(int rowIndex, int columnIndex)
	{
		int min = Math.min(rowIndex, columnIndex);
		int max = Math.max(rowIndex, columnIndex);
		return Math.min(((min > 3)?7-min:min), (max > 3)?7-max:max);
	}
	
	private void calculate()
	{
		int[][] squares = this.board.getSquares();
		
		for(int rowIndex = 0; rowIndex < 8; rowIndex++)
		{
			for(int columnIndex = 0; columnIndex < 8; columnIndex++)
			{
				if(squares[rowIndex][columnIndex] == Board.BLACK_PLAYER)
				{
					this.blackPawnsValue += this.getCentralizationValue(rowIndex, columnIndex);
				}
				else if(squares[rowIndex][columnIndex] == Board.WHITE_PLAYER)
				{
					this.whitePawnsValue += this.getCentralizationValue(rowIndex, columnIndex);
				}
			}
		}
	}
	
	public int getWhitePawnsValue()
	{
		return this.whitePawnsValue;
	}
	
	public int getBlackPawnsValue()
	{
		return this.blackPawnsValue;
	}
}
