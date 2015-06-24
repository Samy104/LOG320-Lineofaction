package game;

import java.util.Arrays;

public class Board
{
	// Constants
	private static final int BOARD_SQUARES_COUNT = 64;
	private static final int ROW_SQUARES_COUNT = 8;
	private static final int COLUMN_SQUARES_COUNT = 8;
	
	private static final char BLACK_PLAYER = '2';
	private static final char WHITE_PLAYER = '4';
	private static final char BLANK_SQUARE = '0';
	
	// Stores the board state
	private int[][] squares;
	
	// Stores pawns count to avoid recomputation
	private int[] rowPawnsCount;
	private int[] columnPawnsCount;
	private int[] forwardBottomDiagonalPawnsCount;
	private int[] forwardTopDiagonalPawnsCount;	
	
	private Move[] possibleMovesBuffer;
	
	// Creates a board from its string representation
	public static Board createBoard(String squaresAsString)
	{
		Board board = null;
		
		if(squaresAsString != null)
		{
			String[] squaresStrings = squaresAsString.split(" ");

			if(squaresStrings.length == BOARD_SQUARES_COUNT)
			{
				int[][] squares = new int[ROW_SQUARES_COUNT][COLUMN_SQUARES_COUNT];
				boolean validBoard = true;
				
				for(int squareIndex = 0; squareIndex < BOARD_SQUARES_COUNT && validBoard; squareIndex++)
				{
					String squareString = squaresStrings[squareIndex];
					
					if(squareString.length() == 1 && 
						(squareString.charAt(0) != BLACK_PLAYER || squareString.charAt(0) != WHITE_PLAYER || squareString.charAt(0) != BLANK_SQUARE))
					{
						squares[squareIndex / COLUMN_SQUARES_COUNT][squareIndex % COLUMN_SQUARES_COUNT] = Character.getNumericValue(squareString.charAt(0));
					}
					else
					{
						validBoard = false;
						System.err.println("Error, invalid square!");
					}
				}
				
				if(validBoard)
				{
					board = new Board(squares);
					board.calculatePawnsCount();
				}
			}
			else
			{
				System.err.println("Error, squares string has an invalid number of squares!");
			}
		}
		
		return board;
	}
	
	// Constructor
	public Board(int[][] squares)
	{
		this.squares = squares;
		this.possibleMovesBuffer = new Move[112];
		
		this.calculatePawnsCount();
	}
	
	// Executes a move
	public void executeMove(Move move)
	{
		// Remembers the square that is overwrited in case we want to undo the move
		move.setOverwritedSquare(this.squares[move.getToRowIndex()][move.getToColumnIndex()]);
		
		// Moves the pawn
		this.squares[move.getToRowIndex()][move.getToColumnIndex()] = this.squares[move.getFromRowIndex()][move.getFromColumnIndex()];
		this.squares[move.getFromRowIndex()][move.getFromColumnIndex()] = 0;
		
		
		// Updates all pawns count variables
		this.rowPawnsCount[move.getFromRowIndex()]--;
		this.columnPawnsCount[move.getFromColumnIndex()]--;
		this.forwardTopDiagonalPawnsCount[move.getFromColumnIndex() + 7 - move.getFromRowIndex()]--;
		this.forwardBottomDiagonalPawnsCount[move.getFromRowIndex() + move.getFromColumnIndex()]--;
		
		// Pawns count of the destination will only change if a pawn wasn't already there
		if(move.getOverwritedSquare() == 0)
		{
			this.rowPawnsCount[move.getToRowIndex()]++;
			this.columnPawnsCount[move.getToColumnIndex()]++;
			this.forwardTopDiagonalPawnsCount[move.getToColumnIndex() + 7 - move.getToRowIndex()]++;
			this.forwardBottomDiagonalPawnsCount[move.getToRowIndex() + move.getToColumnIndex()]++;
		}
	}
	
	// Undo a move
	public void undoMove(Move move)
	{
		// Moves the pawn back and restores the square that was overwrited
		this.squares[move.getFromRowIndex()][move.getFromColumnIndex()] = this.squares[move.getToRowIndex()][move.getToColumnIndex()];
		this.squares[move.getToRowIndex()][move.getToColumnIndex()] = move.getOverwritedSquare();
		
		
		// Updates all pawns count variables
		this.rowPawnsCount[move.getFromRowIndex()]++;
		this.columnPawnsCount[move.getFromColumnIndex()]++;
		this.forwardTopDiagonalPawnsCount[move.getFromColumnIndex() + 7 - move.getFromRowIndex()]++;
		this.forwardBottomDiagonalPawnsCount[move.getFromRowIndex() + move.getFromColumnIndex()]++;
		
		// Pawns count of the destination will only change if a pawn wasn't already there
		if(move.getOverwritedSquare() == 0)
		{
			this.rowPawnsCount[move.getToRowIndex()]--;
			this.columnPawnsCount[move.getToColumnIndex()]--;
			this.forwardTopDiagonalPawnsCount[move.getToColumnIndex() + 7 - move.getToRowIndex()]--;
			this.forwardBottomDiagonalPawnsCount[move.getToRowIndex() + move.getToColumnIndex()]--;
		}
	}
	
	// Returns all valid moves for a given player
	public Move[] getPossibleMoves(int playerColor)
	{
		int moveCount = 0;
		
		int opponentColor = (playerColor == 2) ? 4 : 2;
		boolean isValidMove = true;
		
		int toColumnIndex = 0;
		int toRowIndex = 0;
		
		for(int rowIndex = 0; rowIndex < 8; rowIndex++)
		{
			for(int columnIndex = 0; columnIndex < 8; columnIndex++)
			{
				if(this.squares[rowIndex][columnIndex] == playerColor)
				{
					// Horizontal moves
					int rowPawnsCount = this.countRowPawns(rowIndex);
					
					toColumnIndex = columnIndex - rowPawnsCount;
					
					if(toColumnIndex >= 0 && this.squares[rowIndex][toColumnIndex] != playerColor)
					{
						isValidMove = true;
						
						for(int indexIncrement = 1; indexIncrement < rowPawnsCount; indexIncrement++)
						{
							if(this.squares[rowIndex][columnIndex - indexIncrement] == opponentColor)
							{
								isValidMove = false;
								break;
							}
						}
						
						if(isValidMove)
						{
							this.possibleMovesBuffer[moveCount] = new Move(rowIndex, columnIndex, rowIndex, toColumnIndex, this.squares[rowIndex][toColumnIndex]);
							moveCount++;
						}
					}
					
					toColumnIndex = columnIndex + rowPawnsCount;
					
					if(toColumnIndex <= 7 && this.squares[rowIndex][toColumnIndex] != playerColor)
					{
						isValidMove = true;
						
						for(int indexIncrement = 1; indexIncrement < rowPawnsCount; indexIncrement++)
						{
							if(this.squares[rowIndex][columnIndex + indexIncrement] == opponentColor)
							{
								isValidMove = false;
								break;
							}
						}
						
						if(isValidMove)
						{
							this.possibleMovesBuffer[moveCount] = new Move(rowIndex, columnIndex, rowIndex, toColumnIndex, this.squares[rowIndex][toColumnIndex]);
							moveCount++;
						}
					}
					
					// Vertical moves
					int columnPawnsCount = this.countColumnPawns(columnIndex);
					
					toRowIndex = rowIndex - columnPawnsCount;
					
					if(toRowIndex >= 0 && this.squares[toRowIndex][columnIndex] != playerColor)
					{
						isValidMove = true;
						
						for(int indexIncrement = 1; indexIncrement < columnPawnsCount; indexIncrement++)
						{
							if(this.squares[rowIndex - indexIncrement][columnIndex] == opponentColor)
							{
								isValidMove = false;
								break;
							}
						}
						
						if(isValidMove)
						{
							this.possibleMovesBuffer[moveCount] = new Move(rowIndex, columnIndex, toRowIndex, columnIndex, this.squares[toRowIndex][columnIndex]);
							moveCount++;
						}
					}
					
					toRowIndex = rowIndex + columnPawnsCount;
					
					if(toRowIndex <= 7 && this.squares[toRowIndex][columnIndex] != playerColor)
					{
						isValidMove = true;
						
						for(int indexIncrement = 1; indexIncrement < columnPawnsCount; indexIncrement++)
						{
							if(this.squares[rowIndex + indexIncrement][columnIndex] == opponentColor)
							{
								isValidMove = false;
								break;
							}
						}
						
						if(isValidMove)
						{
							this.possibleMovesBuffer[moveCount] = new Move(rowIndex, columnIndex, toRowIndex, columnIndex, this.squares[toRowIndex][columnIndex]);
							moveCount++;
						}
					}
					
					// Forward Top diagonal moves
					int topDiagonalPawnsCount = this.countForwardTopDiagonalPawns(rowIndex, columnIndex);
					
					toRowIndex = rowIndex - topDiagonalPawnsCount;
					toColumnIndex = columnIndex - topDiagonalPawnsCount;
					
					if(toRowIndex >= 0 && toColumnIndex >= 0 && this.squares[toRowIndex][toColumnIndex] != playerColor)
					{
						isValidMove = true;
						
						for(int indexIncrement = 1; indexIncrement < topDiagonalPawnsCount; indexIncrement++)
						{
							if(this.squares[rowIndex - indexIncrement][columnIndex - indexIncrement] == opponentColor)
							{
								isValidMove = false;
								break;
							}
						}
						
						if(isValidMove)
						{
							this.possibleMovesBuffer[moveCount] = new Move(rowIndex, columnIndex, toRowIndex, toColumnIndex, this.squares[toRowIndex][toColumnIndex]);
							moveCount++;
						}
					}
					
					toRowIndex = rowIndex + topDiagonalPawnsCount;
					toColumnIndex = columnIndex + topDiagonalPawnsCount;
					
					if(toRowIndex <= 7 && toColumnIndex <= 7 && this.squares[toRowIndex][toColumnIndex] != playerColor)
					{
						isValidMove = true;
						
						for(int indexIncrement = 1; indexIncrement < topDiagonalPawnsCount; indexIncrement++)
						{
							if(this.squares[rowIndex + indexIncrement][columnIndex + indexIncrement] == opponentColor)
							{
								isValidMove = false;
								break;
							}
						}
						
						if(isValidMove)
						{
							this.possibleMovesBuffer[moveCount] = new Move(rowIndex, columnIndex, toRowIndex, toColumnIndex, this.squares[toRowIndex][toColumnIndex]);
							moveCount++;
						}
					}
					
					// Forward Bottom diagonal moves
					int bottomDiagonalPawnsCount = this.countForwardBottomDiagonalPawns(rowIndex, columnIndex);
					
					toRowIndex = rowIndex + bottomDiagonalPawnsCount;
					toColumnIndex = columnIndex - bottomDiagonalPawnsCount;
					
					if(toRowIndex <= 7 && toColumnIndex >= 0 && this.squares[toRowIndex][toColumnIndex] != playerColor)
					{
						isValidMove = true;
						
						for(int indexIncrement = 1; indexIncrement < bottomDiagonalPawnsCount; indexIncrement++)
						{
							if(this.squares[rowIndex + indexIncrement][columnIndex - indexIncrement] == opponentColor)
							{
								isValidMove = false;
								break;
							}
						}
						
						if(isValidMove)
						{
							this.possibleMovesBuffer[moveCount] = new Move(rowIndex, columnIndex, toRowIndex, toColumnIndex, this.squares[toRowIndex][toColumnIndex]);
							moveCount++;
						}
					}
					
					toRowIndex = rowIndex - bottomDiagonalPawnsCount;
					toColumnIndex = columnIndex + bottomDiagonalPawnsCount;
					
					if(toRowIndex >= 0 && toColumnIndex <= 7 && this.squares[toRowIndex][toColumnIndex] != playerColor)
					{
						isValidMove = true;
						
						for(int indexIncrement = 1; indexIncrement < bottomDiagonalPawnsCount; indexIncrement++)
						{
							if(this.squares[rowIndex - indexIncrement][columnIndex + indexIncrement] == opponentColor)
							{
								isValidMove = false;
								break;
							}
						}
						
						if(isValidMove)
						{
							this.possibleMovesBuffer[moveCount] = new Move(rowIndex, columnIndex, toRowIndex, toColumnIndex, this.squares[toRowIndex][toColumnIndex]);
							moveCount++;
						}
					}
				}
			}
		}
		
		return Arrays.copyOf(this.possibleMovesBuffer, moveCount);
	}
	
	// Returns the squares of the board
	public int[][] getSquares()
	{
		return this.squares;
	}
	
	// Counts the number of pawns in the row
	public int countRowPawns(int rowIndex)
	{
		return this.rowPawnsCount[rowIndex];
	}
	
	// Counts the number of pawns in the column
	public int countColumnPawns(int columnIndex)
	{
		return this.columnPawnsCount[columnIndex];
	}
	
	// Counts the number of pawns in the top diagonal
	public int countForwardTopDiagonalPawns(int rowIndex, int columnIndex)
	{
		return this.forwardTopDiagonalPawnsCount[columnIndex + 7 - rowIndex];
	}
	
	// Counts the number of pawns in the bottom diagonal
	public int countForwardBottomDiagonalPawns(int rowIndex, int columnIndex)
	{
		return this.forwardBottomDiagonalPawnsCount[rowIndex + columnIndex];
	}
	
	// Precalculates the number of pawns in a row, column, top diagonal and bottom diagonal
	private void calculatePawnsCount()
	{
		this.rowPawnsCount = new int[8];
		this.columnPawnsCount = new int[8];
		this.forwardBottomDiagonalPawnsCount = new int[15];
		this.forwardTopDiagonalPawnsCount = new int[15];
		
		for(int rowIndex = 0; rowIndex < 8; rowIndex++)
		{
			for(int columnIndex = 0; columnIndex < 8; columnIndex++)
			{
				// A pawn is at this position
				if(this.squares[rowIndex][columnIndex] != 0)
				{
					this.rowPawnsCount[rowIndex]++;
					this.columnPawnsCount[columnIndex]++;
					this.forwardBottomDiagonalPawnsCount[rowIndex + columnIndex]++;
					this.forwardTopDiagonalPawnsCount[columnIndex + 7 - rowIndex]++;
				}
			}
		}
	}	
	
	// Prints the board in the console
	public void printIntoConsole()
	{
		for(int index = 0; index < 8*2 + 3; index++)
		{
			System.out.print("*");
		}
		
		System.out.println();
		
		for(int rowIndex = 7; rowIndex >= 0; rowIndex--)
		{
			System.out.print("* ");
			for(int columnIndex = 0; columnIndex <= 7; columnIndex++)
			{
				System.out.print(Integer.toString(this.squares[rowIndex][columnIndex]) + " ");
			}
			
			System.out.println("*");
		}
		
		for(int index = 0; index < 8*2  + 3; index++)
		{
			System.out.print("*");
		}
		
		System.out.println();
	}
}
